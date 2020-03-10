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
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_LIST_EMPTY

@DataJpaTest
class listTournamentSpockTest extends Specification{
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    static final USERNAME_1 = 'username1'
    static final TITLE1 = 'first tournament'
    static final TITLE2 = 'second tournament'
    static final TITLE3 = 'third tournament'
    static final VERSION = 'A'

    @Autowired
    TournamentService tournamentService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Shared
    def tournamentDto1

    @Shared
    def tournamentDto2

    @Shared
    def tournamentDto3

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

    @Shared
    def course

    @Shared
    def courseExecution

    def setupSpec() {

        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        given: "a quiz"
        quiz = new QuizDto()
        quiz.setKey(1)
        quiz.setType(Quiz.QuizType.PROPOSED)
        creationDate = LocalDateTime.now()
        availableDate = LocalDateTime.now()
        conclusionDate = LocalDateTime.now().plusDays(1)
        quiz.setTitle(TITLE1)
        quiz.setScramble(true)
        quiz.setAvailableDate(availableDate.format(formatter))
        quiz.setConclusionDate(conclusionDate.format(formatter))
        quiz.setSeries(1)
        quiz.setVersion(VERSION)

        and: "a user with the role student"
        STUDENT = new User()
        STUDENT.setId(1)
        STUDENT.setRole(User.Role.STUDENT)
        STUDENT.setUsername(USERNAME_1)

        and: "some tournamentDtos"
        tournamentDto1 = new TournamentDto()
        tournamentDto1.setId(1)
        tournamentDto1.setKey(1)
        tournamentDto1.setStatus(Tournament.TournamentStatus.CREATED)
        tournamentDto1.setQuiz(quiz)
        tournamentDto1.setOwner(new UserDto(STUDENT))
        tournamentDto1.setTitle(TITLE1)

        tournamentDto2 = new TournamentDto()
        tournamentDto2.setId(2)
        tournamentDto2.setKey(2)
        tournamentDto2.setStatus(Tournament.TournamentStatus.CREATED)
        tournamentDto2.setQuiz(quiz)
        tournamentDto2.setOwner(new UserDto(STUDENT))
        tournamentDto2.setTitle(TITLE2)

        tournamentDto3 = new TournamentDto()
        tournamentDto3.setId(3)
        tournamentDto3.setKey(3)
        tournamentDto3.setStatus(Tournament.TournamentStatus.CREATED)
        tournamentDto3.setQuiz(quiz)
        tournamentDto3.setOwner(new UserDto(STUDENT))
        tournamentDto3.setTitle(TITLE3)

    }

    def setup() {
        given: "a course and a course execution"
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)

        then:"add to repository"
        courseRepository.save(course)
        courseExecutionRepository.save(courseExecution)
    }

    def "show available tournaments"(){
        //available tournaments exist and are listed
        given: "three tournaments"
        def tournament1 = new Tournament(tournamentDto1, STUDENT)
        def tournament2 = new Tournament(tournamentDto2, STUDENT)
        def tournament3 = new Tournament(tournamentDto3, STUDENT)
        tournamentRepository.save(tournament1)
        tournamentRepository.save(tournament2)
        tournamentRepository.save(tournament3)

        when:
        def result = tournamentService.listTournaments(courseExecution.getId())

        then:
        result.contains(new TournamentDto(tournament1))
        result.contains(new TournamentDto(tournament2))
        result.contains(new TournamentDto(tournament3))
    }

    def "no available tournaments"(){
        //no available tournaments exits throw exception
        when:
        def result = tournamentService.listTournaments(courseExecution.getId())

        then:
        def error = thrown(TutorException)
        error.errorMessage == TOURNAMENT_LIST_EMPTY

    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}
