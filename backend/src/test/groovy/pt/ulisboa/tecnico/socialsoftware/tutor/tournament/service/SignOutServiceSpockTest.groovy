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

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_NOT_FOUND
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.UNABLE_TO_UNROLL

@DataJpaTest
class SignOutServiceSpockTest extends Specification{

    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    static final String USERNAME_1 = "username1"
    static final String USERNAME_2 = "username2"
    static final String TITLE = "Title"
    static final Integer INV_TOURNAMENT_ID = -1

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    TournamentService tournamentService

    @Shared
    def course

    @Shared
    def courseExecution_1

    @Shared
    def STUDENT_OWNER

    @Shared
    def STUDENT_SAME_CE

    @Shared
    def TOURNAMENTDTO

    @Shared
    def userS

    @Shared
    def user_same

    def setupSpec(){

        and: "a user with the role student"
        STUDENT_OWNER = new User()
        STUDENT_OWNER.setId(1)
        STUDENT_OWNER.setRole(User.Role.STUDENT)
        STUDENT_OWNER.setUsername(USERNAME_1)

        and: "a user with the role student"
        STUDENT_SAME_CE = new User()
        STUDENT_SAME_CE.setId(2)
        STUDENT_SAME_CE.setRole(User.Role.STUDENT)
        STUDENT_SAME_CE.setUsername(USERNAME_2)

        and: "a tournamentDto"
        TOURNAMENTDTO = new TournamentDto()
        TOURNAMENTDTO.setId(1)
        TOURNAMENTDTO.setKey(1)
        TOURNAMENTDTO.setStatus(Tournament.TournamentStatus.CREATED)
        TOURNAMENTDTO.setOwner(new UserDto(STUDENT_OWNER))
        TOURNAMENTDTO.setTitle(TITLE)

    }

    def setup(){

        given: "a course and a course execution"
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseExecution_1 = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)

        and: "a user with the role student"
        userS = new User('name', USERNAME_1, 1, User.Role.STUDENT)

        user_same = new User('name', USERNAME_2, 2, User.Role.STUDENT)


        courseExecution_1.addUser(userS)
        courseExecution_1.addUser(user_same)
        userRepository.save(userS)
        userRepository.save(user_same)
        courseRepository.save(course)
        courseExecutionRepository.save(courseExecution_1)

    }

    def "students unroll from a valid tournament"(){
        given:
        def result = tournamentService.createTournament(courseExecution_1.getId() , TOURNAMENTDTO)

        when:
        tournamentService.enrollStudent(courseExecution_1.getId() as Integer, STUDENT_SAME_CE.getUsername() as String, result.getId())

        then:
        def tournamentTest = tournamentRepository.findById(result.getId())
        def result2 = tournamentTest.get().getEnrolledStudents().getAt(0)
        def result5 = user_same.getTournaments()

        result2.username == USERNAME_2
        result2.id == 2
        result2.role == User.Role.STUDENT
        result5.contains(tournamentTest.get())

        when:
        tournamentService.unrollStudent (user_same.getUsername() as String, result.getId() as Integer)

        then:
        def tournamentTest2 = tournamentRepository.findById(result.getId())
        def result3 = tournamentTest2.get().getEnrolledStudents()
        def result4 = user_same.getTournaments()

        !result3.contains(user_same)
        !result4.contains(tournamentTest2)
    }

    def "students unroll from an invalid tournament"(){

        when:
        tournamentService.unrollStudent (USERNAME_2, INV_TOURNAMENT_ID)

        then:
        def error = thrown(TutorException)
        error.errorMessage == TOURNAMENT_NOT_FOUND
    }

    def "unroll students not enrolled in a tournament"(){
        given:
        def result = tournamentService.createTournament(courseExecution_1.getId() , TOURNAMENTDTO)

        when:
        tournamentService.unrollStudent (USERNAME_2, result.getId() as Integer)

        then:
        def error = thrown(TutorException)
        error.errorMessage == UNABLE_TO_UNROLL
    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}
