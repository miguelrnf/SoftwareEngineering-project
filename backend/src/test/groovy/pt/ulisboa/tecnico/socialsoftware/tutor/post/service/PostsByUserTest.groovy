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
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@DataJpaTest
class PostsByUserTest extends Specification{
    public static final String VALID_QUESTION = 'This is a valid question'
    public static final String VALID_STUDENT_QUESTION = 'I am asking a valid question'
    public static final int VALID_KEY = 1
    public static final int VALID_KEY_2 = -1
    public static final int VALID_ID_1 = 1
    public static final int VALID_ID_2 = 4
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

    @Shared
    def VALID_P

    @Shared
    def INVALID_P

    @Shared
    def VALID_Q

    @Shared
    def VALID_U_1

    @Shared
    def VALID_U_2

    @Shared
    def INVALID_U

    @Shared
    def VALID_PQ

    @Shared
    def VALID_PQ_2


    def setupSpec() {
        given: "a valid question"
        VALID_Q = new Question()
        VALID_Q.setKey(VALID_KEY)
        VALID_Q.setContent(VALID_QUESTION)
        VALID_Q.setStatus(Question.Status.AVAILABLE)
        VALID_Q.setNumberOfAnswers(2)
        VALID_Q.setNumberOfCorrect(1)

        and: "a valid user"
        VALID_U_1 = new User()
        VALID_U_1.setId(VALID_ID_1)
        VALID_U_1.setRole(User.Role.STUDENT)
        VALID_U_1.setUsername(VALID_USERNAME_1)

        and: "a valid user with no posts"
        VALID_U_2 = new User()
        VALID_U_2.setId(VALID_ID_2)
        VALID_U_2.setRole(User.Role.STUDENT)
        VALID_U_2.setUsername(VALID_USERNAME_2)

        and: "an invalid user"
        INVALID_U = new User()
        INVALID_U.setId(-1)
        INVALID_U.setRole(User.Role.STUDENT)
        INVALID_U.setUsername(VALID_USERNAME_3)

        and: "a valid postQuestion"
        VALID_PQ = new PostQuestion()
        VALID_PQ.setQuestion(VALID_Q)
        VALID_PQ.setUser(VALID_U_1)
        VALID_PQ.setStudentQuestion(VALID_STUDENT_QUESTION)

        and: "a valid postQuestion2"
        VALID_PQ_2 = new PostQuestion()
        VALID_PQ_2.setQuestion(VALID_Q)
        VALID_PQ_2.setUser(VALID_U_1)
        VALID_PQ_2.setStudentQuestion(VALID_STUDENT_QUESTION)

        and: "a valid post"
        VALID_P = new Post()
        VALID_P.setKey(VALID_KEY)
        VALID_P.setQuestion(VALID_PQ)
        VALID_U_1.addPostQuestion(VALID_PQ)
        VALID_PQ.setPost(VALID_P)

        and: "an invalid post because it will not be saved"
        INVALID_P  = new Post()
        INVALID_P.setKey(VALID_KEY_2)
        INVALID_P.setQuestion((VALID_PQ_2))
        VALID_U_1.addPostQuestion(VALID_PQ_2)
        VALID_PQ_2.setPost(INVALID_P)
    }

    def setup() {
        given: "a valid question"
        def question = new Question()
        question.setKey(VALID_KEY)
        question.setContent(VALID_QUESTION)
        question.setTitle(VALID_TITLE)
        question.setStatus(Question.Status.AVAILABLE)
        question.setNumberOfAnswers(2)
        question.setNumberOfCorrect(1)

        and: "three valid users"
        def user1 = new User(VALID_NAME_1, VALID_USERNAME_1, 1, User.Role.STUDENT)
        def user2 = new User(VALID_NAME_2, VALID_USERNAME_2, 2, User.Role.TEACHER)

        and: "a valid postQuestion"
        def postQuestion1 = new PostQuestion()
        postQuestion1.setQuestion(question)
        postQuestion1.setUser(user1)
        postQuestion1.setStudentQuestion(VALID_STUDENT_QUESTION)
        def postQuestion2 = new PostQuestion()
        postQuestion2.setQuestion(question)
        postQuestion2.setUser(user1)
        postQuestion2.setStudentQuestion(VALID_STUDENT_QUESTION)

        and: "a valid post"
        def post1 = new Post(VALID_KEY, postQuestion1)
        postQuestion1.setPost(post1)
        user1.addPostQuestion(postQuestion1)

        then: "add to repository"
        userRepository.save(user1)
        userRepository.save(user2)
        questionRepository.save(question)
        postRepository.save(post1)
    }

    @Unroll
    def "view posts of an existing user"() {
        when:
        def result = postService.postsByUser(user.getUsername())

        then:
        println(result)
        result.getTotalPosts() == expected

        where:
        user                            || expected
        VALID_U_1 as User               || 1
        VALID_U_2 as User               || 0

    }

    @Unroll
    def "view posts of a user that does not exist"() {
        when:
        postService.postsByUser(user.getUsername())

        then:
        def result = thrown(TutorException)
        result.message == expected

        where:
        user                            || expected
        INVALID_U as User               || ErrorMessage.USERNAME_NOT_FOUND.label

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
