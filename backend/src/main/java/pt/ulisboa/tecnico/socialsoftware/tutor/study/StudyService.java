package pt.ulisboa.tecnico.socialsoftware.tutor.study;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.StatementService;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.SolvedQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementCreationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class StudyService {
    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private QuizService quizService;

    @Autowired
    private StatementService statementService;


    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<QuestionDto> answeredQuestionsOfTopic(int courseId, UserDto userdto, String topicName) {

        User u = checkIfUserExists(userdto.getUsername());

        Topic topic = checkIfTopicExists(courseId, topicName);


        List<QuestionDto> studentAnsweredQuestions = u.getQuizAnswers().stream()
                .flatMap(quizAnswer -> quizAnswer.getQuestionAnswers().stream())
                .filter(questionAnswer -> questionAnswer.getTimeTaken() != null && questionAnswer.getTimeTaken() != 0)
                .filter(questionAnswer -> questionAnswer.getQuizQuestion().getQuestion().getTopics().contains(topic))
                .map(questionAnswer -> new QuestionDto(questionAnswer.getQuizQuestion().getQuestion()))
                .collect(Collectors.toList());


        return studentAnsweredQuestions;
    }


    private Topic checkIfTopicExists(int courseId, String topicName){
        Topic topic = topicRepository.findTopicByName(courseId, topicName);
        if(topic == null) throw new TutorException(TOPIC_NOT_FOUND);
        return topic;
    }

    private User checkIfUserExists(String username) {
        User u = userRepository.findByUsername(username);
        if(u == null)  throw new TutorException(USERNAME_NOT_FOUND);
        return u;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TopicDto> getAvailableTopics(int executionId){
        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));
        List<Question> availableQuestions = questionRepository.findAvailableQuestions(courseExecution.getCourse().getId());

        List<TopicDto> topics = availableQuestions.stream().
                flatMap(question -> question.getTopics().stream()).distinct().map(topic -> new TopicDto(topic)).collect(Collectors.toList());

        return topics;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<SolvedQuizDto> getOwnQuizzes(UserDto userdto, int executionId){
        User user = checkIfUserExists(userdto.getUsername());

        return user.getQuizzes().stream()
                .filter(quiz -> quiz.getCourseExecution().getId() == executionId)
                .flatMap(quiz -> quiz.getQuizAnswers().stream())
                .filter(quizAnswer -> quizAnswer.getUser()==user)
                .map(SolvedQuizDto::new)
                .filter(solvedQuizDto -> solvedQuizDto.getAnswerDate()!=null)
                .sorted(Comparator.comparing(SolvedQuizDto::getAnswerDate))
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StatementQuizDto generateStudentTopicQuiz(UserDto userdto, int executionId, StatementCreationDto quizDetails, String topicName) {
        User user = checkIfUserExists(userdto.getUsername());
        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));
        Topic topic = checkIfTopicExists(courseExecution.getCourse().getId(), topicName);

        quizDetails.setNumberOfQuestions(5);

        Quiz quiz = new Quiz();
        quiz.setKey(quizService.getMaxQuizKey() + 1);
        quiz.setType(Quiz.QuizType.GENERATED.toString());
        quiz.setCreationDate(DateHandler.now());

        List<Question> availableQuestions = questionRepository.findAvailableQuestions(courseExecution.getCourse().getId());


        List<Question> topicAvailableQuestions = availableQuestions.stream()
                .filter(question -> question.getTopics().contains(topic))
                .collect(Collectors.toList());


        /*if(quizDetails.getAssessment() != null) {
            topicAvailableQuestions = statementService.filterByAssessment(topicAvailableQuestions, quizDetails);
        }*/

        if (topicAvailableQuestions.size() < quizDetails.getNumberOfQuestions()) {
            throw new TutorException(NOT_ENOUGH_QUESTIONS);
        }

        topicAvailableQuestions = user.filterQuestionsByStudentModel(quizDetails.getNumberOfQuestions(), topicAvailableQuestions);

        quiz.generateWithName(topicAvailableQuestions, topicName);

        quiz.setStudent(user);
        user.addQuiz(quiz);

        QuizAnswer quizAnswer = new QuizAnswer(user, quiz);

        quiz.setCourseExecution(courseExecution);
        courseExecution.addQuiz(quiz);

        quizRepository.save(quiz);
        quizAnswerRepository.save(quizAnswer);


        return new StatementQuizDto(quizAnswer);
    }


    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String suggestionStudentTopicQuiz(UserDto userdto, int executionId) {
        String topicName="";
        User user = checkIfUserExists(userdto.getUsername());
        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));
        List<TopicDto> availableTopics = getAvailableTopics(executionId);

        List<Topic> myTopics = user.getQuizzes().stream()
                .flatMap(quiz -> quiz.getQuizQuestions().stream())
                .flatMap(quizQuestion -> quizQuestion.getQuestion().getTopics().stream())
                .collect(Collectors.toList());

        List<Topic> solvedTopics = user.getQuizAnswers().stream()
                .flatMap(quizAnswer -> quizAnswer.getQuiz().getQuizQuestions().stream())
                .flatMap(quizQuestion -> quizQuestion.getQuestion().getTopics().stream())
                .collect(Collectors.toList());

        List<Topic> difference = availableTopics.stream()
                .map(y -> checkIfTopicExists(courseExecution.getCourse().getId(), y.getName()))
                .filter(x -> !myTopics.contains(x) && !solvedTopics.contains(x))
                .collect(Collectors.toList());

        Random rand = new Random();

        if(difference.size() > 0)
            topicName = difference.get(rand.nextInt(difference.size())).getName();
        else{
            myTopics.addAll(solvedTopics);
            Map<Topic, Long> frequencyMap = myTopics.stream().collect(Collectors.groupingBy(Function.identity(),Collectors.counting()));

            long min = Collections.min(frequencyMap.values());

            List<String> l = frequencyMap.entrySet().stream().filter(x -> x.getValue()==min).map(topicLongEntry -> topicLongEntry.getKey().getName()).collect(Collectors.toList());

            topicName = l.get(rand.nextInt(l.size()));
        }

        return topicName;
    }

}
