package pt.ulisboa.tecnico.socialsoftware.tutor.leaderboards.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.leaderboards.LeaderboardsService
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.Post
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.PostAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.PostQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostDto
import pt.ulisboa.tecnico.socialsoftware.tutor.post.repository.PostRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.domain.Suggestion
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@DataJpaTest
class GetLeaderboardsTest extends Specification {
    public static final String VALID_NAME_1 = "Ben Dover"
    public static final String VALID_NAME_2 = "Mike Litoris"
    public static final String VALID_NAME_3 = "Peixe Acha"
    public static final String VALID_USERNAME_1 = "BenDover69"
    public static final String VALID_USERNAME_2 = "MikeLitoris420"
    public static final String VALID_USERNAME_3 = "PeixeAcha666"

    @Autowired
    LeaderboardsService leaderboardsService

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    PostRepository postRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Shared
    def USER_1

    @Shared
    def USER_2

    @Shared
    def USER_3

    def setup() {
        given: "a course Repository"
        def courseExecution = new CourseExecution()
        def ceSet = new HashSet()
        ceSet.add(courseExecution)
        courseExecutionRepository.save(courseExecution)

        and: "three valid users"
        def user1 = new User(VALID_NAME_1, VALID_USERNAME_1, 1, User.Role.STUDENT)
        def user2 = new User(VALID_NAME_2, VALID_USERNAME_2, 2, User.Role.STUDENT)
        def user3 = new User(VALID_NAME_3, VALID_USERNAME_3, 3, User.Role.STUDENT)

        def t1 = new Tournament()
        t1.setTitle('T1')
        t1.setNumberOfQuestions(1)
        def t2 = new Tournament()
        t2.setTitle('T2')
        t2.setNumberOfQuestions(2)
        def t3 = new Tournament()
        t3.setTitle('T3')
        t3.setNumberOfQuestions(3)

        user1.setCourseExecutions(ceSet)
        user1.addTournament(t1)
        user1.addTournament(t2)
        user1.addTournament(t3)
        user1.addPostQuestion(new PostQuestion())
        user1.addPostQuestion(new PostQuestion())
        user1.addPostQuestion(new PostQuestion())
        user1.addSuggestion(new Suggestion())
        user1.setNumberOfSuggestionsApproved(1)
        user1.addQuiz(new Quiz())
        user1.addQuiz(new Quiz())
        user1.addQuiz(new Quiz())

        user2.setCourseExecutions(ceSet)
        user2.addTournament(t1)
        user2.addTournament(t2)
        user2.addPostQuestion(new PostQuestion())
        user2.addPostQuestion(new PostQuestion())
        user2.addPostQuestion(new PostQuestion())
        user2.addPostQuestion(new PostQuestion())
        user2.addSuggestion(new Suggestion())
        user2.addSuggestion(new Suggestion())
        user2.setNumberOfSuggestionsApproved(2)
        user2.changeScore(1000)
        user2.addQuiz(new Quiz())
        user2.addQuiz(new Quiz())

        user3.setCourseExecutions(ceSet)
        user3.addPostQuestion(new PostQuestion())
        user3.addPostQuestion(new PostQuestion())
        user3.addSuggestion(new Suggestion())
        user3.addSuggestion(new Suggestion())
        user3.addSuggestion(new Suggestion())
        user3.setNumberOfSuggestionsApproved(3)
        user3.changeScore(100)

        then: "add to repository"
        userRepository.save(user1)
        userRepository.save(user2)
        userRepository.save(user3)
        USER_1 = new UserDto(user1)
        USER_2 = new UserDto(user2)
        USER_3 = new UserDto(user3)
    }


    def "get leaderboards"() {
        when:
        def result = leaderboardsService.getCurrentLeaderboards(1)

        then:
        // Leaderboards are in reverse order
        result.getBestScores().get(0) == USER_1
        result.getBestScores().get(1) == USER_3
        result.getBestScores().get(2) == USER_2

        result.getMostApprovedSuggestions().get(0) == USER_1
        result.getMostApprovedSuggestions().get(1) == USER_2
        result.getMostApprovedSuggestions().get(2) == USER_3

        result.getMostPosts().get(0) == USER_3
        result.getMostPosts().get(1) == USER_1
        result.getMostPosts().get(2) == USER_2

        result.getMostQuizzesSolved().get(0) == USER_3
        result.getMostQuizzesSolved().get(1) == USER_2
        result.getMostQuizzesSolved().get(2) == USER_1

        result.getMostTournamentsParticipated().get(0) == USER_3
        result.getMostTournamentsParticipated().get(1) == USER_2
        result.getMostTournamentsParticipated().get(2) == USER_1
    }

    @TestConfiguration
    static class LeaderboardsServiceImplTestContextConfiguration {
        @Bean
        LeaderboardsService LeaderboardsService() {
            return new LeaderboardsService()
        }
    }
}
