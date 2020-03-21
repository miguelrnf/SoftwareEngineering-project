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
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostDto
import pt.ulisboa.tecnico.socialsoftware.tutor.post.repository.PostRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@DataJpaTest
class EditAnswerTest extends Specification {
    public static final String VALID_QUESTION = 'This is a valid question'
    public static final String VALID_STUDENT_QUESTION = 'I am asking a valid question'
    public static final String VALID_ANSWER = 'I am a valid answer'
    public static final String INVALID_ANSWER_BLANK = ' '
    public static final String INVALID_ANSWER_TOO_LONG = '5EdnCpIJFNNr0enpzluxNDqldKmHf6TZvTeLpj6laJPTYaZeI3DYv9KGVXtykpTq0hjXtS75Y3VhBlHlPPI3E1HlmHNI5pH\' +\n' +
            '                    \'5QYoF24hA7Dd8z6nxA8NStjuugQmKMuZYKV5jugeFtcqt2yoT4LzVMtAvtB7jGMQ8ua4Pxm1QifflguBuJDNmXdtNkpwX3l\' +\n' +
            '                    \'smTxBwLMoIvXebgzIDG20yloMadbiY8RT7IcMYSRkAFZJqT4xS6Zr9MXLA0ceLetDLYTvWkXokBo4hGcq4cWhXTF6EpK9JV\' +\n' +
            '                    \'wzx7qH0ftW3nakG1dYonRLFvsvAHDby5eomorAjt35goeFCKiiqGd69NIZhVJm7LIqArqndg52o0cUGIm064b0TbxN9naBy\' +\n' +
            '                    \'3bFhiDjNh22m8XPgbVh4OHloTKVW0xELfMPDWXjeVsXQrEudyKuUrJKzhRBGoxkXHgNcnASnGTfSm4VQGVsQbuWGiOQ7RLn\' +\n' +
            '                    \'bs1xN568BiF1vQ3MTHvhS922likFcpOEnTqpG41UOiSYJuGXnybo1Pr5fNirhtBhf2lQTKlX6Np0EsshuDjGqBqjWJg3npy\' +\n' +
            '                    \'vwdgl6bAjG7wt0RaDf6g22iIv5dys9KCd6mZB5mCh1W1naEAaUZkS0d52fwhciRVmAUqlQxmfdl650O14FntY93IWILmChH\' +\n' +
            '                    \'wa7vsaXqFJbMM5c4hN2M2mCjVbQYimYHGty6JaJGJ6vBAjkFqRRTC98u5xLBijcUgIfswDdO2JatYs5feyUEEvlB4WO3QcF\' +\n' +
            '                    \'jYzZdZKjgpNfNXc86zBD3cgUZpk30wSsxN1MgVutK335D4KmqyMcZ5vD4fBoZtJPgAcjOFLSJucrZVr28ln8wL404Slm3EA\' +\n' +
            '                    \'VBiTuQJWUYlaMJ33nJh6UGeZx1Hwj0fThLq2UtWN9KsBRKnvPl54Ixprv4bisZ6CLYoIh4bhNNZGzVTYmchtvYfVahe153l\' +\n' +
            '                    \'9vusIYIQt7jSaqSrzlzdS94a1qa3rKiGqvFpgBsbPvzQ6eDpA6WgSTrUgJTuNr5ns1UNG2Y0KqGqvFpgBsbPvzQ6eDpA6Wg\' +\n' +
            '                    \'5EdnCpIJFNNr0enpzluxNDqldKmHf6TZvTeLpj6laJPTYaZeI3DYv9KGVXtykpTq0hjXtS75Y3VhBlHlPPI3E1HlmHNI5pH\' +\n' +
            '                    \'5QYoF24hA7Dd8z6nxA8NStjuugQmKMuZYKV5jugeFtcqt2yoT4LzVMtAvtB7jGMQ8ua4Pxm1QifflguBuJDNmXdtNkpwX3l\' +\n' +
            '                    \'smTxBwLMoIvXebgzIDG20yloMadbiY8RT7IcMYSRkAFZJqT4xS6Zr9MXLA0ceLetDLYTvWkXokBo4hGcq4cWhXTF6EpK9JV\' +\n' +
            '                    \'wzx7qH0ftW3nakG1dYonRLFvsvAHDby5eomorAjt35goeFCKiiqGd69NIZhVJm7LIqArqndg52o0cUGIm064b0TbxN9naBy\' +\n' +
            '                    \'3bFhiDjNh22m8XPgbVh4OHloTKVW0xELfMPDWXjeVsXQrEudyKuUrJKzhRBGoxkXHgNcnASnGTfSm4VQGVsQbuWGiOQ7RLn\' +\n' +
            '                    \'bs1xN568BiF1vQ3MTHvhS922likFcpOEnTqpG41UOiSYJuGXnybo1Pr5fNirhtBhf2lQTKlX6Np0EsshuDjGqBqjWJg3npy\' +\n' +
            '                    \'vwdgl6bAjG7wt0RaDf6g22iIv5dys9KCd6mZB5mCh1W1naEAaUZkS0d52fwhciRVmAUqlQxmfdl650O14FntY93IWILmChH\' +\n' +
            '                    \'wa7vsaXqFJbMM5c4hN2M2mCjVbQYimYHGty6JaJGJ6vBAjkFqRRTC98u5xLBijcUgIfswDdO2JatYs5feyUEEvlB4WO3QcF\' +\n' +
            '                    \'jYzZdZKjgpNfNXc86zBD3cgUZpk30wSsxN1MgVutK335D4KmqyMcZ5vD4fBoZtJPgAcjOFLSJucrZVr28ln8wL404Slm3EA\' +\n' +
            '                    \'VBiTuQJWUYlaMJ33nJh6UGeZx1Hwj0fThLq2UtWN9KsBRKnvPl54Ixprv4bisZ6CLYoIh4bhNNZGzVTYmchtvYfVahe153l\' +\n' +
            '                    \'9vusIYIQt7jSaqSrzlzdS94a1qa3rKiSTrx_'
    public static final int VALID_KEY = 1
    public static final int VALID_KEY_2 = 2
    public static final int VALID_KEY_3 = 3
    public static final int VALID_KEY_4 = 4
    public static final int VALID_KEY_5 = 5
    public static final int VALID_ID = 1
    public static final int VALID_ID_2 = 2
    public static final String VALID_NAME_1 = "Ben Dover"
    public static final String VALID_USERNAME_1 = "BenDover69"
    public static final String VALID_NAME_2 = "Mickey"
    public static final String VALID_USERNAME_2 = "Mickey123"


    @Autowired
    PostService postService

    @Autowired
    PostRepository postRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    UserRepository userRepository

    @Shared
    def VALID_Q

    @Shared
    def VALID_U_TEACHER

    @Shared
    def INVALID_U_NOT_TEACHER

    @Shared
    def STUDENT


    @Shared
    def VALID_PQ

    @Shared
    def VALID_PQ_2

    @Shared
    def VALID_PQ_3

    @Shared
    def VALID_PQ_4

    @Shared
    def VALID_PQ_5

    @Shared
    def VALID_P_NO_ANSWER

    @Shared
    def VALID_P_WITH_ANSWER

    @Shared
    def VALID_PA

    @Shared
    def INVALID_PA_NO_ANSWER

    @Shared
    def INVALID_PA_EMPTY

    @Shared
    def INVALID_PA_TOO_LONG

    @Shared
    def INVALID_P_NO_ANSWER

    @Shared
    def INVALID_P_ANSWER_EMPTY

    @Shared
    def INVALID_P_ANSWER_TOO_LONG



    def setupSpec() {
        given: "a valid question"
        VALID_Q = new Question()
        VALID_Q.setKey(VALID_KEY)
        VALID_Q.setContent(VALID_QUESTION)
        VALID_Q.setStatus(Question.Status.AVAILABLE)
        VALID_Q.setNumberOfAnswers(2)
        VALID_Q.setNumberOfCorrect(1)

        and: "a valid user to edit(teacher)"
        VALID_U_TEACHER = new User()
        VALID_U_TEACHER.setId(VALID_ID)
        VALID_U_TEACHER.setRole(User.Role.TEACHER)
        VALID_U_TEACHER.setUsername(VALID_USERNAME_1)

        and: "an invalid user"
        INVALID_U_NOT_TEACHER = new User()
        INVALID_U_NOT_TEACHER.setId(VALID_ID_2)
        INVALID_U_NOT_TEACHER.setRole(User.Role.STUDENT)
        INVALID_U_NOT_TEACHER.setUsername(VALID_USERNAME_2)

        and: "a valid postQuestion"
        VALID_PQ = new PostQuestion()
        VALID_PQ.setQuestion(VALID_Q)
        VALID_PQ.setUser(INVALID_U_NOT_TEACHER)
        VALID_PQ.setStudentQuestion(VALID_STUDENT_QUESTION)

        and: "a valid postQuestion2"
        VALID_PQ_2 = new PostQuestion()
        VALID_PQ_2.setQuestion(VALID_Q)
        VALID_PQ_2.setUser(INVALID_U_NOT_TEACHER)
        VALID_PQ_2.setStudentQuestion(VALID_STUDENT_QUESTION)

        and: "a valid postQuestion3"
        VALID_PQ_3 = new PostQuestion()
        VALID_PQ_3.setQuestion(VALID_Q)
        VALID_PQ_3.setUser(INVALID_U_NOT_TEACHER)
        VALID_PQ_3.setStudentQuestion(VALID_STUDENT_QUESTION)

        and: "a valid postQuestion4"
        VALID_PQ_4 = new PostQuestion()
        VALID_PQ_4.setQuestion(VALID_Q)
        VALID_PQ_4.setUser(INVALID_U_NOT_TEACHER)
        VALID_PQ_4.setStudentQuestion(VALID_STUDENT_QUESTION)

        and: "a valid postQuestion5"
        VALID_PQ_5 = new PostQuestion()
        VALID_PQ_5.setQuestion(VALID_Q)
        VALID_PQ_5.setUser(INVALID_U_NOT_TEACHER)
        VALID_PQ_5.setStudentQuestion(VALID_STUDENT_QUESTION)

        and: "a valid post with no answer(INVALID)"
        VALID_P_NO_ANSWER = new Post()
        VALID_P_NO_ANSWER.setKey(VALID_KEY)
        VALID_P_NO_ANSWER.setQuestion(VALID_PQ)

        and: "a valid post with empty answer"
        INVALID_P_ANSWER_EMPTY = new Post()
        INVALID_P_ANSWER_EMPTY.setKey(VALID_KEY_2)
        INVALID_P_ANSWER_EMPTY.setQuestion(VALID_PQ_2)

        and: "a valid post with empty answer"
        INVALID_P_ANSWER_TOO_LONG = new Post()
        INVALID_P_ANSWER_TOO_LONG.setKey(VALID_KEY_2)
        INVALID_P_ANSWER_TOO_LONG.setQuestion(VALID_PQ_4)

        and: "a valid post with a valid answer"
        VALID_P_WITH_ANSWER = new Post(VALID_KEY_3, VALID_PQ_3)

        and: "a valid postAnswer"
        VALID_PA = new PostAnswer()
        VALID_PA.setUser(VALID_U_TEACHER)
        VALID_PA.setTeacherAnswer(VALID_ANSWER)
        VALID_PA.setPost(VALID_P_WITH_ANSWER)
        VALID_P_WITH_ANSWER.setAnswer(VALID_PA)


        and: "an invalid postAnswer - no answer"
        INVALID_PA_NO_ANSWER = new PostAnswer()
        INVALID_PA_NO_ANSWER.setUser(VALID_U_TEACHER)
        INVALID_PA_NO_ANSWER.setTeacherAnswer(null)
        INVALID_PA_NO_ANSWER.setPost(VALID_P_NO_ANSWER)
        VALID_P_NO_ANSWER.setAnswer(INVALID_PA_NO_ANSWER)

        and: "an invalid postAnswer - empty answer"
        INVALID_PA_EMPTY = new PostAnswer()
        INVALID_PA_EMPTY.setUser(VALID_U_TEACHER)
        INVALID_PA_EMPTY.setTeacherAnswer(INVALID_ANSWER_BLANK)
        INVALID_PA_EMPTY.setPost(INVALID_P_ANSWER_EMPTY)
        INVALID_P_ANSWER_EMPTY.setAnswer(INVALID_PA_EMPTY)

        and: "an invalid postAnswer - answer too long"
        INVALID_PA_TOO_LONG = new PostAnswer()
        INVALID_PA_TOO_LONG.setUser(VALID_U_TEACHER)
        INVALID_PA_TOO_LONG.setTeacherAnswer(INVALID_ANSWER_TOO_LONG)
        INVALID_PA_TOO_LONG.setPost(INVALID_P_ANSWER_TOO_LONG)
        INVALID_P_ANSWER_TOO_LONG.setAnswer(INVALID_PA_TOO_LONG)

       
        
        
        
        
        
        
        
        
        println('-' * 50)
        println('-' * 50)
        println(VALID_P_WITH_ANSWER.dump())
        println('-' * 50)
        println('-' * 50)
        println(VALID_PA.dump())
        println('-' * 50)
        println('-' * 50)


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
        def user1 = new User(VALID_NAME_1, VALID_USERNAME_1, 1, User.Role.TEACHER)
        def user2 = new User(VALID_NAME_2, VALID_USERNAME_2, 2, User.Role.STUDENT)

        and: "a valid postQuestion1"
        def postQuestion1 = new PostQuestion()
        postQuestion1.setQuestion(question)
        postQuestion1.setUser(user2)
        postQuestion1.setStudentQuestion(VALID_STUDENT_QUESTION)

        and: "a valid post"
        def post = new Post(VALID_KEY_3, postQuestion1)
        postQuestion1.setPost(post)
        user2.addPostQuestion(postQuestion1)

        and: "a valid postAnswer"
        def postAnswer = new PostAnswer()
        postAnswer.setUser(user1)
        postAnswer.setTeacherAnswer(VALID_ANSWER)
        post.setAnswer(postAnswer)




        then: "add to repository"
        userRepository.save(user1)
        userRepository.save(user2)
        questionRepository.save(question)
        postRepository.save(post)


    }


    @Unroll
    def "valid edit"() {
        when:
        def dto = new PostAnswerDto(VALID_PA)
        def post = new PostDto(VALID_P_WITH_ANSWER)
        dto.setPost(post)
        def result = postService.editAnswer(dto, new UserDto(user))

        then:
        result.getKey() == result.getKey()
        result.getAnswer() == result.getAnswer()

        where:
        user                         || expected
        VALID_U_TEACHER as User      || VALID_P_WITH_ANSWER as Post


    }

    @Unroll
    def "invalid edit"() {
        when:
        def dto = new PostAnswerDto(pa)
        def post = new PostDto(VALID_P_WITH_ANSWER)
        dto.setPost(post)
        postService.editAnswer(dto, new UserDto(user))

        then:
        def result = thrown(TutorException)
        result.message == expected

        where:
        user                            | pa                                  || expected
        VALID_U_TEACHER as User         | INVALID_PA_NO_ANSWER as PostAnswer  || ErrorMessage.NO_ANSWER.label
        VALID_U_TEACHER as User         | INVALID_PA_TOO_LONG as PostAnswer   || ErrorMessage.INVALID_ANSWER_TOO_LONG.label
        VALID_U_TEACHER as User         | INVALID_PA_EMPTY as PostAnswer      || ErrorMessage.INVALID_ANSWER_BLANK.label
        INVALID_U_NOT_TEACHER as User   | VALID_PA as PostAnswer              || ErrorMessage.USER_HAS_WRONG_ROLE.label

    }

    @TestConfiguration
    static class PostServiceImplTestContextConfiguration {
        @Bean
        PostService postService() {
            return new PostService()
        }
    }

}
