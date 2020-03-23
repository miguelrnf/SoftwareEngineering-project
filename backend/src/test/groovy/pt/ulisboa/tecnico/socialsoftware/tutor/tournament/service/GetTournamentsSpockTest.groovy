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
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@DataJpaTest
class GetTournamentsSpockTest extends Specification{
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    static final USERNAME_1 = 'username1'
    static final TITLE1 = 'first tournament'
    static final TITLE2 = 'second tournament'
    static final String NAME = 'Name'
    static final DATENOW = LocalDateTime.now()
    static final DATETOMORROW = LocalDateTime.now().plusDays(1)
    static int tempId = 1
    static int tournId = 1
    static int userId = 1

    @Autowired
    TournamentService tournamentService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    TopicRepository topicRepository

    @Autowired
    TopicConjunctionRepository topicConjunctionRepository

    @Autowired
    AssessmentRepository assessmentRepository

    @Shared
    def tournamentDto1

    @Shared
    def tournamentDto2

    @Shared
    def STUDENT

    @Shared
    def course

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

    @Shared
    def user

    @Shared
    def courseExecution

    @Shared
    def formatter

    def setupSpec() {

        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        given: "a user with the role student"
        STUDENT = new User()
        STUDENT.setId(userId++)
        STUDENT.setRole(User.Role.STUDENT)
        STUDENT.setUsername(USERNAME_1)

        and: "some tournamentDtos"
        tournamentDto1 = new TournamentDto()
        tournamentDto1.setId(1)
        tournamentDto1.setKey(1)
        tournamentDto1.setStatus(Tournament.TournamentStatus.CREATED.name())
        tournamentDto1.setOwner(new UserDto(STUDENT))
        tournamentDto1.setTitle(TITLE1)
        tournamentDto1.setNumberOfQuestions(3)
        tournamentDto1.setAvailableDate(DATENOW.format(formatter))
        tournamentDto1.setConclusionDate(DATETOMORROW.format(formatter))


        tournamentDto2 = new TournamentDto()
        tournamentDto2.setId(2)
        tournamentDto2.setKey(2)
        tournamentDto2.setOwner(new UserDto(STUDENT))
        tournamentDto2.setTitle(TITLE2)
        tournamentDto2.setNumberOfQuestions(3)
        tournamentDto2.setAvailableDate(DATENOW.format(formatter))
        tournamentDto2.setConclusionDate(DATETOMORROW.format(formatter))


    }

    def setup() {
        given: "a course and a course execution"
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        user = new User()
        user.setKey(2)
        user.setRole(User.Role.STUDENT)
        user.setUsername(USERNAME_1)

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
        topicConjunction = new TopicConjunction()

        and:
        def tcl = new ArrayList<TopicConjunction>()
        tcl.add(topicConjunction)
        ass = new Assessment(courseExecution, tcl, assdto)


        then:"add to repository"
        courseRepository.save(course)
        courseExecutionRepository.save(courseExecution)
        userRepository.save(user)
        topicRepository.save(topic)
        topicConjunctionRepository.save(topicConjunction)
        assessmentRepository.save(ass)
    }

    @Unroll
    def "list tournaments"(){
        //all tournaments are listed
        given: "two tournaments"
        assdto.setId(tempId++)
        tournamentDto2.setStatus(status)
        tournamentDto1.setAssessmentDto(assdto)
        tournamentDto2.setAssessmentDto(assdto)
        tournamentService.createTournament(courseExecution.id, tournamentDto1)
        tournamentService.createTournament(courseExecution.id, tournamentDto2)
        def tournament1 = new Tournament(tournamentDto1, user, ass)
        def tournament2 = new Tournament(tournamentDto2, user, ass)
        tournament1.setId(tournId++)
        tournament2.setId(tournId++)

        when:
        def result = tournamentService.getTournaments(courseExecution.getId())

        then:
        result.contains(new TournamentDto(tournament1))
        result.contains(new TournamentDto(tournament2))
        result.size() == size

        where:
        status         || size
        "CANCELED"     ||  2
        "CREATED"      ||  2
        "OPEN"         ||  2
        "CLOSED"       ||  2
    }

    def "empty list"() {
        given: "nothing"

        when:
        def test = tournamentService.getTournaments(courseExecution.getId())

        then:
        test.isEmpty()

    }


    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}
