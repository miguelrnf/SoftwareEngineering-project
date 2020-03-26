package pt.ulisboa.tecnico.socialsoftware.tutor.post.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.post.PostService
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@DataJpaTest
class SubmitPostTest extends Specification {
    public static final String COURSE_NAME = 'TECNICO'
    public static final String VALID_QUESTION = 'This is a valid question'
    public static final String VALID_STUDENT_QUESTION = 'I am asking a valid question'
    public static final int VALID_COURSE_EXECUTION = 11
    public static final String EMPTY_QUESTION = ''
    public static final int VALID_KEY = 1
    public static final int INVALID_KEY = -1
    public static final int VALID_ID = 1
    public static final int INVALID_ID = -1
    public static final String VALID_NAME = "Ben Dover"
    public static final String VALID_USERNAME = "BenDover69"
    public static final String VALID_USERNAME_TEACHER = "something"
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
    CourseRepository courseRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    UserRepository userRepository

    @Shared
    def VALID_Q

    @Shared
    def INVALID_Q

    @Shared
    def VALID_U

    @Shared
    def INVALID_U_UID

    @Shared
    def INVALID_U_UNAME

    @Shared
    def INVALID_U_ROLE

    def setupSpec() {
        given: "a valid question"
        VALID_Q = new QuestionDto()
        VALID_Q.setId()
        VALID_Q.setKey(VALID_KEY)
        VALID_Q.setContent(VALID_QUESTION)

        and: "an invalid question"
        INVALID_Q = new QuestionDto()
        INVALID_Q.setKey(INVALID_KEY)
        INVALID_Q.setContent(VALID_QUESTION)

        and: "a valid user"
        VALID_U = new User()
        VALID_U.setId(VALID_ID)
        VALID_U.setRole(User.Role.STUDENT)
        VALID_U.setUsername(VALID_USERNAME)

        and: "a user with an invalid uid"
        INVALID_U_UID = new User()
        INVALID_U_UID.setId(INVALID_ID)
        INVALID_U_UID.setRole(User.Role.STUDENT)
        INVALID_U_UID.setUsername(VALID_USERNAME)

        and: "a user with the role teacher"
        INVALID_U_ROLE = new User()
        INVALID_U_ROLE.setId(VALID_ID)
        INVALID_U_ROLE.setRole(User.Role.TEACHER)
        INVALID_U_ROLE.setUsername(VALID_USERNAME_TEACHER)

        and: "a user with an invalid username"
        INVALID_U_UNAME = new User()
        INVALID_U_UNAME.setId(VALID_ID)
        INVALID_U_UNAME.setRole(User.Role.STUDENT)
    }

    def setup() {
        given: "a course"
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)

        and: "a question"
        def question = new Question()
        question.setKey(VALID_KEY)
        question.setContent(VALID_QUESTION)
        question.setStatus(Question.Status.AVAILABLE)
        question.setNumberOfAnswers(2)
        question.setNumberOfCorrect(1)

        and: "a quiz"
        def quiz = new Quiz()
        def quizQuestion = new QuizQuestion()
        quizQuestion.setQuestion(question)
        quiz.setKey(VALID_KEY)
        quiz.addQuizQuestion(quizQuestion)

        and: "a quiz answer"
        def user = new User(VALID_NAME, VALID_USERNAME, 1, User.Role.STUDENT)
        def quizAnswer = new QuizAnswer(user, quiz)
        def questionAnswer = new QuestionAnswer()
        questionAnswer.setQuizQuestion()
        quizAnswer.addQuestionAnswer()

        and: "a user that answered the question"
        user.addQuizAnswer(quizAnswer)

        and: "a user with the role teacher"
        def userT = new User(VALID_NAME, VALID_USERNAME_TEACHER, 2, User.Role.TEACHER)

        then: "add to repository"
        courseRepository.save(course)
        questionRepository.save(question)
        quizRepository.save(quiz)
        userRepository.save(user)
        userRepository.save(userT)
    }


    @Unroll
    def "valid submission"() {
        when:
        def pq = new PostQuestionDto()
        pq.setQuestion(q as QuestionDto)
        pq.setStudentQuestion(sq)
        pq.setUser(new UserDto (u as User))

        then:
        def result = postService.submitPost(pq)
        result.getQuestion().getStudentQuestion() == pq.getStudentQuestion()
        result.getQuestion().getUser().getUsername() == pq.getUser().getUsername()


        where:
        q       |       sq                  |       u
        VALID_Q |   VALID_STUDENT_QUESTION  |     VALID_U

    }

    @Unroll
    def "invalid fields"() {
        when:
        def pq = new PostQuestionDto()
        pq.setQuestion(q as QuestionDto)
        pq.setStudentQuestion(sq)
        pq.setUser(new UserDto (u as User))
        postService.submitPost(pq)

        then:
        def result = thrown(TutorException)
        result.message == expected


        where:
        q         |       sq                  |       u                 ||      expected
        VALID_Q   |   EMPTY_QUESTION          |   VALID_U               ||   ErrorMessage.NO_STUDENT_QUESTION.label
        VALID_Q   |   TOO_MANY_CHARS          |   VALID_U               ||   ErrorMessage.STUDENT_QUESTION_TOO_LONG.label
        INVALID_Q |   VALID_STUDENT_QUESTION  |   VALID_U               ||   ErrorMessage.QUESTION_NOT_FOUND.label.toString().replace("%d", "-1")

    }

    @Unroll
    def "invalid users"() {
        when:
        def pq = new PostQuestionDto()
        pq.setQuestion(q as QuestionDto)
        pq.setStudentQuestion(sq)
        pq.setUser(new UserDto (u as User))
        postService.submitPost(pq)

        then:
        def result = thrown(TutorException)
        result.message == expected

        where:
        q         |       sq                  |       u                 ||      expected
        VALID_Q   |   VALID_STUDENT_QUESTION  |   INVALID_U_UNAME       ||   ErrorMessage.USERNAME_NOT_FOUND.label
        VALID_Q   |   VALID_STUDENT_QUESTION  |   INVALID_U_ROLE        ||   ErrorMessage.USER_HAS_WRONG_ROLE.label

    }

    @TestConfiguration
    static class PostServiceImplTestContextConfiguration {
        @Bean
        PostService postService() {
            return new PostService()
        }
    }
}
