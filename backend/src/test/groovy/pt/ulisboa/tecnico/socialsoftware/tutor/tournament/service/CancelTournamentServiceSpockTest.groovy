package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.TopicConjunction
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicConjunctionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.AssessmentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicConjunctionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_PERMISSION_CANCEL
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USERNAME_NOT_FOUND


@DataJpaTest
class CancelTournamentServiceSpockTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    static final USERNAME_1 = 'username1'
    static final USERNAME_2 = 'username2'
    static final USERNAME_3 = 'username3'
    static final USERNAME_4 = 'username4'
    static final TITLE = 'first tournament'
    static final NUMQUESTIONS = 2
    static final DATENOW = DateHandler.toISOString(DateHandler.now().plusDays(1))
    static final DATETOMORROW = DateHandler.toISOString(DateHandler.now().plusDays(2))
    static final NAME = 'name'
    static int tempId = 1

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

    @Autowired
    QuestionRepository questionRepository

    @Shared
    def tournamentDto

    @Shared
    def course

    @Shared
    def courseExecution

    @Shared
    def TEACHER

    @Shared
    def ADMIN

    @Shared
    def STUDENT

    @Shared
    def NLL_USERNAME

    @Shared
    def STUDENT2

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
    def questionOne

    @Shared
    def questionTwo


    def setupSpec() {

        given: "a tournamentDto"
        tournamentDto = new TournamentDto()
        tournamentDto.setId(1)
        tournamentDto.setStatus(Tournament.TournamentStatus.CREATED.name())
        tournamentDto.setAvailableDate(DATENOW)
        tournamentDto.setConclusionDate(DATETOMORROW)
        tournamentDto.setNumberOfQuestions(NUMQUESTIONS)

        and: "a user with the role teacher"
        TEACHER = new User()
        TEACHER.setRole(User.Role.TEACHER)
        TEACHER.setUsername(USERNAME_2)

        and: "a user with the role admin"
        ADMIN = new User()
        ADMIN.setRole(User.Role.ADMIN)
        ADMIN.setUsername(USERNAME_3)

        and: "a user with the role student"
        STUDENT = new User()
        STUDENT.setRole(User.Role.STUDENT)
        STUDENT.setUsername(USERNAME_1)

        and: "a user with the role student"
        STUDENT2 = new User()
        STUDENT2.setRole(User.Role.STUDENT)
        STUDENT2.setUsername(USERNAME_4)

        and: "a user with the null username"
        NLL_USERNAME = new User()
        NLL_USERNAME.setRole(User.Role.STUDENT)
        NLL_USERNAME.setUsername(null)
    }

    def setup() {
        given: "a course and a course execution"
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)

        and: "a topic dto"
        topicDto = new TopicDto()
        topicDto.setName(NAME)

        and: "a topic conjunction dto"
        topicConjunctionDto = new TopicConjunctionDto()
        topicConjunctionDto.addTopic(topicDto)

        and: " a valid assessments"
        assdto = new AssessmentDto()
        assdto.setTitle(TITLE)
        assdto.setStatus(Assessment.Status.AVAILABLE.name())
        assdto.setTopicConjunctionsFromUnit(topicConjunctionDto)
        topic = new Topic(course, topicDto)
        topicConjunction = new TopicConjunction()
        topicConjunction.addTopic(topic)

        and:
        def tcl = new ArrayList<TopicConjunction>()
        tcl.add(topicConjunction)
        ass = new Assessment(courseExecution, tcl, assdto)

        and:
        questionOne = new Question()
        questionOne.setKey(1)
        questionOne.setContent("Question Content")
        questionOne.setTitle("Question Title")
        questionOne.setStatus(Question.Status.AVAILABLE)
        questionOne.setCourse(course)
        questionOne.addTopic(topic)

        questionTwo = new Question()
        questionTwo.setKey(2)
        questionTwo.setContent("Question Content")
        questionTwo.setTitle("Question Title")
        questionTwo.setStatus(Question.Status.AVAILABLE)
        questionTwo.setCourse(course)
        questionTwo.addTopic(topic)


        and: "a user with the role student"
        def userS = new User('name', USERNAME_1, 1, User.Role.STUDENT)

        def userT = new User('name', USERNAME_2, 2, User.Role.TEACHER)

        def userA = new User('name', USERNAME_3, 3, User.Role.ADMIN)

        def userS2 = new User('name', USERNAME_4, 4, User.Role.STUDENT)


        courseExecution.addUser(userS)
        userS.addCourse(courseExecution)
        courseExecution.addUser(userT)
        userT.addCourse(courseExecution)
        courseExecution.addUser(userS2)
        userS2.addCourse(courseExecution)

        then:"add to repository"
        questionRepository.save(questionOne)
        questionRepository.save(questionTwo)
        courseRepository.save(course)
        courseExecutionRepository.save(courseExecution)
        userRepository.save(userS)
        userRepository.save(userT)
        userRepository.save(userA)
        userRepository.save(userS2)
        topicRepository.save(topic)
        topicConjunctionRepository.save(topicConjunction)
        assessmentRepository.save(ass)
    }

    def "owner Student cancel tournament"() {
        given:
        assdto.setId(tempId++)
        tournamentDto.setOwner(new UserDto(STUDENT))
        tournamentDto.setTitle(TITLE)
        tournamentDto.setAssessmentDto(assdto)
        def tournament = tournamentService.createTournament(courseExecution.getId(), tournamentDto)

        when:
        def canceledTournament = tournamentService.cancelTournament(tournament.getId(), USERNAME_1)

        then:"the return data are correct"
        canceledTournament.id == tournament.getId()
        tournament.owner.getName() == canceledTournament.owner.getName()
        tournament.owner.getRole() == User.Role.STUDENT
        tournament.title == TITLE
        canceledTournament.status == "CANCELED"
    }

    def "owner Teacher cancel tournament"() {
        given:
        assdto.setId(tempId++)
        tournamentDto.setOwner(new UserDto(TEACHER))
        tournamentDto.setTitle(TITLE)
        tournamentDto.setAssessmentDto(assdto)
        def tournament = tournamentService.createTournament(courseExecution.getId(), tournamentDto)

        when:
        def canceledTournament = tournamentService.cancelTournament(tournament.getId(), USERNAME_2)

        then:"the return data are correct"
        canceledTournament.id == tournament.getId()
        tournament.owner.getName() == canceledTournament.owner.getName()
        tournament.owner.getRole() == User.Role.TEACHER
        tournament.title == TITLE
        canceledTournament.status == "CANCELED"
    }

    @Unroll
    def "invalid arguments: user=#user"() {
        given:
        assdto.setId(tempId++)
        tournamentDto.setOwner(new UserDto(STUDENT as User))
        tournamentDto.setTitle(TITLE)
        tournamentDto.setAssessmentDto(assdto)
        def tournament = tournamentService.createTournament(courseExecution.getId(), tournamentDto)


        when:
        tournamentService.cancelTournament(tournament.getId(), user.getUsername())

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        user         || errorMessage
        TEACHER      || TOURNAMENT_PERMISSION_CANCEL
        ADMIN        || TOURNAMENT_PERMISSION_CANCEL
        NLL_USERNAME || USERNAME_NOT_FOUND
        STUDENT2     || TOURNAMENT_PERMISSION_CANCEL

    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }

        @Bean
        QuizService quizService() {
            return new QuizService()
        }

        @Bean
        AnswerService answerService() {
            return new AnswerService()
        }

        @Bean
        AnswersXmlImport answersXmlImport() {
            return new AnswersXmlImport()
        }

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }
}