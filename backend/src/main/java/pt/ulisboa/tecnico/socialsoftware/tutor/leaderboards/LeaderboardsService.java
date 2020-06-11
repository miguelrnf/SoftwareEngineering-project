package pt.ulisboa.tecnico.socialsoftware.tutor.leaderboards;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.leaderboards.dto.LeaderboardsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.Post;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.repository.PostRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaderboardsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public LeaderboardsDto getCurrentLeaderboards(int courseExecutionId) {
        //List<User> users = userRepository.findByCourseExecution(courseExecutionId);
        // List<Post> posts = postRepository.findAll(); TODO - posts need votes
        List<User> users = userRepository.findAll();

        List<UserDto> bestScores = users.stream()
                .sorted(Comparator.comparingInt(User::getScore))
                .limit(10).map(UserDto::new).collect(Collectors.toList());

        List<UserDto> mostApprovedSuggestions = users.stream()
                .sorted(Comparator.comparingInt(User::getNumberApprovedSuggestions))
                .limit(10).map(UserDto::new).collect(Collectors.toList());

        List<UserDto> mostPosts = users.stream()
                .sorted(Comparator.comparingInt((User u) -> u.getPostQuestions().size()))
                .limit(10).map(UserDto::new).collect(Collectors.toList());

        List<UserDto> mostQuizzesSolved = users.stream()
                .sorted(Comparator.comparingInt((User u) -> u.getQuizzes().size()))
                .limit(10).map(UserDto::new).collect(Collectors.toList());

        List<UserDto> mostTournamentsParticipated = users.stream()
                .sorted(Comparator.comparingInt((User u) -> u.getTournaments().size()))
                .limit(10).map(UserDto::new).collect(Collectors.toList());

        // List<Post> mostUpvoted = posts.stream()
        //        .sorted(Comparator.comparingInt(Post::getUpvotes)).limit(10).collect(Collectors.toList());

        return new LeaderboardsDto(bestScores, mostApprovedSuggestions, mostPosts,
                mostQuizzesSolved, mostTournamentsParticipated);
    }
}
