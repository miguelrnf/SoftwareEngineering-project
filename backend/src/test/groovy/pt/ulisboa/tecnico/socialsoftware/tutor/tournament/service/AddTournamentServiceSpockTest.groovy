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
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_EXECUTION_NOT_FOUND


@DataJpaTest
class AddTournamentServiceSpockTest extends Specification{
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    static final USERNAME_1 = 'username1'
    static final TITLE = 'first tournament'
    static final VERSION = 'A'

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    TournamentService tournamentService

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
    def quiz

    @Shared
    def formatter

    @Shared
    def STUDENT

    def setupSpec() {

        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        given: "a quiz"
        quiz = new QuizDto()
        quiz.setKey(1)
        quiz.setType(Quiz.QuizType.PROPOSED)
        creationDate = LocalDateTime.now()
        availableDate = LocalDateTime.now()
        conclusionDate = LocalDateTime.now().plusDays(1)
        quiz.setTitle(TITLE)
        quiz.setScramble(true)
        quiz.setAvailableDate(availableDate.format(formatter))
        quiz.setConclusionDate(conclusionDate.format(formatter))
        quiz.setSeries(1)
        quiz.setVersion(VERSION)

        and: "a tournamentDto"
        tournamentDto = new TournamentDto()
        tournamentDto.setId(1)
        tournamentDto.setKey(1)
        tournamentDto.setStatus(Tournament.TournamentStatus.CREATED)
        tournamentDto.setQuiz(quiz)

        and: "a user with the role student"
        STUDENT = new User()
        STUDENT.setId(1)
        STUDENT.setRole(User.Role.STUDENT)
        STUDENT.setUsername(USERNAME_1)

    }

    def setup() {
        given: "a course and a course execution"
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)

        and: "a user with the role student"
        def userS = new User('name', USERNAME_1, 1, User.Role.STUDENT)

        then:"add to repository"
        courseRepository.save(course)
        courseExecutionRepository.save(courseExecution)
        userRepository.save(userS)

    }

    def "valid course Execution"() {
        given:
        tournamentDto.setOwner(new UserDto(STUDENT))
        tournamentDto.setTitle(TITLE)
        tournamentDto.setId(1)

        when:
        def result = tournamentService.createTournament(courseExecution.getId(), tournamentDto)

        then:
        result.id != null
        result.key == 1
        result.owner.getName() == 'name'
        result.owner.getRole() == User.Role.STUDENT
        result.title == TITLE
        result.status == Tournament.TournamentStatus.CREATED
        result.quiz.getTitle() == TITLE
        result.quiz.getScramble()
        result.quiz.getVersion() == VERSION

        def tourntest = new TournamentDto(courseExecution.getTournaments().getAt(0))

        result.getId() == tourntest.getId()
        result.getKey() == tourntest.getKey()
        result.getOwner().getName() == tourntest.getOwner().getName()
        result.getOwner().getRole() == tourntest.getOwner().getRole()
        result.getTitle() == tourntest.getTitle()
    }



    @Unroll
    def "invalid arguments: id=#id || errorMessage=#errorMessage "() {
        given:
        tournamentDto.setOwner(new UserDto(STUDENT))
        tournamentDto.setTitle(TITLE)

        when:
        tournamentService.createTournament(id, tournamentDto as TournamentDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        id     || errorMessage
        -1     || COURSE_EXECUTION_NOT_FOUND
         4     || COURSE_EXECUTION_NOT_FOUND
         5     || COURSE_EXECUTION_NOT_FOUND
    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}
