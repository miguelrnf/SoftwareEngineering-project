package pt.ulisboa.tecnico.socialsoftware.tutor.post.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.post.PostService
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.Post
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.PostQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostDto
import pt.ulisboa.tecnico.socialsoftware.tutor.post.repository.PostRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@DataJpaTest
class DeletePostTest extends Specification {
    public static final String VALID_QUESTION = 'This is a valid question'
    public static final String VALID_STUDENT_QUESTION = 'I am asking a valid question'
    public static final int VALID_KEY = 1
    public static final int VALID_KEY_NOT_SAVED = 2
    public static final int VALID_KEY_NOT_ANSWERED = 3
    public static final int INVALID_KEY = -1
    public static final int VALID_ID_1 = 1
    public static final String VALID_NAME_1 = "Ben Dover"
    public static final String VALID_NAME_2 = "Mike Litoris"
    public static final String VALID_USERNAME_1 = "BenDover69"
    public static final String VALID_USERNAME_2 = "MikeLitoris420"

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
    def VALID_U

    @Shared
    def VALID_PQ

    @Shared
    def INVALID_P_KEY

    @Shared
    def INVALID_P_NOT_SAVED

    @Shared
    def INVALID_P_NOT_ANSWERED

    def setupSpec() {
        given: "a valid question"
        VALID_Q = new Question()
        VALID_Q.setKey(VALID_KEY)
        VALID_Q.setContent(VALID_QUESTION)
        VALID_Q.setStatus(Question.Status.AVAILABLE)
        VALID_Q.setNumberOfAnswers(2)
        VALID_Q.setNumberOfCorrect(1)

        and:"a valid user"
        VALID_U = new User()
        VALID_U.setId(VALID_ID_1)
        VALID_U.setRole(User.Role.STUDENT)
        VALID_U.setUsername(VALID_USERNAME_1)

        and: "a valid postQuestion"
        VALID_PQ = new PostQuestion()
        VALID_PQ.setQuestion(VALID_Q)
        VALID_PQ.setUser(VALID_U)
        VALID_PQ.setStudentQuestion(VALID_STUDENT_QUESTION)

        and: "a valid post"
        VALID_P = new Post()
        VALID_P.setKey(VALID_KEY)
        VALID_P.setQuestion(VALID_PQ)

        and: "a post with an invalid key"
        INVALID_P_KEY = new Post()
        INVALID_P_KEY.setKey(INVALID_KEY)
        INVALID_P_KEY.setQuestion(VALID_PQ)

        and: "a post that was not saved"
        INVALID_P_NOT_SAVED = new Post()
        INVALID_P_NOT_SAVED.setKey(VALID_KEY_NOT_SAVED)
        INVALID_P_NOT_SAVED.setQuestion(VALID_PQ)

        and: "a post that was not answered"
        INVALID_P_NOT_ANSWERED = new Post()
        INVALID_P_NOT_ANSWERED.setKey(VALID_KEY_NOT_ANSWERED)
        INVALID_P_NOT_ANSWERED.setQuestion(VALID_PQ)
    }

    def setup() {
        given: "a valid question"
        def question = new Question()
        question.setKey(VALID_KEY)
        question.setContent(VALID_QUESTION)
        question.setStatus(Question.Status.AVAILABLE)
        question.setNumberOfAnswers(2)
        question.setNumberOfCorrect(1)

        and: "two valid users"
        def user1 = new User(VALID_NAME_1, VALID_USERNAME_1, 1, User.Role.STUDENT)
        def user2 = new User(VALID_NAME_2, VALID_USERNAME_2, 2, User.Role.STUDENT)

        and: "two valid postQuestions"
        def postQuestion1 = new PostQuestion()
        postQuestion1.setQuestion(question)
        postQuestion1.setUser(user1)
        postQuestion1.setStudentQuestion(VALID_STUDENT_QUESTION)
        def postQuestion2 = new PostQuestion()
        postQuestion2.setQuestion(question)
        postQuestion2.setUser(user2)
        postQuestion2.setStudentQuestion(VALID_STUDENT_QUESTION)

        and: "two valid posts"
        def post1 = new Post(VALID_KEY, postQuestion1)
        def post2 = new Post(VALID_KEY_NOT_ANSWERED, postQuestion2)
        postQuestion1.setPost(post1)
        postQuestion2.setPost(post2)
        user1.addPostQuestion(postQuestion1)
        user2.addPostQuestion(postQuestion2)

        then: "add to repository"
        userRepository.save(user1)
        userRepository.save(user2)
        questionRepository.save(question)
        postRepository.save(post1)
        postRepository.save(post2)
    }

    @Unroll
    def "valid deletion"() {
        when:
        def result = postService.deletePost(new PostDto(tocheck))

        then:
        result.getKey() == expected.getKey()
        result.getQuestion().getQuestion().getKey() == expected.getQuestion().getQuestion().getKey()
        result.getQuestion().getStudentQuestion() == expected.getQuestion().getStudentQuestion()

        where:
        tocheck                 |   expected
        VALID_P as Post         |   VALID_P as Post
    }

    @Unroll
    def "invalid deletions"() {
        when:
        postService.deletePost(new PostDto(tocheck as Post))

        then:
        def result = thrown(TutorException)
        result.message == expected

        where:
        tocheck                 |   expected
        INVALID_P_KEY           |   ErrorMessage.INVALID_POST.label
        INVALID_P_NOT_SAVED     |   ErrorMessage.INVALID_POST.label
        INVALID_P_NOT_ANSWERED  |   ErrorMessage.NOT_YOUR_POST.label
    }

    @TestConfiguration
    static class PostServiceImplTestContextConfiguration {
        @Bean
        PostService postService() {
            return new PostService()
        }
    }
}
