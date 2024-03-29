package pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.SuggestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.domain.Suggestion
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.repository.SuggestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Shared
import spock.lang.Specification

@DataJpaTest
class DeleteSuggestionTest extends Specification{
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String SUGGESTION_CONTENT = 'suggestion content'
    public static final String SUGGESTION_TITLE = 'suggestion title'
    public static final String OPTION_CONTENT = "optionId content"
    public static final String URL = 'URL'
    public static final int VALID_KEY = 1
    public static final int VALID_KEY2 = 2
    public static final int INVALID_KEY = -1
    public static final int VALID_ID = 1
    public static final int VALID_ID2 = 2
    public static final int INVALID_ID = -1
    public static final String VALID_NAME = "Ben Dover"
    public static final String REJECTED = "REJECTED"
    public static final String APPROVED = "APPROVED"
    public static final String VALID_USERNAME = "BenDover69"
    public static final String VALID_USERNAME_TEACHER = "something"
    public static final String VALID_NAME_TOPIC = "Spock"
    public static final String EMPTY_SUGGESTION = ""
    public static final String VALID_USERNAME_T = "Teacher"
    public static final String VALID_JUSTIFICATION = "approved/rejected"

    @Autowired
    SuggestionService suggestionService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    SuggestionRepository suggestionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    TopicRepository topicRepository

    @Shared
    def VALID_U

    @Shared
    def VALID_T

    @Shared
    def INVALID_U_UID

    @Shared
    def INVALID_U_UNAME

    @Shared
    def INVALID_U_ROLE

    @Shared
    def VALID_TOPIC

    @Shared
    def VALID_TOPIC_DTO

    @Shared
    def VALID_TOPIC_LIST

    @Shared
    def INVALID_TOPIC_LIST

    @Shared
    def suggestion

    @Shared
    def suggestion2
    @Shared
    def sug

    def sug2

    def course
    def courseExecution

    def setupSpec() {
        given: "a user with an invalid uid"
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


        and: "a valid user - STUDENT "
        VALID_U = new User(VALID_NAME, VALID_USERNAME, 1, User.Role.STUDENT)

        and: "a valid user - teacher "
        VALID_T = new User(VALID_NAME, VALID_USERNAME_TEACHER, 2, User.Role.TEACHER)
    }

    def setup(){
        given:
        course = new Course(COURSE_NAME, Course.Type.TECNICO)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        and: "a user with the role teacher"
        def userT = new User(VALID_NAME, VALID_USERNAME_TEACHER, 2, User.Role.TEACHER)


        and: "a user with the role student"
        def userS = new User(VALID_NAME, VALID_USERNAME, 1, User.Role.STUDENT)

        and: "a valid topicDto"
        VALID_TOPIC_DTO = new TopicDto()
        VALID_TOPIC_DTO.setId(VALID_ID)
        VALID_TOPIC_DTO.setName(VALID_NAME_TOPIC)

        and: "a valid topic"
        VALID_TOPIC = new Topic(course, VALID_TOPIC_DTO)


        and: "a valid list of topics"
        VALID_TOPIC_LIST = new HashSet<Topic>()
        VALID_TOPIC_LIST.add(VALID_TOPIC)

        and: "a invalid list of topics"
        INVALID_TOPIC_LIST = new HashSet<Topic>()

        and: "a suggestion"
        sug = new Suggestion()
        sug.setStudentQuestion(SUGGESTION_CONTENT as String)
        sug.setKey(VALID_KEY)
        sug.setCourse(courseExecution)

        sug.setTopicsList(VALID_TOPIC_LIST)
        sug.setId(VALID_ID)
        sug.setStudent(userS)
        sug.setTitle(SUGGESTION_TITLE)

        def option = new Option()
        option.setContent(OPTION_CONTENT)
        option.setCorrect(true)
        option.setSequence(1)
        def options = new ArrayList<Option>()
        options.add(option)
        sug.setOptions(options)


        then: "add to repository"
        courseRepository.save(course)
        courseExecutionRepository.save(courseExecution)
        userRepository.save(userS)
        userRepository.save(userT)
        topicRepository.save(VALID_TOPIC)
        suggestionRepository.save(sug)
    }


    def "valid deletion"() {
        when:
        suggestionService.deleteSuggestion(courseExecution.getId(),
                suggestionRepository.findAll().get(0).getId(), VALID_USERNAME )

        then:
        suggestionRepository.findAll().size()==0

    }

    @TestConfiguration
    static class SuggestionServiceImplTestContextConfiguration {

        @Bean
        SuggestionService suggestionService() {
            return new SuggestionService()
        }
    }
}