package pt.ulisboa.tecnico.socialsoftware.tutor.suggestion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.domain.Suggestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.dto.SuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.repository.SuggestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public SuggestionDto createSuggestion(int courseId, SuggestionDto suggestionDto){
        String username = suggestionDto.get_student().getUsername();
        CourseExecution course = courseExecutionRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId));
        User user = checkIfUserExists(username);
        checkIfUserHasRoleStudent(user);

        Set<Topic> topics = checkIfTopicExists(courseId, suggestionDto);

        if (suggestionDto.getKey() == null) {
            int maxQuestionNumber = suggestionRepository.getMaxSuggestionNumber() != null ?
                    suggestionRepository.getMaxSuggestionNumber() : 0;
            suggestionDto.setKey(maxQuestionNumber + 1);
        }

        Suggestion suggestion = new Suggestion(course, user, suggestionDto);
        suggestion.setCreationDate(LocalDateTime.now());
        suggestion.set_topicsList(topics);
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

        Suggestion suggestion = checkIfSuggestionExists(suggestionDto.getKey());

        suggestion.setStatus( Suggestion.Status.valueOf(suggestionDto.getStatus()) );
        suggestion.set_justification(suggestionDto.get_justification());

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
        Optional<List<Suggestion>> approvedList = suggestionRepository.getApprovedList();
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

        Suggestion suggestion = checkIfSuggestionExists(suggestionDto.getKey());

        entityManager.remove(suggestion);
    }


    private User checkIfUserExists(String username) {
        User u = userRepository.findByUsername(username);
        if(u == null)  throw new TutorException(USERNAME_NOT_FOUND);
        return u;
    }

    private Suggestion checkIfSuggestionExists(int suggestionKey) {
        return suggestionRepository.findByKey(suggestionKey).orElseThrow(() -> new TutorException(SUGGESTION_NOT_FOUND));
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

    private void checkIfUserHasRoleStudent(User user) {
        if(user.getRole().compareTo(User.Role.STUDENT) != 0) throw new TutorException(USER_HAS_WRONG_ROLE);
    }

    private void checkIfUserIsValid (SuggestionDto suggestionDto, Suggestion s) {
        if(suggestionDto.get_student().getUsername() != (s.get_student().getUsername())) throw new TutorException(ACCESS_DENIED);
    }

    private void checkIfUserHasRoleTeacher(User user) {
        if(user.getRole().compareTo(User.Role.TEACHER) != 0) throw new TutorException(USER_HAS_WRONG_ROLE);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SuggestionDto editSuggestion(SuggestionDto suggestionDto) {

        checkIfUserHasRoleStudent(checkIfUserExists(suggestionDto.get_student().getUsername()));
        Suggestion s = checkIfSuggestionExists(suggestionDto.getKey());
        checkIfUserIsValid (suggestionDto,s);

        if (s.get_questionStr().isEmpty()) {

            throw new TutorException(SUGGESTION_EMPTY);

        }

        if (s.get_questionStr().length() > 1024) {

            throw new TutorException(SUGGESTION_TOO_LONG);

        }

        s.set_questionStr(suggestionDto.get_questionStr());

        return new SuggestionDto(s);

    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<SuggestionDto> listAllSuggestions(UserDto userdto) {

        checkIfUserHasRoleStudent(checkIfUserExists(userdto.getUsername()));

        List<SuggestionDto> array = suggestionRepository.listAllSuggestions(userdto.getId()).stream().map(SuggestionDto::new).collect(Collectors.toList());

        if (array.size() == 0) throw new TutorException(EMPTY_SUGGESTIONS_LIST);

        return array;

    }
}