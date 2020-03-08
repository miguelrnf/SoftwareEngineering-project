package pt.ulisboa.tecnico.socialsoftware.tutor.tourament.service

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CreateTournamentServiceSpockTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    static final USERNAME = 'username'
    static final TITLE = 'first tournament'
    static final VERSION = 'A'

    def TournamentService
    def tournamentDto
    def course
    def courseExecution
    def formatter
    def creationDate
    def availableDate
    def conclusionDate
    def quiz

    def setup() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        TournamentService = new TournamentService()

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecution.setId(1)

    }

    def "student creates a tournament"() {
       given:"a student"
       def user = new User('name', USERNAME, 1, User.Role.STUDENT)
       user.setId(1)
       user.setKey(1)
       def userDto = new UserDto(user)
       and:"a quizz"
       quiz = new QuizDto()
       quiz.setKey(1)
       quiz.setTitle(TITLE)
       creationDate = LocalDateTime.now()
       availableDate = LocalDateTime.now()
       conclusionDate = LocalDateTime.now().plusDays(1)
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

       when:
       def result = TournamentService.createTournament(courseExecution.getId(), tournamentDto)

       then:"the return data are correct"
       result.id == 1
       result.key == 1
       result.owner.getName() == 'name'
       result.owner.getRole() == User.Role.STUDENT
       result.title == TITLE
       result.status == Tournament.TournamentStatus.CREATED
       result.quiz.getTitle() == TITLE
       result.quiz.getScramble() == true
       result.quiz.getVersion() == VERSION
    }

    def "create a tournament with no title"() {
        given:"a student"
        def user = new User('name', USERNAME, 1, User.Role.TEACHER)
        user.setId(1)
        user.setKey(1)
        def userDto = new UserDto(user)

        and:"a quizz"
        quiz = new QuizDto()
        quiz.setKey(1)
        quiz.setTitle(TITLE)
        creationDate = LocalDateTime.now()
        availableDate = LocalDateTime.now()
        conclusionDate = LocalDateTime.now().plusDays(1)
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
        tournamentDto.setStatus(Tournament.TournamentStatus.CREATED)
        tournamentDto.setQuiz(quiz)

        when:
        TournamentService.createTournament(courseExecution.getId(), tournamentDto)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_NOT_CONSISTENT
    }

    def "create a tournament with blank title"() {
        given:"a student"
        def user = new User('name', USERNAME, 1, User.Role.TEACHER)
        user.setId(1)
        user.setKey(1)
        def userDto = new UserDto(user)

        and:"a quizz"
        quiz = new QuizDto()
        quiz.setKey(1)
        quiz.setTitle(TITLE)
        creationDate = LocalDateTime.now()
        availableDate = LocalDateTime.now()
        conclusionDate = LocalDateTime.now().plusDays(1)
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
        tournamentDto.setTitle(" ")
        tournamentDto.setStatus(Tournament.TournamentStatus.CREATED)
        tournamentDto.setQuiz(quiz)

        when:
        TournamentService.createTournament(courseExecution.getId(), tournamentDto)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_NOT_CONSISTENT

    }

    def "teacher creates a tournament"() {
        given:"a student"
        def user = new User('name', USERNAME, 1, User.Role.TEACHER)
        user.setId(1)
        user.setKey(1)
        def userDto = new UserDto(user)

        and:"a quizz"
        quiz = new QuizDto()
        quiz.setKey(1)
        quiz.setTitle(TITLE)
        creationDate = LocalDateTime.now()
        availableDate = LocalDateTime.now()
        conclusionDate = LocalDateTime.now().plusDays(1)
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
        tournamentDto.setTitle(" ")
        tournamentDto.setStatus(Tournament.TournamentStatus.CREATED)
        tournamentDto.setQuiz(quiz)

        when:
        TournamentService.createTournament(courseExecution.getId(), tournamentDto)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_PERMISSION
    }

    def "admin creates a tournament"() {
        given:"a student"
        def user = new User('name', USERNAME, 1, User.Role.TEACHER)
        user.setId(1)
        user.setKey(1)
        def userDto = new UserDto(user)

        and:"a quizz"
        quiz = new QuizDto()
        quiz.setKey(1)
        quiz.setTitle(TITLE)
        creationDate = LocalDateTime.now()
        availableDate = LocalDateTime.now()
        conclusionDate = LocalDateTime.now().plusDays(1)
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
        tournamentDto.setTitle(" ")
        tournamentDto.setStatus(Tournament.TournamentStatus.CREATED)
        tournamentDto.setQuiz(quiz)

        when:
        TournamentService.createTournament(courseExecution.getId(), tournamentDto)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_PERMISSION
    }
}
