package pt.ulisboa.tecnico.socialsoftware.tutor.classroom.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.classroom.ClassroomService
import pt.ulisboa.tecnico.socialsoftware.tutor.classroom.dto.ClassroomDto
import pt.ulisboa.tecnico.socialsoftware.tutor.classroom.dto.DocumentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.classroom.repository.ClassroomRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.classroom.repository.DocumentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.TopicConjunction
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.*
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.study.StudyService
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.StatementService
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementCreationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification

import java.util.stream.Collectors

@DataJpaTest
class CreateClassroomTest extends Specification {
    static final USERNAME = 'username'
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String TOPIC_NAME = "TOPIC"
    public static final String TOPIC_NAME2 = "TOPIC2"
    static final DATETOMORROW = DateHandler.toISOString(DateHandler.now().plusDays(2))


    @Autowired
    StudyService studyService

    @Autowired
    ClassroomService classroomService

    @Autowired
    QuizRepository quizRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    AssessmentRepository assessmentRepository

    @Autowired
    TopicRepository topicRepository

    @Autowired
    TopicConjunctionRepository topicConjunctionRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    OptionRepository optionRepository

    @Autowired
    ClassroomRepository classroomRepository

    @Autowired
    DocumentRepository documentRepository


    def user
    def courseExecution
    def questionOne
    def questionTwo
    def questionThree
    def assessment

    def setup() {
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        course.addCourseExecution(courseExecution)
        courseExecution.setCourse(course)

        courseExecutionRepository.save(courseExecution)
        courseRepository.save(course)

        user = new User('name', USERNAME, 1, User.Role.STUDENT)
        user.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(user)

        def topic = new Topic()
        topic.setName("TOPIC")
        topic.setCourse(course)
        topicRepository.save(topic)

        def topic2 = new Topic()
        topic2.setName("TOPIC2")
        topic2.setCourse(course)
        topicRepository.save(topic2)

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

        questionThree = new Question()
        questionThree.setKey(3)
        questionThree.setContent("Question Content")
        questionThree.setTitle("Question Title")
        questionThree.setStatus(Question.Status.AVAILABLE)
        questionThree.setCourse(course)
        questionThree.addTopic(topic2)

        userRepository.save(user)
        questionRepository.save(questionOne)
        questionRepository.save(questionTwo)
        questionRepository.save(questionThree)

        def topicConjunction = new TopicConjunction()
        topicConjunction.addTopic(topic)
        topicConjunction.addTopic(topic2)
        topic.addTopicConjunction(topicConjunction)
        topic2.addTopicConjunction(topicConjunction)
        topicConjunctionRepository.save(topicConjunction)

        assessment = new Assessment()
        assessment.setTitle("Assessment title")
        assessment.setStatus(Assessment.Status.AVAILABLE)
        assessment.setCourseExecution(courseExecution)
        assessment.addTopicConjunction(topicConjunction)
        topicConjunction.setAssessment(assessment)
        assessmentRepository.save(assessment)

    }

    def 'generate a classroom'() {
        given:
        def classroomDto = new ClassroomDto()
        classroomDto.setType("LECTURE")
        classroomDto.setTitle("t")
        classroomDto.setAvailableDate(DATETOMORROW)

        when:

        classroomService.createClassroom(courseExecution.getId(), classroomDto)


        then:
        classroomRepository.count() == 1L

    }

    def 'generate a invalid classroom'() {
        given:
        def classroomDto = new ClassroomDto()
        classroomDto.setType(type as String)
        classroomDto.setTitle(title as String)
        classroomDto.setAvailableDate(d as String)

        when:

        classroomService.createClassroom(courseExecution.getId(), classroomDto)


        then:
        def result = thrown(TutorException)
        result.message == expected

        where:
        type                 |       title               |       d                 |      expected
        "LECTURE"            |          ""               |    DATETOMORROW         |   ErrorMessage.INVALID_TITLE_FOR_CLASSROOM.label
        ""                   |        "t"                |   DATETOMORROW          |   ErrorMessage.NO_TYPE.label

    }


    def 'generate a classroom and document'() {
        given:
        def classroomDto = new ClassroomDto()
        classroomDto.setType("LECTURE")
        classroomDto.setTitle("t")
        classroomDto.setAvailableDate(DATETOMORROW)

        def documentDto = new DocumentDto()
        documentDto.setTitle("t")
        documentDto.setType("VIDEO")
        documentDto.setUrl("url")

        when:

        def classroom = classroomService.createClassroom(courseExecution.getId(), classroomDto)
        def result = classroomService.addDocument(classroom.getId(), documentDto)


        then:
        classroomRepository.count() == 1L
        documentRepository.count() == 1
    }


    @TestConfiguration
    static class QuizServiceImplTestContextConfiguration {

        @Bean
        StudyService studyService() {
            return new StudyService()
        }
        @Bean
        StatementService statementService() {
            return new StatementService()
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

        @Bean
        ClassroomService classroomService() {
            return new ClassroomService()
        }
    }
}