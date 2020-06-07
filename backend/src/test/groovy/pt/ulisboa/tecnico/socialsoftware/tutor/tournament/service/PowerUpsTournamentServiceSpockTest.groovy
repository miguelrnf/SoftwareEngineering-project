package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicConjunctionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.*
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementOptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Shared
import spock.lang.Specification

@DataJpaTest
class PowerUpsTournamentServiceSpockTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    static final USERNAME_1 = 'username1'
    static final TITLE = 'first tournament'
    static final NUMQUESTIONS = 1
    static final DATENOW = DateHandler.toISOString(DateHandler.now().plusDays(1))
    static final DATETOMORROW = DateHandler.toISOString(DateHandler.now().plusDays(2))
    static final NAME = 'name'
    static final CONTENT = 'SOMETHING GOOD'
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

    @Autowired
    OptionRepository optionRepository

    @Autowired
    QuizRepository quizRepository

    @Shared
    def tournamentDto

    @Shared
    def course

    @Shared
    def courseExecution

    @Shared
    def STUDENT

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
    def statementQuestionOne

    @Shared
    def quiz

    @Shared
    def statementOption1

    @Shared
    def statementOption2

    @Shared
    def statementOption3

    @Shared
    def statementOption4

    @Shared
    def questionAnswer

    @Shared
    def quizQuestion


    @Shared
    def questionOne

    @Shared
    def option1

    @Shared
    def option2

    @Shared
    def option3

    @Shared
    def option4

    def setupSpec() {

        given: "a tournamentDto"
        tournamentDto = new TournamentDto()
        tournamentDto.setId(1)
        tournamentDto.setStatus(Tournament.TournamentStatus.CREATED.name())
        tournamentDto.setAvailableDate(DATENOW)
        tournamentDto.setConclusionDate(DATETOMORROW)
        tournamentDto.setNumberOfQuestions(NUMQUESTIONS)
        tournamentDto.setType("STANDARD")
        tournamentDto.setTitle(TITLE)

        and: "a user with the role student"
        STUDENT = new User()
        STUDENT.setRole(User.Role.STUDENT)
        STUDENT.setUsername(USERNAME_1)
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
        questionOne.setHint("Teste_1234")

        and:
        quizQuestion = new QuizQuestion()
        quizQuestion.setQuestion(questionOne)

        and:
        questionAnswer = new QuestionAnswer()
        questionAnswer.setQuizQuestion(quizQuestion)

        and:
        statementQuestionOne = new StatementQuestionDto(questionAnswer)

        and:
        quiz = new Quiz()
        quiz.addQuizQuestion(quizQuestion)
        quiz.setCourseExecution(courseExecution)


        and:
        option1 = new Option()
        option1.setContent(CONTENT)
        option1.setCorrect(false)
        option1.setSequence(0)
        option1.setQuestion(questionOne)
        option2 = new Option()
        option2.setContent(CONTENT)
        option2.setCorrect(true)
        option2.setSequence(0)
        option2.setQuestion(questionOne)
        option3 = new Option()
        option3.setContent(CONTENT)
        option3.setCorrect(false)
        option3.setSequence(0)
        option3.setQuestion(questionOne)
        option4 = new Option()
        option4.setContent(CONTENT)
        option4.setCorrect(false)
        option4.setSequence(0)
        option4.setQuestion(questionOne)
        def options = new ArrayList<OptionDto>()
        options.add(new OptionDto(option1))
        options.add(new OptionDto(option2))
        options.add(new OptionDto(option3))
        options.add(new OptionDto(option4))
        questionOne.setOptions(options)
        optionRepository.save(option1)
        optionRepository.save(option2)
        optionRepository.save(option3)
        optionRepository.save(option4)

        and:
        statementOption1 = new StatementOptionDto(option1)
        statementOption2 = new StatementOptionDto(option2)
        statementOption3 = new StatementOptionDto(option3)
        statementOption4 = new StatementOptionDto(option4)
        def statementOptionList = new ArrayList<StatementOptionDto>()
        statementOptionList.add(statementOption1)
        statementOptionList.add(statementOption2)
        statementOptionList.add(statementOption3)
        statementOptionList.add(statementOption4)
        statementQuestionOne.setOptions(statementOptionList)

        and: "a user with the role student"
        def userS = new User('name', USERNAME_1, 1, User.Role.STUDENT)

        courseExecution.addUser(userS)
        userS.addCourse(courseExecution)

        then:"add to repository"
        questionRepository.save(questionOne)
        courseRepository.save(course)
        courseExecutionRepository.save(courseExecution)
        userRepository.save(userS)
        topicRepository.save(topic)
        topicConjunctionRepository.save(topicConjunction)
        assessmentRepository.save(ass)
        quizRepository.save(quiz)
    }

    def "power Up Fifty Fifty"() {
        given:
        assdto.setId(tempId++)
        tournamentDto.setOwner(new UserDto(STUDENT))
        tournamentDto.setAssessmentDto(assdto)
        def tournament = tournamentService.createTournament(courseExecution.getId(), tournamentDto)
        quiz.setTournament(new Tournament(tournament,  STUDENT, ass))

        when:
        def result = tournamentService.removeTwoOptions(statementQuestionOne, quiz.getId())

        then:"the return data are correct"
        result.getOptions().size() == 2
    }

    def "power Up Right Option"() {
        given:
        assdto.setId(tempId++)
        tournamentDto.setOwner(new UserDto(STUDENT))
        tournamentDto.setAssessmentDto(assdto)
        def tournament = tournamentService.createTournament(courseExecution.getId(), tournamentDto)
        quiz.setTournament(new Tournament(tournament,  STUDENT, ass))

        when:
        def result = tournamentService.rigthAnswer(statementQuestionOne, quiz.getId())

        then:"the return data are correct"
        result.getOptions().size() == 1
        result.getOptions().get(0).getOptionId() == option2.getId()
    }

    def "power Up hint"() {
        given:
        assdto.setId(tempId++)
        tournamentDto.setOwner(new UserDto(STUDENT))
        tournamentDto.setAssessmentDto(assdto)
        def tournament = tournamentService.createTournament(courseExecution.getId(), tournamentDto)
        quiz.setTournament(new Tournament(tournament,  STUDENT, ass))

        when:
        def result = tournamentService.getHint(statementQuestionOne, quiz.getId())

        then:"the return data are correct"
        result.equals(questionOne.getHint())
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