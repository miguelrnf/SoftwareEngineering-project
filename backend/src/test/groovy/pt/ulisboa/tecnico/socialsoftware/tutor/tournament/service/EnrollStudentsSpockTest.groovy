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

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class EnrollStudentsSpockTest extends Specification{
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String ACRONYM_2 = "AS2"
    public static final String ACADEMIC_TERM_2 = "2 SEM"
    static final String USERNAME_1 = "username1"
    static final String USERNAME_2 = "username2"
    static final String USERNAME_3 = "username3"
    static final String USERNAME_4 = "username4"
    static final String USERNAME_5 = "username5"
    static final String USERNAME_6 = "username420"
    static final Integer NUMQUESTIONS = 2
    static final String TITLE = "Title"
    static final String NAME = "NOME"
    static final DATENOW = DateHandler.toISOString(DateHandler.now().plusDays(1))
    static final DATETOMORROW = DateHandler.toISOString(DateHandler.now().plusDays(2))
    static int tempId = 1

    @Autowired
    UserRepository userRepository

    @Autowired
    AssessmentRepository assessmentRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    TournamentService tournamentService

    @Autowired
    TopicRepository topicRepository

    @Autowired
    TopicConjunctionRepository topicConjunctionRepository

    @Autowired
    QuestionRepository questionRepository

    @Shared
    def course

    @Shared
    def courseExecution_1

    @Shared
    def courseExecution_2

    @Shared
    def TEACHER

    @Shared
    def ADMIN

    @Shared
    def STUDENT_OWNER

    @Shared
    def STUDENT_SAME_CE

    @Shared
    def STUDENT_OTHER_CE

    @Shared
    def TOURNAMENTDTO

    @Shared
    def userS

    @Shared
    def user_same

    @Shared
    def user_other

    @Shared
    def userT

    @Shared
    def userA

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

    def setupSpec(){

        given: "a user with the role teacher"

        TEACHER = new User()
        TEACHER.setRole(User.Role.TEACHER)
        TEACHER.setUsername(USERNAME_4)

        and: "a user with the role admin"
        ADMIN = new User()
        ADMIN.setRole(User.Role.ADMIN)
        ADMIN.setUsername(USERNAME_5)

        and: "a user with the role student"
        STUDENT_OWNER = new User()
        STUDENT_OWNER.setRole(User.Role.STUDENT)
        STUDENT_OWNER.setUsername(USERNAME_1)

        and: "a user with the role student"
        STUDENT_SAME_CE = new User()
        STUDENT_SAME_CE.setRole(User.Role.STUDENT)
        STUDENT_SAME_CE.setUsername(USERNAME_2)

        and: "a user with the role student"
        STUDENT_OTHER_CE = new User()
        STUDENT_OTHER_CE.setRole(User.Role.STUDENT)
        STUDENT_OTHER_CE.setUsername(USERNAME_3)

        and: "a tournamentDto"
        TOURNAMENTDTO = new TournamentDto()
        TOURNAMENTDTO.setId(1)
        TOURNAMENTDTO.setStatus(Tournament.TournamentStatus.CREATED.name())
        TOURNAMENTDTO.setOwner(new UserDto(STUDENT_OWNER))
        TOURNAMENTDTO.setTitle(TITLE)
        TOURNAMENTDTO.setAvailableDate(DATENOW)
        TOURNAMENTDTO.setConclusionDate(DATETOMORROW)
        TOURNAMENTDTO.setNumberOfQuestions(NUMQUESTIONS)
    }

    def setup(){

        given: "a course and a course execution"
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseExecution_1 = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecution_2 = new CourseExecution(course, ACRONYM_2, ACADEMIC_TERM_2, Course.Type.TECNICO)


        and: "a user with the role student"
        userS = new User('name', USERNAME_1, 1, User.Role.STUDENT)
        user_same = new User('name', USERNAME_2, 2, User.Role.STUDENT)
        user_other = new User('name', USERNAME_3, 3, User.Role.STUDENT)
        userT = new User('name', USERNAME_4, 4, User.Role.TEACHER)
        userA = new User('name', USERNAME_5, 5, User.Role.ADMIN)

        and: "a topic dto"
        topicDto = new TopicDto()
        topicDto.setName(NAME)

        and: "a topic conjunction dto"
        topicConjunctionDto = new TopicConjunctionDto()
        topicConjunctionDto.addTopic(topicDto)

        and: " a valid assessments"
        assdto = new AssessmentDto()
        assdto.setId(1)
        assdto.setTitle(TITLE)
        assdto.setStatus(Assessment.Status.AVAILABLE.name())
        assdto.setTopicConjunctionsFromUnit(topicConjunctionDto)
        topic = new Topic(course, topicDto)
        topicConjunction = new TopicConjunction()
        topicConjunction.addTopic(topic)

        and:
        def tcl = new ArrayList<TopicConjunction>()
        tcl.add(topicConjunction)
        ass = new Assessment(courseExecution_1, tcl, assdto)

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

        questionRepository.save(questionOne)
        questionRepository.save(questionTwo)
        courseExecution_1.addUser(userS)
        userS.addCourse(courseExecution_1)
        courseExecution_1.addUser(userT)
        userT.addCourse(courseExecution_1)
        courseExecution_1.addUser(userA)
        userA.addCourse(courseExecution_1)
        courseExecution_1.addUser(user_same)
        user_same.addCourse(courseExecution_1)
        courseExecution_2.addUser(user_other)
        user_other.addCourse(courseExecution_2)

        userRepository.save(userS)
        userRepository.save(user_same)
        userRepository.save(user_other)
        userRepository.save(userT)
        userRepository.save(userA)
        courseRepository.save(course)
        courseExecutionRepository.save(courseExecution_1)
        courseExecutionRepository.save(courseExecution_2)
        topicRepository.save(topic)
        topicConjunctionRepository.save(topicConjunction)
        assessmentRepository.save(ass)

    }

    def "Enroll a student that is in the same courseExecution as the owner"() {
        given:
        assdto.setId(tempId++)
        TOURNAMENTDTO.setAssessmentDto(assdto)
        def result = tournamentService.createTournament(courseExecution_1.getId() , TOURNAMENTDTO)

        when:
        tournamentService.enrollStudent(STUDENT_SAME_CE.getUsername(), result.getId())

        then:
        def tournamentTest = tournamentRepository.findById(result.getId())
        def result2 = tournamentTest.get().getEnrolledStudents()[0]

        result2.username == USERNAME_2
        result2.id == 2
        result2.role == User.Role.STUDENT

    }

    @Unroll
    def "invalid arguments: user=#user || errorMessage=#errorMessage "() {
        given:
        assdto.setId(tempId++)
        TOURNAMENTDTO.setAssessmentDto(assdto)
        def result = tournamentService.createTournament(courseExecution_1.getId() , TOURNAMENTDTO)

        when:
        tournamentService.enrollStudent(user.getUsername(), result.getId())

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        user      || errorMessage
        TEACHER   || TOURNAMENT_PERMISSION_ENROLL
        ADMIN     || TOURNAMENT_PERMISSION_ENROLL
    }

    @Unroll
    def "invalid arguments: tournamentSate=#tournameSate || errorMessage=#errorMessage "() {
        given:
        assdto.setId(tempId++)
        TOURNAMENTDTO.setAssessmentDto(assdto)
        TOURNAMENTDTO.setStatus(tournamentStatus.name())
        def result = tournamentService.createTournament(courseExecution_1.getId() , TOURNAMENTDTO)

        when:
        tournamentService.enrollStudent(STUDENT_OTHER_CE.getUsername(), result.getId())

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        tournamentStatus                       || errorMessage
        Tournament.TournamentStatus.CANCELED   || TOURNAMENT_NOT_AVAILABLE
        Tournament.TournamentStatus.CLOSED     || TOURNAMENT_NOT_AVAILABLE
        Tournament.TournamentStatus.OPEN       || TOURNAMENT_NOT_AVAILABLE
    }

    def "Enroll a student that isn't in the same courseExecution as the owner"() {
        given:
        assdto.setId(tempId++)
        TOURNAMENTDTO.setAssessmentDto(assdto)
        def result = tournamentService.createTournament(courseExecution_1.getId() , TOURNAMENTDTO)

        when:
        tournamentService.enrollStudent(STUDENT_OTHER_CE.getUsername(), result.getId())

        then:
        def error = thrown(TutorException)
        error.errorMessage == TOURNAMENT_NOT_AVAILABLE
    }

    @Unroll
    def "invalid arguments: userName=#username || errorMessage=#errorMessage "() {
        given:
        assdto.setId(tempId++)
        TOURNAMENTDTO.setAssessmentDto(assdto)
        def result = tournamentService.createTournament(courseExecution_1.getId() , TOURNAMENTDTO)

        when:
        tournamentService.enrollStudent(username, result.getId() as Integer)

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        username                    || errorMessage
        null                        || USERNAME_NOT_FOUND
        USERNAME_6                  || USERNAME_NOT_FOUND

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
