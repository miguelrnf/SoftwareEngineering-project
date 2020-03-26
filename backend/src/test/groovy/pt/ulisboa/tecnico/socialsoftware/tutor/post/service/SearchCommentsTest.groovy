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
class SearchCommentsTest extends Specification {
    public static final String VALID_QUESTION = 'This is a valid question'
    public static final String VALID_STUDENT_QUESTION = 'I am asking a valid question'
    public static final String VALID_COMMENT = 'This is a valid comment'
    public static final String EMPTY_QUESTION = ''
    public static final int VALID_KEY = 1
    public static final int VALID_KEY_2 = 2
    public static final int VALID_KEY_3 = 3
    public static final int VALID_ID = 1
    public static final String VALID_NAME_1 = "Ben Dover"
    public static final String VALID_USERNAME_1 = "BenDover69"
    public static final String VALID_NAME_2 = "Mickey"
    public static final String VALID_USERNAME_2 = "Mickey123"
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
    def VALID_PC

    @Shared
    def VALID_PC_2

    @Shared
    def VALID_C_LIST

    @Shared
    def COMMENT_LIST


    def setupSpec() {

        given: "a valid question"
        VALID_Q = new Question()
        VALID_Q.setKey(VALID_KEY)
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
        VALID_P = new Post(VALID_KEY, VALID_PQ)

        and: "a valid post comment"
        VALID_PC = new PostComment()
        VALID_PC.setPost(VALID_P)
        VALID_PC.setKey(VALID_KEY)
        VALID_PC.setUser(VALID_U)
        VALID_PC.setComment(VALID_COMMENT)
        VALID_PC.setCreationDate(LocalDateTime.now())
        VALID_P.addComment(VALID_PC)

        and: "a valid post comment"
        VALID_PC_2 = new PostComment()
        VALID_PC_2.setPost(VALID_P)
        VALID_PC_2.setKey(VALID_KEY_2)
        VALID_PC_2.setUser(VALID_U)
        VALID_PC_2.setComment("ola")
        VALID_PC_2.setCreationDate(LocalDateTime.now())
        VALID_P.addComment(VALID_PC_2)

        COMMENT_LIST = new HashSet<>()
        COMMENT_LIST.add(new PostCommentDto(VALID_PC, false))
        COMMENT_LIST.add(new PostCommentDto(VALID_PC_2, false))

        VALID_C_LIST = new HashSet<>()
        VALID_C_LIST.add(new PostCommentDto(VALID_PC, false))

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

        and: "a valid postQuestion"
        def postQuestion1 = new PostQuestion()
        postQuestion1.setQuestion(question)
        postQuestion1.setUser(user1)
        postQuestion1.setStudentQuestion(VALID_STUDENT_QUESTION)

        and: "a valid post"
        def post1 = new Post(VALID_KEY, postQuestion1)
        postQuestion1.setPost(post1)
        user1.addPostQuestion(postQuestion1)

        and: "a valid comment"
        def comment = new PostComment()
        comment.setKey(VALID_KEY)
        comment.setComment(VALID_COMMENT)
        comment.setPost(post1)
        comment.setUser(user1)
        comment.setCreationDate(LocalDateTime.now())
        post1.addComment(comment)

        and: "a valid comment"
        def comment1 = new PostComment()
        comment1.setKey(VALID_KEY_2)
        comment1.setComment("ola")
        comment1.setPost(post1)
        comment1.setUser(user2)
        comment1.setCreationDate(LocalDateTime.now())
        post1.addComment(comment1)


        then: "add to repository"
        userRepository.save(user1)
        userRepository.save(user2)
        questionRepository.save(question)
        postRepository.save(post1)
        commentRepository.save(comment)
        commentRepository.save(comment1)
    }

    @Unroll
    def "valid search"() {
        when:
        def result = postService.searchComment(new String(string) as String)

        then:
        result.size() == size
        for(int i = 0; i < size; i++)
            assert result[i].getComment() == expected[i].getComment()
        where:
        string    | size              || expected
        "comment" | 1                 || VALID_C_LIST as Set<PostCommentDto>
        //"a"       | 2                 || COMMENT_LIST as Set<PostCommentDto>

    }


    @Unroll
    def "invalid search"() {
        when:
        postService.searchComment(new String(string) as String)

        then:
        def result = thrown(TutorException)
        result.message == expected


        where:
        string                          || expected
        "jvnnfvdfovkdofkvnojvn"         || ErrorMessage.INVALID_COMMENT_SEARCH.label
    }

    @TestConfiguration
    static class PostServiceImplTestContextConfiguration {
        @Bean
        PostService postService() {
            return new PostService()
        }
    }
}

