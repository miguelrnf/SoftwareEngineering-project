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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.AssessmentService
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

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.NOT_ENOUGH_QUESTIONS_TOURNAMENT
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_NOT_CONSISTENT

@DataJpaTest
class UserOptionsSpockTest extends Specification{
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    static final USERNAME_1 = 'username1'
    static final NAME = 'name'
    static final NUMQUESTIONS = 3
    static final DATEBEFORE = LocalDateTime.now().minusDays(1)
    static final DATENOW = LocalDateTime.now()
    static final DATETOMORROW = LocalDateTime.now().plusDays(1)
    static int tempId = 1

    @Autowired
    TournamentService tournamentService

    @Autowired
    AssessmentService assessmentService

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    TopicRepository topicRepository

    @Autowired
    TopicConjunctionRepository topicConjunctionRepository

    @Autowired
    AssessmentRepository assessmentRepository

    @Shared
    def formatter

    @Shared
    def ASSDTO

    @Shared
    def ASSDTO_0

    @Shared
    def ASSDTO_1

    @Shared
    def ASSDTO_2

    @Shared
    def ASSDTO_3

    @Shared
    def ASSDTO_4

    @Shared
    def tournamentDto

    @Shared
    def course

    @Shared
    def courseExecution

    @Shared
    def userS

    @Shared
    def topic

    @Shared
    def topicConjunction

    @Shared
    def topicDto

    @Shared
    def topicConjunctionDto

    @Shared
    def STUDENT


    def setupSpec(){
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        given: "a user with the role student"
        STUDENT = new User()
        STUDENT.setId(1)
        STUDENT.setRole(User.Role.STUDENT)
        STUDENT.setUsername(USERNAME_1)

        and: "a tournamentDto"
        tournamentDto = new TournamentDto()
        tournamentDto.setId(1)
        tournamentDto.setKey(1)
        tournamentDto.setStatus(Tournament.TournamentStatus.CREATED)
        tournamentDto.setOwner(new UserDto(STUDENT))
        tournamentDto.setNumberOfQuestions(3)
        tournamentDto.setAvailableDate(LocalDateTime.now().format(formatter))
        tournamentDto.setConclusionDate(LocalDateTime.now().plusDays(1).format(formatter))
        tournamentDto.setTitle("Title")

        and: "a topic dto"
        topicDto = new TopicDto()
        topicDto.setId(1)
        topicDto.setName(NAME)

        and: "a topic conjunction dto"
        topicConjunctionDto = new TopicConjunctionDto()
        topicConjunctionDto.setId(1)
        topicConjunctionDto.addTopic(topicDto)

        and: " a valid assessments"
        ASSDTO = new AssessmentDto()
        ASSDTO.setStatus(Assessment.Status.AVAILABLE.name())
        ASSDTO.setTopicConjunctionsFromUnit(topicConjunctionDto)

        and: "a valid assessment"
        ASSDTO_0 = new AssessmentDto()
        ASSDTO_0.setStatus(Assessment.Status.AVAILABLE.name())
        ASSDTO_0.setTopicConjunctionsFromUnit(topicConjunctionDto)

        and: "a invalid assessment"
        ASSDTO_1 = new AssessmentDto()
        ASSDTO_1.setStatus(Assessment.Status.DISABLED.name())
        ASSDTO_1.setTopicConjunctionsFromUnit(topicConjunctionDto)

        and: "a invalid assessment"
        ASSDTO_2 = new AssessmentDto()
        ASSDTO_2.setStatus(Assessment.Status.REMOVED.name())
        ASSDTO_2.setTopicConjunctionsFromUnit(topicConjunctionDto)

        and: "a invalid assessment"
        ASSDTO_3 = new AssessmentDto()
        ASSDTO_3.setStatus(Assessment.Status.AVAILABLE.name())
        ASSDTO_3.setTopicConjunctionsFromUnit(topicConjunctionDto)

        and: "a not saved assessment"
        ASSDTO_4 = new AssessmentDto()
        ASSDTO_4.setStatus(Assessment.Status.AVAILABLE.name())
        ASSDTO_4.setTopicConjunctionsFromUnit(topicConjunctionDto)

    }

    def setup(){
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        give:
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        userS = new User()
        userS.setKey(1)
        userS.setRole(User.Role.STUDENT)
        userS.setUsername(USERNAME_1)
        topic = new Topic(course, topicDto)
        topicConjunction = new TopicConjunction(topicConjunctionDto)

        then:"add to repository"
        userRepository.save(userS)
        topicRepository.save(topic)
        topicConjunctionRepository.save(topicConjunction)
        courseRepository.save(course)
        courseExecutionRepository.save(courseExecution)

    }

    def "valid options"(){
        given:"an assessment"
        ASSDTO.setId(tempId++)
        def assr = assessmentService.createAssessment(courseExecution.getId(), ASSDTO, true)
        tournamentDto.setAssessmentDto(ASSDTO)

        when:
        def result = tournamentService.createTournament(courseExecution.getId(), tournamentDto)

        then:"the return data are correct"
        result.availableDate == tournamentDto.availableDate
        result.conclusionDate == tournamentDto.conclusionDate
        result.assessmentDto == assr
    }

    @Unroll
    def "invalid arguments: assessmentDto=#assDto | AvailableDate=#avalDate | ConclusionDate = #concDate | NumberOfQuestions=#questNumb || errorMessage=#errorMessage t"(){
        (assDto as AssessmentDto).setId(tempId++)
        assessmentService.createAssessment(courseExecution.getId(), (assDto as AssessmentDto), true)
        tournamentDto.setAvailableDate(avalDate.format(formatter))
        tournamentDto.setConclusionDate(concDate.format(formatter))
        tournamentDto.setAssessmentDto(assDto as AssessmentDto)
        tournamentDto.setNumberOfQuestions(questNumb)

        when:
        tournamentService.createTournament(courseExecution.getId(), tournamentDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:

        assDto   | avalDate | concDate     | questNumb    || errorMessage
        ASSDTO_0 | DATENOW  | DATEBEFORE   | NUMQUESTIONS || TOURNAMENT_NOT_CONSISTENT
        ASSDTO_1 | DATENOW  | DATETOMORROW | NUMQUESTIONS || TOURNAMENT_NOT_CONSISTENT
        ASSDTO_2 | DATENOW  | DATETOMORROW | NUMQUESTIONS || TOURNAMENT_NOT_CONSISTENT
        ASSDTO_3 | DATENOW  | DATETOMORROW | -1           || NOT_ENOUGH_QUESTIONS_TOURNAMENT
    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
        @Bean
        AssessmentService assessmentService() {
            return new AssessmentService()
        }
    }

}
