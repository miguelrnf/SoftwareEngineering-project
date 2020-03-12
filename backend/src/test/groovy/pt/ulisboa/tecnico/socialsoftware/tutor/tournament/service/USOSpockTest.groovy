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

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.ASSESSMENT_NOT_FOUND
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.NOT_ENOUGH_QUESTIONS_TOURNAMENT
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_NOT_CONSISTENT

@DataJpaTest
class USOSpockTest extends Specification{
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    static final USERNAME_1 = 'username1'
    static final NAME = 'name'
    static final NUMQUESTIONS = 3
    static final DATEBEFORE = LocalDateTime.now().minusDays(1)
    static final DATENOW = LocalDateTime.now()
    static final DATETOMORROW = LocalDateTime.now().plusDays(1)

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

    def formatter
    def assdto
    def assdto_0
    def assdto_1
    def assdto_2
    def assdto_3
    def tournamentDto
    def course
    def courseExecution
    def userS
    def topic
    def topicConjunction

    def setup(){
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        given: "a user with the role student"
        def STUDENT = new User()
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
        def topicDto = new TopicDto()
        topicDto.setId(1)
        topicDto.setName(NAME)

        and: "a topic conjunction dto"
        def topicConjunctionDto = new TopicConjunctionDto()
        topicConjunctionDto.setId(1)
        topicConjunctionDto.addTopic(topicDto)

        and: " a valid assessments"
        assdto = new AssessmentDto()
        assdto.setId(1)
        assdto.setStatus(Assessment.Status.AVAILABLE.name())
        assdto.setTopicConjunctionsFromUnit(topicConjunctionDto)

        and: "a valid assessment"
        assdto_0 = new AssessmentDto()
        assdto_0.setId(2)
        assdto_0.setStatus(Assessment.Status.AVAILABLE.name())
        assdto_0.setTopicConjunctionsFromUnit(topicConjunctionDto)

        and: " some invalid assessments"
        assdto_1 = new AssessmentDto()
        assdto_1.setId(3)
        assdto_1.setStatus(Assessment.Status.DISABLED.name())
        assdto_1.setTopicConjunctionsFromUnit(topicConjunctionDto)

        and: " some invalid assessments"
        assdto_2 = new AssessmentDto()
        assdto_2.setId(4)
        assdto_2.setStatus(Assessment.Status.REMOVED.name())
        assdto_2.setTopicConjunctionsFromUnit(topicConjunctionDto)

        and: " some invalid assessments"
        assdto_3 = new AssessmentDto()
        assdto_3.setId(5)
        assdto_3.setStatus(Assessment.Status.AVAILABLE.name())
        assdto_3.setTopicConjunctionsFromUnit(topicConjunctionDto)

        and:
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
        def assr = assessmentService.createAssessment(courseExecution.getId(), assdto, true)
        tournamentDto.setAssessmentDto(assdto)
        println(assessmentRepository.findAll().dump())

        when:
        def result = tournamentService.createTournament(courseExecution.getId(), tournamentDto)

        then:"the return data are correct"
        result.availableDate == tournamentDto.availableDate
        result.conclusionDate == tournamentDto.conclusionDate
        result.assessmentDto == assr
    }

    def "invalid arguments"(){
        given:"an assessment"
        assessmentService.createAssessment(courseExecution.getId(), assdto_0, true)
        tournamentDto.setAvailableDate(DATENOW.format(formatter))
        tournamentDto.setConclusionDate(DATEBEFORE.format(formatter))
        tournamentDto.setAssessmentDto(assdto_0)
        tournamentDto.setNumberOfQuestions(NUMQUESTIONS)
        println(assessmentRepository.findAll().dump())

        when:
        tournamentService.createTournament(courseExecution.getId(), tournamentDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == TOURNAMENT_NOT_CONSISTENT
    }

    def "invalid arguments 2"(){
        given:"an assessment"
        assessmentService.createAssessment(courseExecution.getId(), assdto_1, true)
        tournamentDto.setAvailableDate(DATENOW.format(formatter))
        tournamentDto.setConclusionDate(DATETOMORROW.format(formatter))
        tournamentDto.setAssessmentDto(assdto_1)
        tournamentDto.setNumberOfQuestions(NUMQUESTIONS)
        println(assessmentRepository.findAll().dump())

        when:
        tournamentService.createTournament(courseExecution.getId(), tournamentDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == TOURNAMENT_NOT_CONSISTENT
    }

    def "invalid arguments - "(){
        given:"an assessment"
        assessmentService.createAssessment(courseExecution.getId(), assdto_2,true)
        tournamentDto.setAvailableDate(DATENOW.format(formatter))
        tournamentDto.setConclusionDate(DATETOMORROW.format(formatter))
        tournamentDto.setAssessmentDto(assdto_2)
        tournamentDto.setNumberOfQuestions(NUMQUESTIONS)

        when:
        tournamentService.createTournament(courseExecution.getId(), tournamentDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == TOURNAMENT_NOT_CONSISTENT
    }

    def "invalid arguments - Number of questions negative"(){
        given:"an assessment"
        assessmentService.createAssessment(courseExecution.getId(), assdto_3, true)
        tournamentDto.setAvailableDate(DATENOW.format(formatter))
        tournamentDto.setConclusionDate(DATETOMORROW.format(formatter))
        tournamentDto.setAssessmentDto(assdto_3)
        tournamentDto.setNumberOfQuestions(-1)

        when:
        tournamentService.createTournament(courseExecution.getId(), tournamentDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == NOT_ENOUGH_QUESTIONS_TOURNAMENT
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
