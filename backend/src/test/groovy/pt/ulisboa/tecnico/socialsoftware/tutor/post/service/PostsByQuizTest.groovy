package pt.ulisboa.tecnico.socialsoftware.tutor.post.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.post.PostService
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.Post
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.PostQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.post.repository.PostRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification
import spock.lang.Unroll

@DataJpaTest
class PostsByQuizTest extends Specification{
    public static final String VALID_QUESTION = 'This is a valid question'
    public static final String VALID_STUDENT_QUESTION = 'I am asking a valid question'
    public static final int VALID_KEY = 1
    public static final String VALID_NAME_1 = "Ben Dover"
    public static final String VALID_NAME_2 = "Mike Litoris"
    public static final String VALID_USERNAME_1 = "BenDover69"
    public static final String VALID_USERNAME_2 = "MikeLitoris420"
    public static final String VALID_USERNAME_3 = "PeixeAcha666"
    public static final String VALID_TITLE = "Title"

    @Autowired
    PostService postService

    @Autowired
    PostRepository postRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    QuizRepository quizRepository

    def setup() {
        given: "two valid questions"
        def question1 = new Question()
        question1.setKey(VALID_KEY)
        question1.setContent(VALID_QUESTION)
        question1.setTitle(VALID_TITLE)
        question1.setStatus(Question.Status.AVAILABLE)
        question1.setNumberOfAnswers(2)
        question1.setNumberOfCorrect(1)

        def question2 = new Question()
        question2.setKey(VALID_KEY)
        question2.setContent(VALID_QUESTION)
        question2.setTitle(VALID_TITLE)
        question2.setStatus(Question.Status.AVAILABLE)
        question2.setNumberOfAnswers(2)
        question2.setNumberOfCorrect(1)

        and: "a valid user"
        def user1 = new User(VALID_NAME_1, VALID_USERNAME_1, 1, User.Role.STUDENT)

        and: "a valid postQuestion"
        def postQuestion1 = new PostQuestion()
        postQuestion1.setQuestion(question1)
        postQuestion1.setUser(user1)
        postQuestion1.setStudentQuestion(VALID_STUDENT_QUESTION)

        and: "a valid post"
        def post1 = new Post(VALID_KEY, postQuestion1)
        postQuestion1.setPost(post1)
        user1.addPostQuestion(postQuestion1)

        and: "a valid quiz with posts"
        def quiz1 = new Quiz()
        quiz1.setKey(1)
        quiz1.setType("IN_CLASS")
        quiz1.addQuizQuestion(new QuizQuestion(quiz1, question1, 1))

        and: "a valid quiz"
        def quiz2 = new Quiz()
        quiz2.setKey(2)
        quiz2.setType("IN_CLASS")
        quiz2.addQuizQuestion(new QuizQuestion(quiz2, question2, 1))

        then: "add to repository"
        userRepository.save(user1)
        questionRepository.save(question1)
        questionRepository.save(question2)
        postRepository.save(post1)
        quizRepository.save(quiz1)
        quizRepository.save(quiz2)
    }

    @Unroll
    def "view posts of an existing user"() {
        when:
        def result = postService.postsByQuiz(quizid)

        then:
        println(result)
        result.getTotalPosts() == expected

        where:
        quizid            || expected
        1                 || 1              // Since the quiz is being checked by id and we want the second quiz on the second test
        4                 || 0              // We have to specify id = 4 (each test resets and adds the posts back to db again)

    }

    @Unroll
    def "view posts of a user that does not exist"() {
        when:
        postService.postsByQuiz(quizid)

        then:
        def result = thrown(TutorException)
        result.message.replaceAll("id.*", "") == expected.replaceAll("id.*", "")

        where:
        quizid          || expected
        7               || ErrorMessage.QUIZ_NOT_FOUND.label

    }

    @TestConfiguration
    static class PostServiceImplTestContextConfiguration {
        @Bean
        PostService postService() {
            return new PostService()
        }

        @Bean
        QuizService quizService() {
            return new QuizService()
        }

        @Bean
        AnswerService answerService() {
            return new AnswerService()
        }

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }

        @Bean
        AnswersXmlImport xmlImporter() {
            return new AnswersXmlImport()
        }
    }
}
