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
class RedirectDuplicatePostTest extends Specification{
    public static final String VALID_QUESTION = 'This is a valid question'
    public static final String VALID_QUESTION_2 = 'This is a valid question too'
    public static final String VALID_STUDENT_QUESTION = 'I am asking a valid question'
    public static final String VALID_TEACHER_ANSWER = 'I AM ANSWER'
    public static final int VALID_KEY = 1
    public static final int VALID_KEY_2 = 2
    public static final int VALID_KEY_3 = 3
    public static final int VALID_KEY_4 = 4
    public static final int VALID_KEY_NOT_SAVED = 2
    public static final int VALID_KEY_NOT_ANSWERED = 3
    public static final int INVALID_KEY = -1
    public static final int VALID_ID_1 = 1
    public static final int VALID_ID_2 = 2
    public static final String VALID_NAME_1 = "Ben Dover"
    public static final String VALID_NAME_2 = "Mike Litoris"
    public static final String VALID_USERNAME_1 = "BenDover69"
    public static final String VALID_USERNAME_2 = "MikeLitoris420"
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
    def VALID_P_1

    @Shared
    def VALID_P_2

    @Shared
    def VALID_P_NO_ANSWER

    @Shared
    def VALID_Q

    @Shared
    def VALID_PA

    @Shared
    def VALID_Q_DIFFERENT

    @Shared
    def VALID_U_STUDENT

    @Shared
    def VALID_U_TEACHER

    @Shared
    def VALID_PQ_1

    @Shared
    def VALID_PQ_2

    @Shared
    def VALID_PQ_3

    @Shared
    def VALID_PQ_DIFFERENT

    @Shared
    def VALID_PA_DIFFERENT

    @Shared
    def INVALID_P_NOT_ANSWERED

    @Shared
    def VALID_P_DIFFERENT

    def setupSpec() {
        given: "a valid question"
        VALID_Q = new Question()
        VALID_Q.setKey(VALID_KEY)
        VALID_Q.setContent(VALID_QUESTION)
        VALID_Q.setStatus(Question.Status.AVAILABLE)
        VALID_Q.setNumberOfAnswers(2)
        VALID_Q.setNumberOfCorrect(1)

        given: "a different valid question"
        VALID_Q_DIFFERENT = new Question()
        VALID_Q_DIFFERENT.setKey(VALID_KEY_2)
        VALID_Q_DIFFERENT.setContent(VALID_QUESTION_2)
        VALID_Q_DIFFERENT.setStatus(Question.Status.AVAILABLE)
        VALID_Q_DIFFERENT.setNumberOfAnswers(2)
        VALID_Q_DIFFERENT.setNumberOfCorrect(1)

        and:"a valid student"
        VALID_U_STUDENT = new User()
        VALID_U_STUDENT.setId(VALID_ID_1)
        VALID_U_STUDENT.setRole(User.Role.STUDENT)
        VALID_U_STUDENT.setUsername(VALID_USERNAME_1)

        and:"a valid teacher"
        VALID_U_TEACHER = new User()
        VALID_U_TEACHER.setId(VALID_ID_2)
        VALID_U_TEACHER.setRole(User.Role.TEACHER)
        VALID_U_TEACHER.setUsername(VALID_USERNAME_2)

        and: "a valid postQuestion1 - Q"
        VALID_PQ_1 = new PostQuestion()
        VALID_PQ_1.setQuestion(VALID_Q)
        VALID_PQ_1.setUser(VALID_U_STUDENT)
        VALID_PQ_1.setStudentQuestion(VALID_STUDENT_QUESTION)

        and: "a valid postQuestion2 - Q"
        VALID_PQ_2 = new PostQuestion()
        VALID_PQ_2.setQuestion(VALID_Q)
        VALID_PQ_2.setUser(VALID_U_STUDENT)
        VALID_PQ_2.setStudentQuestion(VALID_STUDENT_QUESTION)

        and: "a valid postQuestion3 - Q"
        VALID_PQ_3 = new PostQuestion()
        VALID_PQ_3.setQuestion(VALID_Q)
        VALID_PQ_3.setUser(VALID_U_STUDENT)
        VALID_PQ_3.setStudentQuestion(VALID_STUDENT_QUESTION)

        and: "a valid postQuestion - Q different"
        VALID_PQ_DIFFERENT = new PostQuestion()
        VALID_PQ_DIFFERENT.setQuestion(VALID_Q_DIFFERENT)
        VALID_PQ_DIFFERENT.setUser(VALID_U_STUDENT)
        VALID_PQ_DIFFERENT.setStudentQuestion(VALID_STUDENT_QUESTION)

        and: "a valid post - Q1"
        VALID_P_1 = new Post()
        VALID_P_1.setKey(VALID_KEY)
        VALID_P_1.setQuestion(VALID_PQ_1)

        and: "a valid post - Q2"
        VALID_P_2 = new Post()
        VALID_P_2.setKey(VALID_KEY_2)
        VALID_P_2.setQuestion(VALID_PQ_2)

        and: "a valid post - Q3 no answer"
        VALID_P_NO_ANSWER = new Post()
        VALID_P_NO_ANSWER.setKey(VALID_KEY_4)
        VALID_P_NO_ANSWER.setQuestion(VALID_PQ_2)

        and: "a valid post - Q different"
        VALID_P_DIFFERENT = new Post()
        VALID_P_DIFFERENT.setKey(VALID_KEY_3)
        VALID_P_DIFFERENT.setQuestion(VALID_PQ_DIFFERENT)


        and: "a post that was not answered"
        INVALID_P_NOT_ANSWERED = new Post()
        INVALID_P_NOT_ANSWERED.setKey(VALID_KEY_NOT_ANSWERED)

        and: "a valid post answer"
        VALID_PA = new PostAnswer()
        VALID_PA.setUser(VALID_U_STUDENT)
        VALID_PA.setTeacherAnswer(VALID_TEACHER_ANSWER)
        VALID_PA.setPost(VALID_P_2)
        VALID_P_2.setAnswer(VALID_PA)

        and: "a valid post answer DIFFERENT"
        VALID_PA_DIFFERENT = new PostAnswer()
        VALID_PA_DIFFERENT.setUser(VALID_U_STUDENT)
        VALID_PA_DIFFERENT.setTeacherAnswer(VALID_TEACHER_ANSWER)
        VALID_PA_DIFFERENT.setPost(VALID_P_DIFFERENT)
        VALID_P_DIFFERENT.setAnswer(VALID_PA_DIFFERENT)


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

        and: "two valid users"
        def user1 = new User(VALID_NAME_1, VALID_USERNAME_1, 1, User.Role.STUDENT)
        def user2 = new User(VALID_NAME_2, VALID_USERNAME_2, 2, User.Role.TEACHER)

        and: "two valid postQuestions"
        def postQuestion1 = new PostQuestion()
        postQuestion1.setQuestion(question)
        postQuestion1.setUser(user1)
        postQuestion1.setStudentQuestion(VALID_STUDENT_QUESTION)
        def postQuestion2 = new PostQuestion()
        postQuestion2.setQuestion(question)
        postQuestion2.setUser(user1)
        postQuestion2.setStudentQuestion(VALID_STUDENT_QUESTION)
        def postQuestion3 = new PostQuestion()
        postQuestion3.setQuestion(question)
        postQuestion3.setUser(user1)
        postQuestion3.setStudentQuestion(VALID_STUDENT_QUESTION)
        def postQuestion4 = new PostQuestion()
        postQuestion4.setQuestion(question)
        postQuestion4.setUser(user1)
        postQuestion4.setStudentQuestion(VALID_STUDENT_QUESTION)

        and: "two valid posts"
        def post1 = new Post(VALID_KEY, postQuestion1)
        def post2 = new Post(VALID_KEY_2, postQuestion2)
        def post3 = new Post(VALID_KEY_3, postQuestion3)
        def post4 = new Post(VALID_KEY_4, postQuestion3)
        postQuestion1.setPost(post1)
        postQuestion2.setPost(post2)
        postQuestion3.setPost(post3)
        postQuestion4.setPost(post4)
        user1.addPostQuestion(postQuestion1)
        user1.addPostQuestion(postQuestion2)
        user1.addPostQuestion(postQuestion3)
        user1.addPostQuestion(postQuestion4)

        and: "a valid post answer"
        def pa = new PostAnswer(user2, VALID_TEACHER_ANSWER)
        def pa2 = new PostAnswer(user2, VALID_TEACHER_ANSWER)
        pa.setPost(post2)
        post2.setAnswer(pa)
        pa.setPost(post3)
        post3.setAnswer(pa2)

        then: "add to repository"
        userRepository.save(user1)
        userRepository.save(user2)
        questionRepository.save(question)
        postRepository.save(post1)
        postRepository.save(post2)
        postRepository.save(post3)
        postRepository.save(post4)
    }

    @Unroll
    def "valid redirect"() {
        when:
        def post1 = new PostDto(p1)
        def post2 = new PostDto(p2)
        def userT = new UserDto(user)
        user.setId(VALID_ID_2)
        def result = postService.redirect(post1, post2, userT)

        then:
        result.getAnswer().getTeacherAnswer() == post2.getAnswer().getTeacherAnswer()
        !result.getDiscussStatus()
        result.getPostStatus()

        where:
        p1                 | p2                 | user                      || expected
        VALID_P_1 as Post  | VALID_P_2 as Post  | VALID_U_TEACHER as User   || VALID_P_2 as Post


    }

    @Unroll
    def "invalid redirect"() {
        when:
        def post1 = new PostDto(p1)
        def post2 = new PostDto(p2)
        def userT = new UserDto(user)
        user.setId(VALID_ID_2)
        postService.redirect(post1, post2, userT)

        then:
        def result = thrown(TutorException)
        result.message == expected

        where:
        p1                 | p2                         | user                      || expected
        VALID_P_1 as Post  | VALID_P_DIFFERENT as Post  | VALID_U_TEACHER as User   || ErrorMessage.DIFFERENT_QUESTION.label
        VALID_P_1 as Post  | VALID_P_NO_ANSWER as Post  | VALID_U_TEACHER as User   || ErrorMessage.NO_ANSWER.label
        VALID_P_1 as Post  | VALID_P_2 as Post          | VALID_U_STUDENT as User   || ErrorMessage.USER_HAS_WRONG_ROLE.label

    }

    @TestConfiguration
    static class PostServiceImplTestContextConfiguration {
        @Bean
        PostService postService() {
            return new PostService()
        }
    }

}
