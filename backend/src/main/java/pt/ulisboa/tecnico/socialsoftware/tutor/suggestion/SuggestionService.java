package pt.ulisboa.tecnico.socialsoftware.tutor.suggestion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
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
import java.util.*;
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
    public List<TopicDto> chooseTopics(int courseId, String username){
        CourseExecution course = courseExecutionRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId));
        User user = checkIfUserExists(username);

        List<Topic> list = topicRepository.findTopics(course.getCourse().getId());

        if(list.isEmpty())
            throw new TutorException(NO_TOPICS);
        return list.stream().map(TopicDto::new).collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SuggestionDto submitSuggestion(int courseId, SuggestionDto s){
        return s;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SuggestionDto createSuggestion(int courseId, SuggestionDto suggestionDto){
        String username = suggestionDto.get_student().getUsername();
        CourseExecution course = courseExecutionRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId));
        User user = checkIfUserExists(username);

        if(user.getRole().compareTo(User.Role.STUDENT) != 0) {throw new TutorException(USER_HAS_WRONG_ROLE);}

        Set<Topic> topics = checkIfTopicExists(course.getCourse().getId(), suggestionDto);

        if (suggestionDto.getKey() == null) {
            int maxQuestionNumber = suggestionRepository.getMaxSuggestionNumber() != null ?
                    suggestionRepository.getMaxSuggestionNumber() : 0;
            suggestionDto.setKey(maxQuestionNumber + 1);
        }

        Suggestion suggestion = new Suggestion(course, user, suggestionDto);
        suggestion.setCreationDate(LocalDateTime.now());
        suggestion.set_topicsList(topics);

        topics.forEach(topic -> topic.addSuggestion(suggestion));

        entityManager.persist(suggestion);

        return new SuggestionDto(suggestion);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SuggestionDto approveSuggestion(int courseId, SuggestionDto suggestionDto, UserDto userDto){
        String username = userDto.getUsername();
        CourseExecution course = courseExecutionRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId));
        User user = checkIfUserExists(username);
        if(user.getRole() != User.Role.TEACHER)  throw new TutorException(USER_HAS_WRONG_ROLE);

        Suggestion suggestion = checkIfSuggestionExists(suggestionDto.get_id());

        suggestion.setStatus( Suggestion.Status.valueOf(suggestionDto.getStatus()));

        if (suggestionDto.getStatus().equals("REJECTED")) {

            suggestion.set_justification(suggestionDto.get_justification());
            suggestion.get_student().incrementNumberofsuggestions();


        }

        else {

            suggestion.get_student().incrementNumberofsuggestions();
            suggestion.get_student().incrementNumberofapprovedsuggestions();

        }


        return new SuggestionDto(suggestion);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<SuggestionDto> approvedSuggestionList(int courseId, UserDto userDto){
        String username = userDto.getUsername();
        CourseExecution course = courseExecutionRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId));
        User user = checkIfUserExists(username);
        if(user.getRole() != User.Role.TEACHER)  throw new TutorException(USER_HAS_WRONG_ROLE);
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
        CourseExecution course = courseExecutionRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId));
        User user = checkIfUserExists(username);

        if(!user.getUsername().equals(suggestionDto.get_student().getUsername()))  throw new TutorException(NOT_SUGGESTION_CREATOR);

        Suggestion suggestion = checkIfSuggestionExists(suggestionDto.get_id());

        entityManager.remove(suggestion);
    }


    private User checkIfUserExists(String username) {
        User u = userRepository.findByUsername(username);
        if(u == null)  throw new TutorException(USERNAME_NOT_FOUND);
        return u;
    }

    private Suggestion checkIfSuggestionExists(int suggestionId) {
        return suggestionRepository.findById(suggestionId).orElseThrow(() -> new TutorException(SUGGESTION_NOT_FOUND));
    }

    private  Set<Topic> checkIfTopicExists(int courseId, SuggestionDto suggestionDto) {
        List<TopicDto> newTopics = suggestionDto.get_topicsList();

        if (newTopics.isEmpty()){
            throw new TutorException(EMPTY_TOPICS);
        }

        newTopics.stream().filter(topic -> topicRepository.findTopicByName(courseId, topic.getName()) != null )
                .findAny().orElseThrow(() -> new TutorException(TOPIC_NOT_FOUND));


        return newTopics.stream().map(topic -> topicRepository.findTopicByName(courseId, topic.getName()))
                .collect(Collectors.toSet());
    }

    private boolean checkIfUserHasRoleStudent(User user) {
        if(user.getRole().compareTo(User.Role.STUDENT) != 0) {return false;}

        return true;
    }

    private void checkIfUserIsValid (SuggestionDto suggestionDto, Suggestion s) {
        if(!suggestionDto.get_student().getUsername().equals(s.get_student().getUsername())) throw new TutorException(ACCESS_DENIED);
    }

    private boolean checkIfUserHasRoleTeacher(User user) {
        if(user.getRole().compareTo(User.Role.TEACHER) != 0) {return false;}

        return true;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SuggestionDto editSuggestion(SuggestionDto suggestionDto) {

        checkIfUserHasRoleStudent(checkIfUserExists(suggestionDto.get_student().getUsername()));
        Suggestion s = checkIfSuggestionExists(suggestionDto.get_id());
        checkIfUserIsValid (suggestionDto,s);

        if (s.get_questionStr().isEmpty()) {

            throw new TutorException(SUGGESTION_EMPTY);

        }

        if (s.get_questionStr().length() > 1024) {

            throw new TutorException(SUGGESTION_TOO_LONG);

        }

        s.set_questionStr(suggestionDto.get_questionStr());
        s.setStatus(Suggestion.Status.TOAPPROVE);
        s.set_changed(true);

        return new SuggestionDto(s);

    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<SuggestionDto> listAllSuggestions(UserDto userdto) {

        List<SuggestionDto> array = new ArrayList<>();
        List<Suggestion> tmp = new ArrayList<>();
        User u = checkIfUserExists(userdto.getUsername());

        if (checkIfUserHasRoleTeacher(u)) {


            for (CourseExecution Course : u.getCourseExecutions()) {

                array.addAll(suggestionRepository.listAllSuggestionsbyCourseId(Course.getId()).stream().map(SuggestionDto::new).collect(Collectors.toList()));

            }

            if (array.size() == 0) throw new TutorException(EMPTY_SUGGESTIONS_LIST);


            return array;
        }


        if (checkIfUserHasRoleStudent(u)) {

            tmp = suggestionRepository.findAll().stream().filter(suggestion -> userdto.getUsername().equals(suggestion.get_student().getUsername())).collect(Collectors.toList());
            if (tmp.size() == 0) throw new TutorException(EMPTY_SUGGESTIONS_LIST);

            return tmp.stream().map(SuggestionDto::new).collect(Collectors.toList());

        }

        throw new TutorException(ACCESS_DENIED);

    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ListByUsernameDto listAllSuggestionsbyUsername(String username) {

        List<SuggestionDto> array = new ArrayList<>();
        List<Suggestion> tmp = new ArrayList<>();
        User u = checkIfUserExists(username);
        ListByUsernameDto List = new ListByUsernameDto();

        if (checkIfUserHasRoleStudent(u)) {

            tmp = suggestionRepository.findAll().stream().filter(suggestion -> username.equals(suggestion.get_student().getUsername())).collect(Collectors.toList());
            if (tmp.size() == 0) throw new TutorException(EMPTY_SUGGESTIONS_LIST);

            List.setListByUsernameDto (tmp);
            List.setNumberofapprovedsuugs (u.getnumberofapprovedsuggs());
            List.setNumberofsuugs(u.getnumberofsuggs());

            return List;

        }

        throw new TutorException(ACCESS_DENIED);

    }


    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Suggestion.Status SeeSuggestionStatus(SuggestionDto suggestionDto) {

        checkIfUserHasRoleStudent(checkIfUserExists(suggestionDto.get_student().getUsername()));
        Suggestion s = checkIfSuggestionExists(suggestionDto.get_id());
        checkIfUserIsValid (suggestionDto,s);

        return s.getStatus();
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuestionDto addQuestion(int courseId, SuggestionDto suggestionDto, UserDto userDto){
        String username = userDto.getUsername();
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId));
        User user = checkIfUserExists(username);
        if(user.getRole() != User.Role.TEACHER)  throw new TutorException(USER_HAS_WRONG_ROLE);

        Suggestion suggestion = checkIfSuggestionExists(suggestionDto.get_id());

        QuestionDto questionDto = suggestionToQuestion(suggestionDto);

        if (questionDto.getCreationDate() == null) {
            questionDto.setCreationDate(LocalDateTime.now().format(Course.formatter));
        }

        Question question = new Question(course, questionDto);

        suggestion.setStatus(Suggestion.Status.QUESTION);

        question.updateTopics(suggestion.get_topicsList());
//falta adicionar novas infos
        questionRepository.save(question);

        return new QuestionDto(question);
    }

    private QuestionDto suggestionToQuestion(SuggestionDto sugg){
        if (!sugg.getStatus().equals("APPROVED")) throw new TutorException(SUGGESTION_NOT_APPROVED);

        if (sugg.getTitle().trim().length() == 0 ||
                sugg.get_questionStr().trim().length() == 0 ||
                sugg.getOptions().stream().anyMatch(optionDto -> optionDto.getContent().trim().length() == 0)) {
            throw new TutorException(QUESTION_MISSING_DATA);
        }

        if (sugg.getOptions().stream().filter(OptionDto::getCorrect).count() != 1) {
            throw new TutorException(QUESTION_MULTIPLE_CORRECT_OPTIONS);
        }

        QuestionDto questionDto = new QuestionDto();

        questionDto.setContent(sugg.get_questionStr());
        questionDto.setTitle(sugg.getTitle());
        questionDto.setOptions(sugg.getOptions());
        questionDto.setStatus(Question.Status.AVAILABLE.name());

        return questionDto;
    }

}
