package pt.ulisboa.tecnico.socialsoftware.tutor.suggestion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.domain.Suggestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.dto.ListByUsernameDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.dto.SuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.repository.SuggestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class SuggestionService {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private SuggestionRepository suggestionRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SuggestionDto createSuggestion(int courseId, SuggestionDto suggestionDto){
        CourseExecution course = courseExecutionRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId));
        User user = checkIfUserExists(suggestionDto.getStudent().getUsername());

        checkRoleIfStudent(user);

        Set<Topic> topics = checkIfTopicExists(course.getCourse().getId(), suggestionDto);

        if (suggestionDto.getKey() == null) {
            int maxQuestionNumber = suggestionRepository.getMaxSuggestionNumber() != null ?
                    suggestionRepository.getMaxSuggestionNumber() : 0;
            suggestionDto.setKey(maxQuestionNumber + 1);
        }

        Suggestion suggestion = new Suggestion(course, user, suggestionDto);

        user.incrementNumberOfSuggestions();

        suggestion.setCreationDate(LocalDateTime.now());
        suggestion.setTopicsList(topics);
        suggestion.setIsPrivate(suggestionDto.getIsPrivate());

        //new
        checkTitleAndOptions(suggestionDto);
        suggestion.addOptions(suggestionDto);
        suggestion.setTitle(suggestionDto.getTitle());

        topics.forEach(topic -> topic.addSuggestion(suggestion));

        entityManager.persist(suggestion);

        return new SuggestionDto(suggestion);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SuggestionDto approveSuggestion(int courseId, SuggestionDto suggestionDto, UserDto userDto){
        courseExecutionRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId));
        User user = checkIfUserExists(userDto.getUsername());
        Suggestion suggestion = checkIfSuggestionExists(suggestionDto.getId());

        if(!suggestion.getStatus().equals(Suggestion.Status.TOAPPROVE)) {
            if (suggestion.getStatus().equals(Suggestion.Status.REJECTED)) throw new TutorException(SUGGESTION_ALREADY_REJ);
            if (suggestion.getStatus().equals(Suggestion.Status.APPROVED)) throw new TutorException(SUGGESTION_ALREADY_APP);
        }

        checkRoleIfTeacher(user);

        suggestion.setStatus( Suggestion.Status.valueOf(suggestionDto.getStatus()));

        if (suggestionDto.getStatus().equals("REJECTED")) {
            suggestion.setTeacherExplanation(suggestionDto.getTeacherExplanation());
            suggestion.getStudent().incrementNumberOfSuggestions();
        }

        else
            suggestion.getStudent().incrementNumberOfApprovedSuggestions();

        return new SuggestionDto(suggestion);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<SuggestionDto> approvedSuggestionList(int courseId, UserDto userDto){
        String username = userDto.getUsername();
        courseExecutionRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId));
        User user = checkIfUserExists(username);
        checkRoleIfTeacher(user);
        Optional<List<Suggestion>> approvedList = suggestionRepository.getApprovedList(courseId);
        if(approvedList.isEmpty())
            throw new TutorException(NO_APPROVED_SUGGESTIONS);
        return approvedList.get().stream().map(SuggestionDto::new).collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void deleteSuggestion(int courseId, SuggestionDto suggestionDto, UserDto userDto){
        String username = userDto.getUsername();
        courseExecutionRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId));
        User user = checkIfUserExists(username);

        if(!user.getUsername().equals(suggestionDto.getStudent().getUsername()))  throw new TutorException(NOT_SUGGESTION_CREATOR);

        Suggestion suggestion = checkIfSuggestionExists(suggestionDto.getId());

        entityManager.remove(suggestion);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SuggestionDto editSuggestion(SuggestionDto suggestionDto) {

        checkRoleIfStudent(checkIfUserExists(suggestionDto.getStudent().getUsername()));
        Suggestion s = checkIfSuggestionExists(suggestionDto.getId());
        checkIfUserIsValid (suggestionDto,s);

        if (s.getStudentQuestion().isEmpty()) {
            throw new TutorException(SUGGESTION_EMPTY);
        }

        else if (s.getStudentQuestion().length() > 1024) {
            throw new TutorException(SUGGESTION_TOO_LONG);
        }

        else if (s.getStudentQuestion().equals(suggestionDto.getStudentQuestion())) {
            s.setIsPrivate(suggestionDto.getIsPrivate());
            return new SuggestionDto(s);
        }

        s.setStudentQuestion(suggestionDto.getStudentQuestion());
        s.setIsPrivate(suggestionDto.getIsPrivate());
        s.setStatus(Suggestion.Status.TOAPPROVE);

        //new
        s.addOptions(suggestionDto);
        s.setTitle(suggestionDto.getTitle());


        // TODO: 05/05/2020  falta alterar topicos

        return new SuggestionDto(s);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<SuggestionDto> listAllSuggestions(UserDto userdto) {
        User u = checkIfUserExists(userdto.getUsername());

        return u.getCourseExecutions().stream().flatMap(c -> suggestionRepository.listAllSuggestionsbyCourseId(c.getId())
                .stream().map(SuggestionDto::new)).filter(s -> u.getRole() != User.Role.STUDENT || s.getStudent().getUsername().equals(u.getUsername()))
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ListByUsernameDto listAllSuggestionsbyUsername(String username) {
        User u = checkIfUserExists(username);
        ListByUsernameDto dto = new ListByUsernameDto();
        List<SuggestionDto> list = u.getCourseExecutions().stream().flatMap(c -> suggestionRepository.listAllSuggestionsbyCourseId(c.getId())
                .stream().map(SuggestionDto::new).filter(s -> s.getStudent().getUsername().equals(username)))
                .collect(Collectors.toList());
        dto.setListByUsernameDto(list);
        dto.setNumberofapprovedsuugs(u.getNumberOfSuggestionsApproved());
        dto.setNumberofsuggs(list.size());

        return dto;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuestionDto addQuestion(int courseId, SuggestionDto suggestionDto, UserDto userDto){
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId));
        checkRoleIfTeacher(checkIfUserExists(userDto.getUsername()));
        Suggestion suggestion = checkIfSuggestionExists(suggestionDto.getId());

        //new
        editSuggestion(suggestionDto);

        QuestionDto questionDto = suggestionToQuestion(suggestionDto);

        if (questionDto.getCreationDate() == null) {
            questionDto.setCreationDate(DateHandler.toISOString(LocalDateTime.now()));
        }

        Question question = new Question();
        question.setTitle(questionDto.getTitle());
        question.setKey(questionDto.getKey());
        question.setContent(questionDto.getContent());
        question.setStatus(Question.Status.valueOf(questionDto.getStatus()));
        question.setCreationDate(DateHandler.toLocalDateTime(questionDto.getCreationDate()));
        question.setCourse(course);
        question.addOptions(questionDto.getOptions(),suggestion.getOptions());

        suggestion.setStatus(Suggestion.Status.QUESTION);

        question.updateTopics(suggestion.getTopicsList());
        questionRepository.save(question);

        return new QuestionDto(question);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SuggestionDto setCheckMark(int courseExecutionId, SuggestionDto suggestionDto, String username){
       
        courseExecutionRepository.findById(courseExecutionId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseExecutionId));
        User user = checkIfUserExists(username);

        Suggestion suggestion = checkIfSuggestionExists(suggestionDto.getId());

        suggestion.setCheckMark(true);

        return new SuggestionDto(suggestion);

    }

    private QuestionDto suggestionToQuestion(SuggestionDto sugg){
        if (!sugg.getStatus().equals("APPROVED")) throw new TutorException(SUGGESTION_NOT_APPROVED);

        checkTitleAndOptions(sugg);

        QuestionDto questionDto = new QuestionDto();

        questionDto.setContent(sugg.getStudentQuestion());
        questionDto.setTitle(sugg.getTitle());
        questionDto.setOptions(sugg.getOptions());
        questionDto.setStatus(Question.Status.AVAILABLE.name());

        return questionDto;
    }

    public void checkRoleIfStudent(User user) {
        if (user.getRole().compareTo(User.Role.STUDENT) != 0)
            throw new TutorException(USER_HAS_WRONG_ROLE);
    }

    public void checkRoleIfTeacher(User user) {
        if (user.getRole().compareTo(User.Role.TEACHER) != 0)
            throw new TutorException(USER_HAS_WRONG_ROLE);
    }

    private User checkIfUserExists(String username) {
        User u = userRepository.findByUsername(username);
        if(u == null)  throw new TutorException(USERNAME_NOT_FOUND);
        return u;
    }

    private Suggestion checkIfSuggestionExists(int suggestionId) {
        return suggestionRepository.findById(suggestionId).orElseThrow(() -> new TutorException(SUGGESTION_NOT_FOUND));
    }

    private Set<Topic> checkIfTopicExists(int courseId, SuggestionDto suggestionDto) {
        List<TopicDto> newTopics = suggestionDto.getTopicsList();

        if (newTopics.isEmpty()){
            throw new TutorException(EMPTY_TOPICS);
        }

        return newTopics.stream().map(topic -> topicRepository.findTopicByName(courseId, topic.getName()))
                .peek(t -> {if (t == null) throw new TutorException(TOPIC_NOT_FOUND);}).collect(Collectors.toSet());
    }

    private void checkIfUserIsValid (SuggestionDto suggestionDto, Suggestion s) {
        if(!suggestionDto.getStudent().getUsername().equals(s.getStudent().getUsername())) throw new TutorException(ACCESS_DENIED);
    }

    private void checkTitleAndOptions(SuggestionDto suggestionDto) {

        if (suggestionDto.getTitle().trim().length() == 0 ||
                suggestionDto.getStudentQuestion().trim().length() == 0 ||
                suggestionDto.getOptions().stream().anyMatch(optionDto -> optionDto.getContent().trim().length() == 0)) {
            throw new TutorException(QUESTION_MISSING_DATA);
        }

        if (suggestionDto.getOptions().stream().filter(OptionDto::getCorrect).count() != 1) {
            throw new TutorException(QUESTION_MULTIPLE_CORRECT_OPTIONS);
        }
    }


}
