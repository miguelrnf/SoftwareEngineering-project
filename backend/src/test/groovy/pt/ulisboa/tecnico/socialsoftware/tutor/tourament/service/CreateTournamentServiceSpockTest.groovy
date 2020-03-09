package pt.ulisboa.tecnico.socialsoftware.tutor.tourament.service

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
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_NOT_CONSISTENT
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_PERMISSION

@DataJpaTest
class CreateTournamentServiceSpockTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    static final USERNAME = 'username'
    static final TITLE = 'first tournament'
    static final VERSION = 'A'
    static final NAME = 'name'

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    TournamentService tournamentService

    def tournamentDto
    def course
    def courseExecution
    def creationDate
    def availableDate
    def conclusionDate
    def quiz
    def formatter

    def setup() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

    }

    def "student creates a tournament"() {
       given:"a student"
       def user = new User('name', USERNAME, 1, User.Role.STUDENT)
       user.setId(1)
       userRepository.save(user)
       def userDto = new UserDto(user)
       and:"a quizz"
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

       and:"tournamment dto"
       tournamentDto = new TournamentDto()
       tournamentDto.setId(1)
       tournamentDto.setKey(1)
       tournamentDto.setOwner(userDto)
       tournamentDto.setTitle(TITLE)
       tournamentDto.setStatus(Tournament.TournamentStatus.CREATED)
       tournamentDto.setQuiz(quiz)
       println(tournamentDto.dump())

       when:
       def result = tournamentService.createTournament(courseExecution.getId(), tournamentDto)

       then:"the return data are correct"
       result.id != null
       result.key == 1
       result.owner.getName() == 'name'
       result.owner.getRole() == User.Role.STUDENT
       result.title == TITLE
       result.status == Tournament.TournamentStatus.CREATED
       result.quiz.getTitle() == TITLE
       result.quiz.getScramble()
       result.quiz.getVersion() == VERSION
    }

    def "null user creates a tournament"() {
        given: "a null user"
        def user = null
        creationDate = LocalDateTime.now()
        availableDate = LocalDateTime.now()
        conclusionDate = LocalDateTime.now().plusDays(1)

        and: "a quizz"
        quiz = new QuizDto()
        quiz.setKey(1)
        quiz.setTitle(TITLE)
        quiz.setScramble(true)
        quiz.setAvailableDate(availableDate.format(formatter))
        quiz.setConclusionDate(conclusionDate.format(formatter))
        quiz.setSeries(1)
        quiz.setVersion(VERSION)

        and: "tournamment dto"
        tournamentDto = new TournamentDto()
        tournamentDto.setKey(1)
        tournamentDto.setOwner(user)
        tournamentDto.setTitle(TITLE)
        tournamentDto.setStatus(Tournament.TournamentStatus.CREATED)
        tournamentDto.setQuiz(quiz)

        when:
        tournamentService.createTournament(courseExecution.getId(), tournamentDto)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_NOT_CONSISTENT
    }


    @Unroll
    def "invalid arguments: role=#role | name=#name | username=#courseName | title=#title || errorMessage=#errorMessage "() {
        given:"a user"
        def user = new User(name , username, 1, role)
        user.setId(1)
        userRepository.save(user)
        creationDate = LocalDateTime.now()
        availableDate = LocalDateTime.now()
        conclusionDate = LocalDateTime.now().plusDays(1)
        def userDto = new UserDto(user)

        and:"a quizz"
        quiz = new QuizDto()
        quiz.setKey(1)
        quiz.setTitle(TITLE)
        quiz.setScramble(true)
        quiz.setAvailableDate(availableDate.format(formatter))
        quiz.setConclusionDate(conclusionDate.format(formatter))
        quiz.setSeries(1)
        quiz.setVersion(VERSION)

        and:"tournamment dto"
        tournamentDto = new TournamentDto()
        tournamentDto.setKey(1)
        tournamentDto.setOwner(userDto)
        tournamentDto.setTitle(title)
        tournamentDto.setStatus(Tournament.TournamentStatus.CREATED)
        tournamentDto.setQuiz(quiz)

        when:
        tournamentService.createTournament(courseExecution.getId(), tournamentDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        role              | name | username | title || errorMessage
        User.Role.TEACHER | NAME | USERNAME | TITLE || TOURNAMENT_PERMISSION
        User.Role.ADMIN   | NAME | USERNAME | TITLE || TOURNAMENT_PERMISSION
        User.Role.STUDENT | NAME |   null   | TITLE || TOURNAMENT_NOT_CONSISTENT
        User.Role.STUDENT | NAME | USERNAME | null  || TOURNAMENT_NOT_CONSISTENT
        User.Role.STUDENT | NAME | USERNAME | '  '  || TOURNAMENT_NOT_CONSISTENT
    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}
