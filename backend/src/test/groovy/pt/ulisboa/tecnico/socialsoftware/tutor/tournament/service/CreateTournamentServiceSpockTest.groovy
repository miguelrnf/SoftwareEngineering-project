package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.TopicConjunction
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicConjunctionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.AssessmentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicConjunctionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_NOT_CONSISTENT
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_PERMISSION

@DataJpaTest
class CreateTournamentServiceSpockTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    static final USERNAME_1 = 'username1'
    static final USERNAME_2 = 'username2'
    static final USERNAME_3 = 'username3'
    static final TITLE = 'first tournament'
    static final NUMQUESTIONS = 3
    static final DATENOW = LocalDateTime.now()
    static final DATETOMORROW = LocalDateTime.now().plusDays(1)
    static final NAME = 'name'

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    TournamentService tournamentService

    @Autowired
    AssessmentRepository assessmentRepository

    @Autowired
    TopicRepository topicRepository

    @Autowired
    TopicConjunctionRepository topicConjunctionRepository

    @Shared
    def tournamentDto

    @Shared
    def course

    @Shared
    def courseExecution

    @Shared
    def creationDate

    @Shared
    def availableDate

    @Shared
    def conclusionDate

    @Shared
    def formatter

    @Shared
    def TEACHER

    @Shared
    def ADMIN

    @Shared
    def STUDENT

    @Shared
    def NLL_USERNAME

    @Shared
    def assdto

    @Shared
    def ass

    @Shared
    def topicDto

    @Shared
    def topicConjunctionDto

    @Shared
    def topic

    @Shared
    def topicConjunction


    def setupSpec() {

        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        creationDate = LocalDateTime.now()
        availableDate = LocalDateTime.now()
        conclusionDate = LocalDateTime.now().plusDays(1)

        and: "a tournamentDto"
        tournamentDto = new TournamentDto()
        tournamentDto.setId(1)
        tournamentDto.setKey(1)
        tournamentDto.setStatus(Tournament.TournamentStatus.CREATED.name())
        tournamentDto.setAvailableDate(DATENOW.format(formatter))
        tournamentDto.setConclusionDate(DATETOMORROW.format(formatter))
        tournamentDto.setNumberOfQuestions(NUMQUESTIONS)

        and: "a user with the role teacher"
        TEACHER = new User()
        TEACHER.setId(3)
        TEACHER.setRole(User.Role.TEACHER)
        TEACHER.setUsername(USERNAME_2)

        and: "a user with the role admin"
        ADMIN = new User()
        ADMIN.setId(2)
        ADMIN.setRole(User.Role.ADMIN)
        ADMIN.setUsername(USERNAME_3)

        and: "a user with the role student"
        STUDENT = new User()
        STUDENT.setId(1)
        STUDENT.setRole(User.Role.STUDENT)
        STUDENT.setUsername(USERNAME_1)

        and: "a user with the null username"
        NLL_USERNAME = new User()
        NLL_USERNAME.setId(1)
        NLL_USERNAME.setRole(User.Role.STUDENT)
        NLL_USERNAME.setUsername(null)
    }

    def setup() {
        given: "a course and a course execution"
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)

        and: "a topic dto"
        topicDto = new TopicDto()
        topicDto.setId(1)
        topicDto.setName(NAME)

        and: "a topic conjunction dto"
        topicConjunctionDto = new TopicConjunctionDto()
        topicConjunctionDto.setId(1)
        topicConjunctionDto.addTopic(topicDto)

        and: " a valid assessments"
        assdto = new AssessmentDto()
        assdto.setId(1)
        assdto.setStatus(Assessment.Status.AVAILABLE.name())
        assdto.setTopicConjunctionsFromUnit(topicConjunctionDto)
        topic = new Topic(course, topicDto)
        topicConjunction = new TopicConjunction(topicConjunctionDto)

        and:
        def tcl = new ArrayList<TopicConjunction>()
        tcl.add(topicConjunction)
        ass = new Assessment(courseExecution, tcl, assdto)


        and: "a user with the role student"
        def userS = new User('name', USERNAME_1, 1, User.Role.STUDENT)

        def userT = new User('name', USERNAME_2, 2, User.Role.TEACHER)

        def userA = new User('name', USERNAME_3, 3, User.Role.ADMIN)


        then:"add to repository"
        courseRepository.save(course)
        courseExecutionRepository.save(courseExecution)
        userRepository.save(userS)
        userRepository.save(userT)
        userRepository.save(userA)
        topicRepository.save(topic)
        topicConjunctionRepository.save(topicConjunction)
        assessmentRepository.save(ass)
    }

    def "student creates a tournament"() {
       given:
       tournamentDto.setOwner(new UserDto(STUDENT))
       tournamentDto.setTitle(TITLE)
       tournamentDto.setAssessmentDto(assdto)

       when:
       def result = tournamentService.createTournament(courseExecution.getId(), tournamentDto)

       then:"the return data are correct"
       result.id != null
       result.key == 1
       result.owner.getName() == 'name'
       result.owner.getRole() == User.Role.STUDENT
       result.title == TITLE
       result.status == "CREATED"
    }

    def "null user creates a tournament"() {
        given: "a null user"
        tournamentDto.setOwner(null)
        tournamentDto.setTitle(TITLE)

        when:
        tournamentService.createTournament(courseExecution.getId(), tournamentDto)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_NOT_CONSISTENT
    }


    @Unroll
    def "invalid arguments: user=#user | title=#title || errorMessage=#errorMessage "() {
        given:
        tournamentDto.setOwner(new UserDto(user as User))
        tournamentDto.setTitle(title)


        when:
        tournamentService.createTournament(courseExecution.getId(), tournamentDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
             user     | title || errorMessage
            TEACHER   | TITLE || TOURNAMENT_PERMISSION
             ADMIN    | TITLE || TOURNAMENT_PERMISSION
         NLL_USERNAME | TITLE || TOURNAMENT_NOT_CONSISTENT
            STUDENT   | null  || TOURNAMENT_NOT_CONSISTENT
            STUDENT   | '  '  || TOURNAMENT_NOT_CONSISTENT
    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}
