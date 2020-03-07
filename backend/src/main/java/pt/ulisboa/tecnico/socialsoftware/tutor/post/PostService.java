package pt.ulisboa.tecnico.socialsoftware.tutor.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.Post;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.PostQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.repository.PostRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_NOT_FOUND;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_HAS_NOT_ANSWERED;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public PostDto submitPost(PostQuestionDto postQuestionDto) {
        //Check if question exists
        Integer questionKey = postQuestionDto.getQuestion().getKey();
        Question question = questionRepository.findByKey(questionKey)
                .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionKey));
        //Check if user answered question
        String username = postQuestionDto.getUser().getUsername();
        User user = userRepository.findByUsername(username);
        user.getQuizAnswers().stream().map(x -> x.getQuestionAnswers()
                .stream().filter(y -> y.getQuizQuestion().getQuestion().getKey().equals(questionKey)))
                .findAny().orElseThrow(() -> new TutorException(USER_HAS_NOT_ANSWERED, questionKey));
        //Get last post key
        int maxPostNumber = postRepository.getMaxPostNumber() == null ? 0
                : postRepository.getMaxPostNumber() + 1;

        Post post = new Post(maxPostNumber, new PostQuestion(question, user, postQuestionDto));
        post.setCreationDate(LocalDateTime.now());
        this.entityManager.persist(post);
        return new PostDto(post);
    }
}
