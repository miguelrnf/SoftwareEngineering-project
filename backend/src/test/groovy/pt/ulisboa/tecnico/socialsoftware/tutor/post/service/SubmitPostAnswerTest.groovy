package pt.ulisboa.tecnico.socialsoftware.tutor.post.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.post.PostService
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.Post
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.PostAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.PostQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.post.repository.PostRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Shared
import spock.lang.Specification

@DataJpaTest
class SubmitPostAnswerTest extends Specification {
    public static final String VALID_QUESTION = 'This is a valid question'
    public static final String VALID_STUDENT_QUESTION = 'I am asking a valid question'
    public static final String VALID_TEACHER_ANSWER = 'I AM ANSWER'
    public static final String EMPTY_QUESTION = ''
    public static final int VALID_KEY = 1
    public static final int VALID_ID = 1
    public static final String VALID_NAME = "Ben Dover"
    public static final String VALID_NAME_TEACHER = "Mike Litoris"
    public static final String VALID_USERNAME = "BenDover69"
    public static final String VALID_USERNAME_TEACHER = "MikeLitoris420"
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
    UserRepository userRepository

    @Shared
    def VALID_P

    @Shared
    def VALID_PQ

    @Shared
    def VALID_Q

    @Shared
    def VALID_U

    @Shared
    def VALID_PA

    @Shared
    def INVALID_PA_TOO_LONG

    @Shared
    def INVALID_PA_EMPTY

    @Shared
    def INVALID_PA_NOT_TEACHER

    @Shared
    def INVALID_U_NOT_TEACHER

    def setupSpec() {
        given: "a valid question"
        VALID_Q = new Question()
        VALID_Q.setId()
        VALID_Q.setKey(VALID_KEY)
        VALID_Q.setContent(VALID_QUESTION)

        and: "a valid user with the role teacher"
        VALID_U = new User()
        VALID_U.setId(VALID_ID)
        VALID_U.setRole(User.Role.TEACHER)
        VALID_U.setUsername(VALID_USERNAME_TEACHER)

        and: "a valid post question"
        VALID_PQ = new PostQuestion()
        VALID_PQ.setQuestion(VALID_Q)
        VALID_PQ.setUser(VALID_U)
        VALID_PQ.setStudentQuestion(VALID_STUDENT_QUESTION)

        and: "a valid post"
        VALID_P = new Post(VALID_KEY, VALID_PQ)

        and: "a valid post answer"
        VALID_PA = new PostAnswer()
        VALID_PA.setUser(VALID_U)
        VALID_PA.setTeacherAnswer(VALID_TEACHER_ANSWER)

        and: "a user with the role student"
        INVALID_U_NOT_TEACHER = new User()
        INVALID_U_NOT_TEACHER.setId(VALID_ID)
        INVALID_U_NOT_TEACHER.setRole(User.Role.STUDENT)
        INVALID_U_NOT_TEACHER.setUsername(VALID_USERNAME)

        and: "a post answer with too many chars"
        INVALID_PA_TOO_LONG = new PostAnswer()
        INVALID_PA_TOO_LONG.setUser(VALID_U)
        INVALID_PA_TOO_LONG.setTeacherAnswer(TOO_MANY_CHARS)

        and: "an empty post answer"
        INVALID_PA_EMPTY = new PostAnswer()
        INVALID_PA_EMPTY.setUser(VALID_U)
        INVALID_PA_EMPTY.setTeacherAnswer(EMPTY_QUESTION)

        and: "a post answer submitted by a user with the role student"
        INVALID_PA_NOT_TEACHER = new PostAnswer()
        INVALID_PA_NOT_TEACHER.setUser(INVALID_U_NOT_TEACHER)
        INVALID_PA_NOT_TEACHER.setTeacherAnswer(VALID_TEACHER_ANSWER)
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
        def user1 = new User(VALID_NAME_TEACHER, VALID_USERNAME_TEACHER, 1, User.Role.TEACHER)
        def user2 = new User(VALID_NAME, VALID_USERNAME, 2, User.Role.STUDENT)

        and: "a valid post question"
        def postQuestion1 = new PostQuestion()
        postQuestion1.setQuestion(question)
        postQuestion1.setUser(user1)
        postQuestion1.setStudentQuestion(VALID_STUDENT_QUESTION)

        and: "a valid post"
        def post1 = new Post(VALID_KEY, postQuestion1)
        postQuestion1.setPost(post1)
        user1.addPostQuestion(postQuestion1)
        user2.addPostQuestion(postQuestion1)

        then: "add to repository"
        userRepository.save(user1)
        userRepository.save(user2)
        postRepository.save(post1)
    }

    def "a valid submission"() {
        when:
        def result = postService.answerQuestion(new PostAnswerDto(pa))

        then:
        result.getUser().getUsername() == expected.getUser().getUsername()
        result.getTeacherAnswer() == expected.getTeacherAnswer()

        where:
        pa                       ||      expected
        VALID_PA as PostAnswer   ||  VALID_PA as PostAnswer
    }

    def "invalid fields"() {
        when:
        postService.answerQuestion(new PostAnswerDto(pa))

        then:
        def result = thrown(TutorException)
        result.message == expected

        where:
        pa                                  ||      expected
        INVALID_PA_EMPTY as PostAnswer      ||  ErrorMessage.INVALID_ANSWER_BLANK.label
        INVALID_PA_TOO_LONG as PostAnswer   ||  ErrorMessage.INVALID_ANSWER_TOO_LONG.label
        INVALID_PA_NOT_TEACHER as PostAnswer||  ErrorMessage.USER_HAS_WRONG_ROLE.label

    }

    @TestConfiguration
    static class PostServiceImplTestContextConfiguration {
        @Bean
        PostService postService() {
            return new PostService()
        }
    }
}
