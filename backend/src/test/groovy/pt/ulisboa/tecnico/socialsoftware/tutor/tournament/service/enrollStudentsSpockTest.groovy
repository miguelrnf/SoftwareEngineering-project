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

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_NOT_AVAILABLE
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_PERMISSION_ENROLL
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USERNAME_NOT_FOUND
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_NOT_FOUND

@DataJpaTest
class enrollStudentsSpockTest extends Specification{
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String ACRONYM_2 = "AS2"
    public static final String ACADEMIC_TERM_2 = "2 SEM"
    static final USERNAME_1 = "username1"
    static final USERNAME_2 = "username2"
    static final USERNAME_3 = "username3"
    static final USERNAME_4 = "username4"
    static final USERNAME_5 = "username5"
    static final USERNAME_6 = "username420"
    static final TITLE = "Title"

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


    def setupSpec(){

        given: "a user with the role teacher"
        TEACHER = new User()
        TEACHER.setId(4)
        TEACHER.setRole(User.Role.TEACHER)
        TEACHER.setUsername(USERNAME_4)

        and: "a user with the role admin"
        ADMIN = new User()
        ADMIN.setId(5)
        ADMIN.setRole(User.Role.ADMIN)
        ADMIN.setUsername(USERNAME_5)

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

        and: "a user with the role student"
        STUDENT_OTHER_CE = new User()
        STUDENT_OTHER_CE.setId(3)
        STUDENT_OTHER_CE.setRole(User.Role.STUDENT)
        STUDENT_OTHER_CE.setUsername(USERNAME_3)

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
        courseExecution_2 = new CourseExecution(course, ACRONYM_2, ACADEMIC_TERM_2, Course.Type.TECNICO)


        and: "a user with the role student"
        userS = new User('name', USERNAME_1, 1, User.Role.STUDENT)

        user_same = new User('name', USERNAME_2, 2, User.Role.STUDENT)

        user_other = new User('name', USERNAME_3, 3, User.Role.STUDENT)

        userT = new User('name', USERNAME_4, 4, User.Role.TEACHER)

        userA = new User('name', USERNAME_5, 5, User.Role.ADMIN)


        courseExecution_1.addUser(userS)
        courseExecution_1.addUser(userT)
        courseExecution_1.addUser(userA)
        courseExecution_1.addUser(user_same)
        courseExecution_2.addUser(user_other)

        userRepository.save(userS)
        userRepository.save(user_same)
        userRepository.save(user_other)
        userRepository.save(userT)
        userRepository.save(userA)
        courseRepository.save(course)
        courseExecutionRepository.save(courseExecution_1)
        courseExecutionRepository.save(courseExecution_2)

    }

    def "Enroll a student that is in the same courseExecution as the owner"() {
        given:
        def result = tournamentService.createTournament(courseExecution_1.getId() , TOURNAMENTDTO)

        when:
        tournamentService.enrollStudent(courseExecution_1.getId(), STUDENT_SAME_CE.getUsername(), result.getId())

        then:
        def tournamentTest = tournamentRepository.findById(result.getId())
        def result2 = tournamentTest.get().getEnrolledStudents().getAt(0)

        result2.username == USERNAME_2
        result2.id == 2
        result2.role == User.Role.STUDENT

    }

    @Unroll
    def "invalid arguments: user=#user || errorMessage=#errorMessage "() {
        given:
        def result = tournamentService.createTournament(courseExecution_1.getId() , TOURNAMENTDTO)

        when:
        tournamentService.enrollStudent(courseExecution_1.getId(), user.getUsername(), result.getId())

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
       TOURNAMENTDTO.setStatus(tournamentStatus)
       def result = tournamentService.createTournament(courseExecution_1.getId() , TOURNAMENTDTO)

       when:
       tournamentService.enrollStudent(courseExecution_1.getId(), STUDENT_OTHER_CE.getUsername(), result.getId())

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
        def result = tournamentService.createTournament(courseExecution_1.getId() , TOURNAMENTDTO)

        when:
        tournamentService.enrollStudent(courseExecution_2.getId(), STUDENT_OTHER_CE.getUsername(), result.getId())

        then:
        def error = thrown(TutorException)
        error.errorMessage == TOURNAMENT_NOT_AVAILABLE
    }

    @Unroll
    def "invalid arguments: userName=#username || errorMessage=#errorMessage "() {
        given:
        def result = tournamentService.createTournament(courseExecution_1.getId() , TOURNAMENTDTO)

        when:
        tournamentService.enrollStudent(courseExecution_1.getId() as Integer, username, result.getId() as Integer)

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
    }

}