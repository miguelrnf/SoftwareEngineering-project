package pt.ulisboa.tecnico.socialsoftware.tutor.general;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.Demo;
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
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementCreationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOPIC_NOT_FOUND;

@Service
public class GeneralService {
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

        System.out.println(studentAnsweredQuestions);

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
    public StatementQuizDto generateStudentTopicQuiz(UserDto userdto, int executionId, StatementCreationDto quizDetails, String topicName) {
        User user = checkIfUserExists(userdto.getUsername());
        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));
        Topic topic = checkIfTopicExists(courseExecution.getCourse().getId(), topicName);

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

        quiz.generate(topicAvailableQuestions);

        QuizAnswer quizAnswer = new QuizAnswer(user, quiz);

        quiz.setCourseExecution(courseExecution);
        courseExecution.addQuiz(quiz);

        quizRepository.save(quiz);
        quizAnswerRepository.save(quizAnswer);


        return null;
    }
}
