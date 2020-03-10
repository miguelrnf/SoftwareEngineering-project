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
class listTournamentSpockTest extends Specification{
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    static final USERNAME_1 = 'username1'
    static final TITLE1 = 'first tournament'
    static final TITLE2 = 'second tournament'

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

    def setupSpec() {

        given: "a user with the role student"
        STUDENT = new User()
        STUDENT.setId(1)
        STUDENT.setRole(User.Role.STUDENT)
        STUDENT.setUsername(USERNAME_1)

        and: "some tournamentDtos"
        tournamentDto1 = new TournamentDto()
        tournamentDto1.setId(1)
        tournamentDto1.setKey(1)
        tournamentDto1.setStatus(Tournament.TournamentStatus.CREATED)
        tournamentDto1.setOwner(new UserDto(STUDENT))
        tournamentDto1.setTitle(TITLE1)

        tournamentDto2 = new TournamentDto()
        tournamentDto2.setId(2)
        tournamentDto2.setKey(2)
        tournamentDto2.setOwner(new UserDto(STUDENT))
        tournamentDto2.setTitle(TITLE2)

    }

    def setup() {
        given: "a course and a course execution"
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        user = new User()
        user.setKey(2)
        user.setRole(User.Role.STUDENT)
        user.setUsername(USERNAME_1)


        then:"add to repository"
        courseRepository.save(course)
        courseExecutionRepository.save(courseExecution)
        userRepository.save(user)
    }

    def "show tournaments"(){
        //available tournaments exist and are listed
        given: "three tournaments"
        tournamentDto2.setStatus(Tournament.TournamentStatus.CREATED)
        tournamentService.createTournament(courseExecution.id, tournamentDto1)
        tournamentService.createTournament(courseExecution.id, tournamentDto2)
        def tournament1 = new Tournament(tournamentDto1, STUDENT)
        def tournament2 = new Tournament(tournamentDto2, STUDENT)
        tournament1.setId(1)
        tournament2.setId(2)

        when:
        def result = tournamentService.listTournaments(courseExecution.getId())

        then:


        result.size() == 2
        result.contains(new TournamentDto(tournament1))
        result.contains(new TournamentDto(tournament2))
    }

    @Unroll
    def "tournament with status=#status || errorMessage=#errorMessage "() {
        given:
        tournamentDto2.setStatus(status)
        def tournament2 = new Tournament(tournamentDto2, STUDENT)
        tournamentService.createTournament(courseExecution.id, tournamentDto2)

        when:
        def result = tournamentService.listTournaments(courseExecution.getId())

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
                    status                   ||       errorMessage
        Tournament.TournamentStatus.CLOSED   || TOURNAMENT_LIST_EMPTY
        Tournament.TournamentStatus.OPEN     || TOURNAMENT_LIST_EMPTY
        Tournament.TournamentStatus.CANCELED || TOURNAMENT_LIST_EMPTY
    }


    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}


