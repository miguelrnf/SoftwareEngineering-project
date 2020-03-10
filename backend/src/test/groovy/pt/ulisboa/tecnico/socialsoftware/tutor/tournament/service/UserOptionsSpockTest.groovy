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

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_NOT_CONSISTENT
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.NOT_ENOUGH_QUESTIONS_TOURNAMENT
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_PERMISSION


@DataJpaTest
class UserOptionsSpockTest extends Specification{
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    static final USERNAME_1 = 'username1'
    static final TITLE = 'first tournament'
    static final NAME = 'name'
    static final NUMQUESTIONS = 3
    static final DATEBEFORE = LocalDateTime.now().minusDays(1)
    static final DATENOW = LocalDateTime.now()
    static final DATETOMORROW = LocalDateTime.now().plusDays(1)

    @Autowired
    TournamentService tournamentService

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
    def STUDENT

    @Shared
    def assessment

    @Shared
    def VALID_ASSESSMENT

    @Shared
    def INVALID_ASSESSMENT_1

    @Shared
    def INVALID_ASSESSMENT_2

    @Shared
    def INVALID_ASSESSMENT_3

    @Shared
    def INVALID_ASSESSMENT_4

    @Shared
    def TOPIC_CONJUCTIONS

    @Shared
    def TOPIC

    @Shared
    def VALID_COURSE

    @Shared
    def VALID_COURSEEXECUT


    def setupSpec(){
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        creationDate = LocalDateTime.now()

        give: "a user with the role student"
        STUDENT = new User(NAME, USERNAME_1, 1, User.Role.STUDENT)
        STUDENT.setId(1)

        and: "a tournamentDto"
        tournamentDto = new TournamentDto()
        tournamentDto.setId(1)
        tournamentDto.setKey(1)
        tournamentDto.setStatus(Tournament.TournamentStatus.CREATED)
        tournamentDto.setOwner(new UserDto(STUDENT))

        and:
        VALID_COURSE = new Course(COURSE_NAME, Course.Type.TECNICO)
        VALID_COURSEEXECUT = new CourseExecution(VALID_COURSE, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)

        and: " a Topic"
        TOPIC = new Topic();
        TOPIC.setName("TOPIC")
        VALID_COURSE.addTopic(TOPIC)

        and: "a Topic conjuctions"
        TOPIC_CONJUCTIONS = new TopicConjunction()
        TOPIC_CONJUCTIONS.addTopic(TOPIC)
        TOPIC.addTopicConjunction(TOPIC_CONJUCTIONS)


        and: "a valid assessment"
        VALID_ASSESSMENT = new Assessment()
        VALID_ASSESSMENT.setStatus(Assessment.Status.AVAILABLE)
        VALID_ASSESSMENT.setCourseExecution(VALID_COURSEEXECUT)
        VALID_ASSESSMENT.addTopicConjunction(TOPIC_CONJUCTIONS)
        TOPIC_CONJUCTIONS.setAssessment(VALID_ASSESSMENT)
        VALID_COURSEEXECUT.addAssessment(VALID_ASSESSMENT)

        and: "invalid assessment 1"
        INVALID_ASSESSMENT_1 = new Assessment()
        INVALID_ASSESSMENT_1.setStatus(Assessment.Status.DISABLED)
        INVALID_ASSESSMENT_1.setCourseExecution(VALID_COURSEEXECUT)
        INVALID_ASSESSMENT_1.addTopicConjunction(TOPIC_CONJUCTIONS)
        TOPIC_CONJUCTIONS.setAssessment(INVALID_ASSESSMENT_1)
        VALID_COURSEEXECUT.addAssessment(INVALID_ASSESSMENT_1)

        and: "invalid assessment 2"
        INVALID_ASSESSMENT_2 = new Assessment()
        INVALID_ASSESSMENT_2.setStatus(Assessment.Status.REMOVED)
        INVALID_ASSESSMENT_2.setCourseExecution(VALID_COURSEEXECUT)
        INVALID_ASSESSMENT_2.addTopicConjunction(TOPIC_CONJUCTIONS)
        TOPIC_CONJUCTIONS.setAssessment(INVALID_ASSESSMENT_1)
        VALID_COURSEEXECUT.addAssessment(INVALID_ASSESSMENT_2)

        and: "invalid assessment 3"
        INVALID_ASSESSMENT_3 = new Assessment()
        INVALID_ASSESSMENT_3.setStatus(Assessment.Status.AVAILABLE)
        INVALID_ASSESSMENT_3.setCourseExecution(VALID_COURSEEXECUT)
        INVALID_ASSESSMENT_3.addTopicConjunction(TOPIC_CONJUCTIONS)
        TOPIC_CONJUCTIONS.setAssessment(INVALID_ASSESSMENT_3)

        and: "invalid assessment 4"
        INVALID_ASSESSMENT_4 = new Assessment()
        INVALID_ASSESSMENT_4.setStatus(Assessment.Status.AVAILABLE)
        INVALID_ASSESSMENT_4.setCourseExecution(VALID_COURSEEXECUT)
        VALID_COURSEEXECUT.addAssessment(INVALID_ASSESSMENT_4)
    }

    def setup(){
        given: "a course and a course execution"
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        def courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)

        and: " a Topic"
        def topic = new Topic();
        topic.setName("TOPIC")
        topic.setCourse(course)

        and: "a Topic conjuctions"
        def topicConjunction = new TopicConjunction()
        topicConjunction.addTopic(topic)
        topic.addTopicConjunction(topicConjunction)
        course.addTopic(topic)

        and:"a valid assessment"
        def assessment = new Assessment()
        assessment.setStatus(Assessment.Status.AVAILABLE)
        assessment.setCourseExecution(courseExecution)
        assessment.addTopicConjunction(topicConjunction)
        topicConjunction.setAssessment(assessment)
        courseExecution.addAssessment(assessment)

        and:"a invalid assessment_1"
        def assessment_1 = new Assessment()
        assessment_1.setStatus(Assessment.Status.DISABLED)
        assessment_1.setCourseExecution(courseExecution)
        assessment_1.addTopicConjunction(topicConjunction)
        topicConjunction.setAssessment(assessment_1)
        courseExecution.addAssessment(assessment_1)

        and:"a invalid assessment_2"
        def assessment_2 = new Assessment()
        assessment_2.setStatus(Assessment.Status.REMOVED)
        assessment_2.setCourseExecution(courseExecution)
        assessment_2.addTopicConjunction(topicConjunction)
        topicConjunction.setAssessment(assessment_2)
        courseExecution.addAssessment(assessment_2)

        and:"a invalid assessment_3"
        def assessment_3 = new Assessment()
        assessment_3.setStatus(Assessment.Status.AVAILABLE)
        assessment_3.setCourseExecution(courseExecution)
        assessment_3.addTopicConjunction(topicConjunction)
        topicConjunction.setAssessment(assessment_3)

        and:"a invalid assessment_4"
        def assessment_4 = new Assessment()
        assessment_4.setStatus(Assessment.Status.AVAILABLE)
        assessment_4.setCourseExecution(courseExecution)
        courseExecution.addAssessment(assessment_4)

        then:"add to repository"
        courseRepository.save(course)
        topicRepository.save(topic)
        courseExecutionRepository.save(courseExecution)
        topicConjunctionRepository.save(topicConjunction)
        assessmentRepository.save(assessment)
        assessmentRepository.save(assessment_1)
        assessmentRepository.save(assessment_2)
        assessmentRepository.save(assessment_3)
        assessmentRepository.save(assessment_4)

    }

    def "valid of options"(){
       given:"a correct date"
       availableDate = DATENOW
       conclusionDate = DATETOMORROW
       tournamentDto.setAvailableDate(availableDate.format(formatter))
       tournamentDto.setConclusionDate(conclusionDate.format(formatter))
       tournamentDto.setNumberOfQuestions(NUMQUESTIONS);
       tournamentDto.setAssessmentDto(new AssessmentDto(assessment))

       when:
       tournamentService.createTournament(courseExecution.getId(), tournamentDto)

       then:
       tournamentRepository.count() == 1L
       def result = tournamentRepository.findAll().get(0)

       result.id != null
       result.key == 1
       result.owner.getName() == NAME
       result.owner.getRole() == User.Role.STUDENT
       result.title == TITLE
       result.status == Tournament.TournamentStatus.CREATED
       result.conclusionDate != null
       result.getAvailableDate().format(formatter) == availableDate.format(formatter)
       result.getConclusionDate().format(formatter) == conclusionDate.format(formatter)
       result.getNumberOfQuestions() ==  NUMQUESTIONS
       result.getAssessment() ==  assessment
}

    def "invalid arguments: available=#avaleDate | conclusionDate=#concDate | numberOfQuestion=#numbOfQuest || errorMessage=#errorMessage "(){
        given:
        availableDate = DATENOW
        conclusionDate = DATETOMORROW
        tournamentDto.setAvailableDate(avaleDate.format(formatter))
        tournamentDto.setConclusionDate(concDate.format(formatter))
        tournamentDto.setNumberOfQuestions(numbOfQuest)
        tournamentDto.setAssessmentDto(new AssessmentDto(assess as Assessment))

        when:
        tournamentService.createTournament(courseExecution.getId(), tournamentDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        avaleDate     |   concDate   | numbOfQuest  |       assess         || errorMessage
         null         |     null     | NUMQUESTIONS |   VALID_ASSESSMENT   || TOURNAMENT_NOT_CONSISTENT
        DATENOW       |     null     | NUMQUESTIONS |   VALID_ASSESSMENT   || TOURNAMENT_NOT_CONSISTENT
         null         | DATETOMORROW | NUMQUESTIONS |   VALID_ASSESSMENT   || TOURNAMENT_NOT_CONSISTENT
        DATENOW       |  DATEBEFORE  | NUMQUESTIONS |   VALID_ASSESSMENT   || TOURNAMENT_NOT_CONSISTENT
        DATENOW       | DATETOMORROW |      -1      |   VALID_ASSESSMENT   || NOT_ENOUGH_QUESTIONS_TOURNAMENT
        DATENOW       | DATETOMORROW |       0      |   VALID_ASSESSMENT   || NOT_ENOUGH_QUESTIONS_TOURNAMENT
        DATENOW       | DATETOMORROW |     null     |   VALID_ASSESSMENT   || NOT_ENOUGH_QUESTIONS_TOURNAMENT
        DATENOW       | DATETOMORROW | NUMQUESTIONS |         null         || TOURNAMENT_PERMISSION
        DATENOW       | DATETOMORROW | NUMQUESTIONS | INVALID_ASSESSMENT_1 || TOURNAMENT_NOT_CONSISTENT
        DATENOW       | DATETOMORROW | NUMQUESTIONS | INVALID_ASSESSMENT_2 || TOURNAMENT_NOT_CONSISTENT
        DATENOW       | DATETOMORROW | NUMQUESTIONS | INVALID_ASSESSMENT_3 || TOURNAMENT_NOT_CONSISTENT
        DATENOW       | DATETOMORROW | NUMQUESTIONS | INVALID_ASSESSMENT_4 || TOURNAMENT_NOT_CONSISTENT

    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }

}
