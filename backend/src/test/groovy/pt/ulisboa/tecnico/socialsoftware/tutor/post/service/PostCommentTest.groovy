package pt.ulisboa.tecnico.socialsoftware.tutor.post.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.post.PostService
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.Post
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.PostComment
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.PostQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostCommentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.post.repository.PostCommentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.post.repository.PostRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime

@DataJpaTest
class PostCommentTest extends Specification {
    public static final String VALID_QUESTION = 'This is a valid question'
    public static final String VALID_STUDENT_QUESTION = 'I am asking a valid question'
    public static final String VALID_COMMENT = 'I am a comment'
    public static final String EMPTY_QUESTION = ''
    public static final int VALID_KEY_1 = 1
    public static final int VALID_KEY_2 = 2
    public static final int VALID_KEY_3 = 3
    public static final int VALID_KEY_4 = 4
    public static final int VALID_KEY_5 = 5
    public static final int VALID_KEY_6 = 6
    public static final int VALID_KEY_7 = 7
    public static final int VALID_ID = 1
    public static final String VALID_NAME_1 = "Ben Dover"
    public static final String VALID_USERNAME_1 = "BenDover69"
    public static final String VALID_TITLE = "Title"
    public static final String TOO_MANY_CHARS =
            '5EdnCpIJFNNr0enpzluxNDqldKmHf6TZvTeLpj6laJPTYaZeI3DYv9KGVXtykpTq0hjXtS75Y3VhBlHlPPI3E1HlmHNI5pH' +
                    '5QYoF24hA7Dd8z6nxA8NStjuugQmKMuZYKV5jugeFtcqt2yoT4LzVMtAvtB7jGMQ8ua4Pxm1QifflguBuJDNmXdtNkpwX3l' +
                    'smTxBwLMoIvXebgzIDG20yloMadbiY8RT7IcMYSRkAFZJqT4xS6Zr9MXLA0ceLetDLYTvWkXokBo4hGcq4cWhXTF6EpK9JV' +
                    'wzx7qH0ftW3nakG1dYonRLFvsvAHDby5eomorAjt35goeFCKiiqGd69NIZhVJm7LIqArqndg52o0cUGIm064b0TbxN9naBy' +
                    '3bFhiDjNh22m8XPgbVh4OHloTKVW0xELfMPDWXjeVsXQrEudyKuUrJKzhRBGoxkXHgNcnASnGTfSm4VQGVsQbuWGiOQ7RLn' +
                    'bs1xN568BiF1vQ3MTHvhS922likFcpOEnTqpG41UOiSYJuGXnybo1Pr5fNirhtBhf2lQTKlX6Np0EsshuDjGqBqjWJg3npy' +
                    'vwdgl6bAjG7wt0RaDf6g22iIv5dys9KCd6mZB5mCh1W1naEAaUZkS0d52fwhciRVmAUqlQxmfdl650O14FntY93IWILmChH' +
                    'wa7vsaXqFJbMM5c4hN2M2mCjVbQYimYHGty6JaJGJ6vBAjkFqRRTC98u5xLBijcUgIfswDdO2JatYs5feyUEEvlB4WO3QcF' +
                    'jYzZdZKjgpNfNXc86zBD3cgUZpk30wSsxN1MgVutK335D4KmqyMcZ5vD4fBoZtJPgAcjOFLSJucrZVr28ln8wL404Slm3EA' +
                    'VBiTuQJWUYlaMJ33nJh6UGeZx1Hwj0fThLq2UtWN9KsBRKnvPl54Ixprv4bisZ6CLYoIh4bhNNZGzVTYmchtvYfVahe153l' +
                    '9vusIYIQt7jSaqSrzlzdS94a1qa3rKiGqvFpgBsbPvzQ6eDpA6WgSTrUgJTuNr5ns1UNG2Y0KqGqvFpgBsbPvzQ6eDpA6Wg' +
                    '5EdnCpIJFNNr0enpzluxNDqldKmHf6TZvTeLpj6laJPTYaZeI3DYv9KGVXtykpTq0hjXtS75Y3VhBlHlPPI3E1HlmHNI5pH' +
                    '5QYoF24hA7Dd8z6nxA8NStjuugQmKMuZYKV5jugeFtcqt2yoT4LzVMtAvtB7jGMQ8ua4Pxm1QifflguBuJDNmXdtNkpwX3l' +
                    'smTxBwLMoIvXebgzIDG20yloMadbiY8RT7IcMYSRkAFZJqT4xS6Zr9MXLA0ceLetDLYTvWkXokBo4hGcq4cWhXTF6EpK9JV' +
                    'wzx7qH0ftW3nakG1dYonRLFvsvAHDby5eomorAjt35goeFCKiiqGd69NIZhVJm7LIqArqndg52o0cUGIm064b0TbxN9naBy' +
                    '3bFhiDjNh22m8XPgbVh4OHloTKVW0xELfMPDWXjeVsXQrEudyKuUrJKzhRBGoxkXHgNcnASnGTfSm4VQGVsQbuWGiOQ7RLn' +
                    'bs1xN568BiF1vQ3MTHvhS922likFcpOEnTqpG41UOiSYJuGXnybo1Pr5fNirhtBhf2lQTKlX6Np0EsshuDjGqBqjWJg3npy' +
                    'vwdgl6bAjG7wt0RaDf6g22iIv5dys9KCd6mZB5mCh1W1naEAaUZkS0d52fwhciRVmAUqlQxmfdl650O14FntY93IWILmChH' +
                    'wa7vsaXqFJbMM5c4hN2M2mCjVbQYimYHGty6JaJGJ6vBAjkFqRRTC98u5xLBijcUgIfswDdO2JatYs5feyUEEvlB4WO3QcF' +
                    'jYzZdZKjgpNfNXc86zBD3cgUZpk30wSsxN1MgVutK335D4KmqyMcZ5vD4fBoZtJPgAcjOFLSJucrZVr28ln8wL404Slm3EA' +
                    'VBiTuQJWUYlaMJ33nJh6UGeZx1Hwj0fThLq2UtWN9KsBRKnvPl54Ixprv4bisZ6CLYoIh4bhNNZGzVTYmchtvYfVahe153l' +
                    '9vusIYIQt7jSaqSrzlzdS94a1qa3rKiSTrx_'

    @Autowired
    PostService postService

    @Autowired
    PostRepository postRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    PostCommentRepository commentRepository

    @Shared
    def VALID_Q

    @Shared
    def VALID_U

    @Shared
    def VALID_PQ

    @Shared
    def VALID_P

    @Shared
    def VALID_C

    @Shared
    def VALID_C_NOT_SAVED

    @Shared
    def VALID_C_CHILD

    @Shared
    def INVALID_C_BLANK

    @Shared
    def INVALID_C_TOO_LONG

    @Shared
    def INVALID_C_NO_PARENT

    def setupSpec() {
        given: "a valid question"
        VALID_Q = new Question()
        VALID_Q.setKey(VALID_KEY_1)
        VALID_Q.setContent(VALID_QUESTION)
        VALID_Q.setStatus(Question.Status.AVAILABLE)
        VALID_Q.setNumberOfAnswers(2)
        VALID_Q.setNumberOfCorrect(1)

        and: "a valid user"
        VALID_U = new User()
        VALID_U.setId(VALID_ID)
        VALID_U.setRole(User.Role.STUDENT)
        VALID_U.setUsername(VALID_USERNAME_1)

        and: "a valid postQuestion"
        VALID_PQ = new PostQuestion()
        VALID_PQ.setQuestion(VALID_Q)
        VALID_PQ.setUser(VALID_U)
        VALID_PQ.setStudentQuestion(VALID_STUDENT_QUESTION)

        and: "a valid post"
        VALID_P = new Post(VALID_KEY_1, VALID_PQ)

        and: "a valid comment"
        VALID_C = new PostComment()
        VALID_C.setPost(VALID_P)
        VALID_C.setUser(VALID_U)
        VALID_C.setKey(VALID_KEY_1)
        VALID_C.setComment(VALID_COMMENT)
        VALID_C.setCreationDate(LocalDateTime.now())

        and: "a valid comment that is not saved"
        VALID_C_NOT_SAVED = new PostComment()
        VALID_C_NOT_SAVED.setPost(VALID_P)
        VALID_C_NOT_SAVED.setUser(VALID_U)
        VALID_C_NOT_SAVED.setKey(VALID_KEY_2)
        VALID_C_NOT_SAVED.setComment(VALID_COMMENT)
        VALID_C_NOT_SAVED.setCreationDate(LocalDateTime.now())

        and: "a valid child comment"
        VALID_C_CHILD = new PostComment()
        VALID_C_CHILD.setPost(VALID_P)
        VALID_C_CHILD.setUser(VALID_U)
        VALID_C_CHILD.setKey(VALID_KEY_3)
        VALID_C_CHILD.setComment(VALID_COMMENT)
        VALID_C_CHILD.setParent(VALID_C)
        VALID_C_CHILD.setCreationDate(LocalDateTime.now())
        VALID_C.addChild(VALID_C_CHILD)

        and: "a blank comment"
        INVALID_C_BLANK = new PostComment()
        INVALID_C_BLANK.setPost(VALID_P)
        INVALID_C_BLANK.setUser(VALID_U)
        INVALID_C_BLANK.setKey(VALID_KEY_4)
        INVALID_C_BLANK.setComment(EMPTY_QUESTION)
        INVALID_C_BLANK.setCreationDate(LocalDateTime.now())

        and: "a comment with too many chars"
        INVALID_C_TOO_LONG = new PostComment()
        INVALID_C_TOO_LONG.setPost(VALID_P)
        INVALID_C_TOO_LONG.setUser(VALID_U)
        INVALID_C_TOO_LONG.setKey(VALID_KEY_5)
        INVALID_C_TOO_LONG.setComment(TOO_MANY_CHARS)
        INVALID_C_TOO_LONG.setCreationDate(LocalDateTime.now())

        and: "a comment with an invalid parent"
        INVALID_C_NO_PARENT = new PostComment()
        INVALID_C_NO_PARENT.setPost(VALID_P)
        INVALID_C_NO_PARENT.setUser(VALID_U)
        INVALID_C_NO_PARENT.setKey(VALID_KEY_6)
        INVALID_C_NO_PARENT.setComment(VALID_COMMENT)
        INVALID_C_NO_PARENT.setParent(VALID_C_NOT_SAVED)
        INVALID_C_NO_PARENT.setCreationDate(LocalDateTime.now())
    }

    def setup() {
        given: "a valid question"
        def question = new Question()
        question.setKey(VALID_KEY_1)
        question.setContent(VALID_QUESTION)
        question.setTitle(VALID_TITLE)
        question.setStatus(Question.Status.AVAILABLE)
        question.setNumberOfAnswers(2)
        question.setNumberOfCorrect(1)

        and: "two valid users"
        def user1 = new User(VALID_NAME_1, VALID_USERNAME_1, 1, User.Role.STUDENT)

        and: "a valid postQuestion"
        def postQuestion1 = new PostQuestion()
        postQuestion1.setQuestion(question)
        postQuestion1.setUser(user1)
        postQuestion1.setStudentQuestion(VALID_STUDENT_QUESTION)

        and: "a valid post"
        def post1 = new Post(VALID_KEY_1, postQuestion1)
        postQuestion1.setPost(post1)
        user1.addPostQuestion(postQuestion1)

        then: "add to repository"
        userRepository.save(user1)
        questionRepository.save(question)
        postRepository.save(post1)
    }

    @Unroll
    def "valid comment submission"() {
        when:
        println(comment.getPost().dump())
        def result = postService.postComment(new PostCommentDto(comment, false))

        then:
        result.getKey() == expected.getKey()
        result.getPost().getKey() == expected.getPost().getKey()
        result.getComment() == expected.getComment()

        where:
        comment                 ||       expected
        VALID_C as PostComment  ||      VALID_C as PostComment
    }

    @Unroll
    def "invalid comment submission"() {
        when:
        postService.postComment(new PostCommentDto(comment, false))

        then:
        def result = thrown(TutorException)
        result.message == expected

        where:
        comment                             ||       expected
        INVALID_C_BLANK as PostComment      ||      ErrorMessage.INVALID_COMMENT.label
        INVALID_C_TOO_LONG as PostComment   ||      ErrorMessage.INVALID_COMMENT.label
        INVALID_C_NO_PARENT as PostComment  ||      ErrorMessage.COMMENT_NO_PARENT.label
    }

    @TestConfiguration
    static class PostServiceImplTestContextConfiguration {
        @Bean
        PostService postService() {
            return new PostService()
        }
    }
}
