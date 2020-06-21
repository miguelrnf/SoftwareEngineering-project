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
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_UNABLE_EDIT
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USERNAME_NOT_FOUND

@DataJpaTest
class EditTournamentServiceSpockTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    static final USERNAME_1 = 'username1'
    static final USERNAME_2 = 'username2'
    static final USERNAME_3 = 'username3'
    static final TITLE = 'first tournament'
    static final EDITED_TITLE = 'first edited tournament'
    static final NUMQUESTIONS = 2
    static final DATENOW = DateHandler.toISOString(DateHandler.now().plusDays(1))
    static final DATETOMORROW = DateHandler.toISOString(DateHandler.now().plusDays(2))
    static final NAME = 'name'
    static id = 1
    static tid = 1

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

    @Autowired
    TournamentRepository tournamentRepository

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
        tournamentDto.setType("STANDARD")
        tournamentDto.setId(tid)

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
        assdto.setId(id)
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


        then:"add to repository"
        questionRepository.save(questionOne)
        questionRepository.save(questionTwo)
        courseRepository.save(course)
        courseExecutionRepository.save(courseExecution)
        userRepository.save(userS)
        userRepository.save(userT)
        userRepository.save(userA)
        topicRepository.save(topic)
        topicConjunctionRepository.save(topicConjunction)
        assessmentRepository.save(ass)
    }

    def "student edits a tournament"() {
       given:
       tournamentDto.setOwner(new UserDto(STUDENT))
       tournamentDto.setTitle(TITLE)
       assdto.setId(id++)
       tournamentDto.setId(tid++)
       tournamentDto.setAssessmentDto(assdto)
       def tournament = tournamentService.createTournament(courseExecution.getId(), tournamentDto)

       when:
       tournamentDto.setTitle(EDITED_TITLE)
       def editedTournament = tournamentService.editTournament(STUDENT.getUsername(), tournamentDto)

       then:"the return data are correct"
       tournamentRepository.count() == 1L
       editedTournament.id != null
       editedTournament.owner.getName() == 'name'
       editedTournament.owner.getRole() == User.Role.STUDENT
       editedTournament.title == EDITED_TITLE
       editedTournament.owner == tournament.owner
       editedTournament.status == "CREATED"
    }

    def "teacher edits a tournament"() {
        given:
        tournamentDto.setOwner(new UserDto(TEACHER))
        tournamentDto.setTitle(TITLE)
        assdto.setId(id++)
        tournamentDto.setId(tid++)
        tournamentDto.setAssessmentDto(assdto)
        def tournament = tournamentService.createTournament(courseExecution.getId(), tournamentDto)

        when:
        tournamentDto.setTitle(EDITED_TITLE)
        def editedTournament = tournamentService.editTournament(TEACHER.getUsername(), tournamentDto)

        then:"the return data are correct"
        tournamentRepository.count() == 1L
        editedTournament.id != null
        editedTournament.owner.getName() == 'name'
        editedTournament.owner.getRole() == User.Role.TEACHER
        editedTournament.title == EDITED_TITLE
        editedTournament.owner == tournament.owner
        editedTournament.status == "CREATED"
    }

    @Unroll
    def "invalid arguments: user=#user | title=#title || errorMessage=#errorMessage "() {
        given:
        tournamentDto.setOwner(new UserDto(STUDENT))
        tournamentDto.setTitle(EDITED_TITLE)
        assdto.setId(id++)
        tournamentDto.setAssessmentDto(assdto)
        tournamentDto.setType(type)
        tournamentDto.setId(tid++)
        tournamentService.createTournament(courseExecution.getId(), tournamentDto)

        when:
        tournamentService.editTournament(user.username, tournamentDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
             user     |      type     || errorMessage
             ADMIN    |   "STANDARD"  || TOURNAMENT_UNABLE_EDIT
         NLL_USERNAME |   "STANDARD"  || USERNAME_NOT_FOUND
            TEACHER   |   "STANDARD"  || TOURNAMENT_UNABLE_EDIT
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
