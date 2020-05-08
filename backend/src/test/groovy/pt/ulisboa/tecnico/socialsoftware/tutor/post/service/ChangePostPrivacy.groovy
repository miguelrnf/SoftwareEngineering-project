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
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.PostAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.PostQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostDto
import pt.ulisboa.tecnico.socialsoftware.tutor.post.repository.PostRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@DataJpaTest
class ChangePostPrivacy extends Specification{
    public static final String VALID_QUESTION = 'This is a valid question'
    public static final String VALID_STUDENT_QUESTION = 'I am asking a valid question'
    public static final String VALID_ANSWER = 'I am a valid answer'
    public static final int VALID_KEY = 1
    public static final int VALID_KEY_2 = 2
    public static final int VALID_ID_1 = 1
    public static final int VALID_ID_2 = 2
    public static final int VALID_ID_3 = 3
    public static final String VALID_NAME_1 = "Ben Dover"
    public static final String VALID_NAME_2 = "Mike Litoris"
    public static final String VALID_NAME_3 = "Peixe Acha"
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
    def VALID_Q

    @Shared
    def VALID_Q_2

    @Shared
    def VALID_PA

    @Shared
    def VALID_PQ

    @Shared
    def VALID_PQ_2

    @Shared
    def VALID_U

    @Shared
    def VALID_U_TEACHER

    @Shared
    def INVALID_U_NOT_OWNER

    @Shared
    def VALID_P_NO_ANSWER


    def setupSpec() {
        given: "a valid question"
        VALID_Q = new Question()
        VALID_Q.setKey(VALID_KEY)
        VALID_Q.setContent(VALID_QUESTION)
        VALID_Q.setStatus(Question.Status.AVAILABLE)
        VALID_Q.setNumberOfAnswers(2)
        VALID_Q.setNumberOfCorrect(1)

        and: "a valid user who is the owner"
        VALID_U = new User()
        VALID_U.setKey(VALID_KEY)
        VALID_U.setRole(User.Role.STUDENT)
        VALID_U.setUsername(VALID_USERNAME_1)

        and: "a valid user with the role teacher"
        VALID_U_TEACHER = new User()
        VALID_U_TEACHER.setId(VALID_ID_2)
        VALID_U_TEACHER.setRole(User.Role.TEACHER)
        VALID_U_TEACHER.setUsername(VALID_USERNAME_2)

        and: "a valid postQuestion"
        VALID_PQ = new PostQuestion()
        VALID_PQ.setQuestion(VALID_Q)
        VALID_PQ.setUser(VALID_U)
        VALID_PQ.setStudentQuestion(VALID_STUDENT_QUESTION)
        VALID_U.addPostQuestion(VALID_PQ)

        and: "a valid postQuestion"
        VALID_PQ_2 = new PostQuestion()
        VALID_PQ_2.setQuestion(VALID_Q)
        VALID_PQ_2.setUser(VALID_U)
        VALID_PQ_2.setStudentQuestion(VALID_STUDENT_QUESTION)
        VALID_U.addPostQuestion(VALID_PQ_2)

        and: "a valid postAnswer"
        VALID_PA = new PostAnswer()
        VALID_PA.setUser(VALID_U_TEACHER)
        VALID_PA.setTeacherAnswer(VALID_ANSWER)

        and: "a valid post with no answer"
        VALID_P_NO_ANSWER = new Post()
        VALID_P_NO_ANSWER.setKey(VALID_KEY)
        VALID_P_NO_ANSWER.setQuestion(VALID_PQ_2)

        and: "a valid post with an answer"
        VALID_P = new Post()
        VALID_P.setKey(VALID_KEY_2)
        VALID_P.setQuestion(VALID_PQ)
        VALID_P.setAnswer(VALID_PA)
        VALID_PQ.setPost(VALID_P)
        VALID_P.setPostPrivacy(false)

        and: "a valid user that does not own the post"
        INVALID_U_NOT_OWNER = new User()
        INVALID_U_NOT_OWNER.setId(VALID_ID_3)
        INVALID_U_NOT_OWNER.setRole(User.Role.STUDENT)
        INVALID_U_NOT_OWNER.setUsername(VALID_USERNAME_3)

    }


    def setup() {
        given: "a valid question"
        def question = new Question()
        question.setKey(VALID_KEY)
        question.setTitle(VALID_TITLE)
        question.setContent(VALID_QUESTION)
        question.setStatus(Question.Status.AVAILABLE)
        question.setNumberOfAnswers(2)
        question.setNumberOfCorrect(1)

        and: "three valid users"
        def user1 = new User(VALID_NAME_1, VALID_USERNAME_1, 1, User.Role.STUDENT)
        def user2 = new User(VALID_NAME_2, VALID_USERNAME_2, 2, User.Role.TEACHER)
        def user3 = new User(VALID_NAME_3, VALID_USERNAME_3, 3, User.Role.STUDENT)

        and: "a valid postQuestion"
        def postQuestion1 = new PostQuestion()
        postQuestion1.setQuestion(question)
        postQuestion1.setUser(user1)
        postQuestion1.setStudentQuestion(VALID_STUDENT_QUESTION)

        and: "a valid postQuestion"
        def postQuestion2 = new PostQuestion()
        postQuestion2.setQuestion(question)
        postQuestion2.setUser(user1)
        postQuestion2.setStudentQuestion(VALID_STUDENT_QUESTION)
        and: "a valid postAnswer"
        def postAnswer1 = new PostAnswer()
        postAnswer1.setUser(user2)
        postAnswer1.setTeacherAnswer(VALID_ANSWER)

        and: "a valid post no answer"
        def postNo = new Post(VALID_KEY, postQuestion2)
        postQuestion2.setPost(postNo)
        user1.addPostQuestion(postQuestion2)
        postNo.setPostPrivacy(false)

        and: "a valid post with an answer"
        def postAnswered = new Post(VALID_KEY_2, postQuestion1)
        def answer = new PostAnswer(user2, VALID_ANSWER)
        postQuestion1.setPost(postAnswered)
        user1.addPostQuestion(postQuestion1)
        answer.setTeacherAnswer(VALID_ANSWER)
        postAnswered.setAnswer(answer)
        postAnswered.setPostPrivacy(false)

        then: "add to repository"
        userRepository.save(user1)
        userRepository.save(user2)
        userRepository.save(user3)
        questionRepository.save(question)
        postRepository.save(postNo)
        postRepository.save(postAnswered)

    }


    @Unroll
    def "valid change post privacy"() {
        when:
        def dto = new PostDto(post)
        dto.getQuestion().setUser(new UserDto(user))
        def result = postService.changePostPrivacy(expected.getKey(), user)

        then:
        result.getPostPrivacy() != expected.getPostPrivacy()

        where:
        user                 | post                    ||       expected
        VALID_U as User      | VALID_P as Post         ||       VALID_P as Post


    }

    @Unroll
    def "invalid change post privacy"() {
        when:
        def dto = new PostDto(post)
        dto.getQuestion().setUser(new UserDto(user))
        postService.changePostPrivacy(VALID_KEY, user)

        then:
        def result = thrown(TutorException)
        result.message == expected

        where:
        user                            | post                       || expected
        INVALID_U_NOT_OWNER as User     | VALID_P as Post            || ErrorMessage.NOT_YOUR_POST.label




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
