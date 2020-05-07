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
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
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

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_LIST_EMPTY

@DataJpaTest
class GetCreatedTournamentSpockTest extends Specification{
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    static final USERNAME_1 = 'username1'
    static final TITLE1 = 'first tournament'
    static final TITLE2 = 'second tournament'
    static final String NAME = 'Name'
    static final DATENOW = DateHandler.toISOString(DateHandler.now().plusDays(1))
    static final DATETOMORROW = DateHandler.toISOString(DateHandler.now().plusDays(2))
    static int tempId = 1
    static int tournId = 1

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

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuestionRepository questionRepository

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
    def tournamentDto1

    @Shared
    def tournamentDto2

    @Shared
    def STUDENT

    @Shared
    def course

    @Shared
    def user

    @Shared
    def courseExecution

    @Shared
    def questionOne

    @Shared
    def questionTwo

    def setupSpec() {

        given: "a user with the role student"
        STUDENT = new User()
        STUDENT.setRole(User.Role.STUDENT)
        STUDENT.setUsername(USERNAME_1)

        and: "some tournamentDtos"
        tournamentDto1 = new TournamentDto()
        tournamentDto1.setId(1)
        tournamentDto1.setStatus(Tournament.TournamentStatus.CREATED.name())
        tournamentDto1.setOwner(new UserDto(STUDENT))
        tournamentDto1.setTitle(TITLE1)
        tournamentDto1.setNumberOfQuestions(2)
        tournamentDto1.setAvailableDate(DATENOW)
        tournamentDto1.setConclusionDate(DATETOMORROW)

        tournamentDto2 = new TournamentDto()
        tournamentDto2.setId(2)
        tournamentDto2.setOwner(new UserDto(STUDENT))
        tournamentDto2.setTitle(TITLE2)
        tournamentDto2.setNumberOfQuestions(2)
        tournamentDto2.setAvailableDate(DATENOW)
        tournamentDto2.setConclusionDate(DATETOMORROW)
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
        topicDto.setName(NAME)

        and: "a topic conjunction dto"
        topicConjunctionDto = new TopicConjunctionDto()
        topicConjunctionDto.addTopic(topicDto)

        and: " a valid assessments"
        assdto = new AssessmentDto()
        assdto.setId(1)
        assdto.setTitle(TITLE1)
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
        then: "add to repository"
        questionRepository.save(questionOne)
        questionRepository.save(questionTwo)
        courseRepository.save(course)
        courseExecutionRepository.save(courseExecution)
        userRepository.save(user)
        topicRepository.save(topic)
        topicConjunctionRepository.save(topicConjunction)
        assessmentRepository.save(ass)
    }

    def "show tournaments"(){
        //available tournaments exist and are listed
        given: "two tournaments"
        assdto.setId(tempId++)
        tournamentDto2.setStatus(Tournament.TournamentStatus.CREATED.name())
        tournamentDto1.setAssessmentDto(assdto)
        tournamentDto2.setAssessmentDto(assdto)
        tournamentService.createTournament(courseExecution.id, tournamentDto1)
        tournamentService.createTournament(courseExecution.id, tournamentDto2)
        def tournament1 = new Tournament(tournamentDto1, user, ass)
        def tournament2 = new Tournament(tournamentDto2, user, ass)
        tournament1.setId(tournId++)
        tournament2.setId(tournId++)

        when:
        def result = tournamentService.listTournaments(courseExecution.getId())

        then:
        result.contains(new TournamentDto(tournament1))
        result.contains(new TournamentDto(tournament2))
        result.size() == 2
    }

    def "don't show cancel tournament"() {
        given:
        assdto.setId(tempId++)
        tournamentDto2.setStatus("CANCELED")
        tournamentDto2.setAssessmentDto(assdto)
        tournamentService.createTournament(courseExecution.id, tournamentDto2)

        when:
        def result = tournamentService.listTournaments(courseExecution.getId())

        then:
        result.isEmpty()
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


