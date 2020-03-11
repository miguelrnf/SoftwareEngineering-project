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
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

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
        Integer questionKey = postQuestionDto.getQuestion().getKey();
        String username = postQuestionDto.getUser().getUsername();
        User user = checkIfUserExists(username);

        checkIfUserHasRoleStudent(user);
        Question question = checkIfQuestionExists(questionKey);
        checkIfUserAnsweredQuestion(questionKey, user);
        int maxPostNumber = getMaxPostNumber();

        Post post = new Post(maxPostNumber, new PostQuestion(question, user, postQuestionDto));
        post.setCreationDate(LocalDateTime.now());
        this.entityManager.persist(post);
        return new PostDto(post);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public PostDto deletePost(PostDto toDelete) {
        User user = checkIfUserExists(toDelete.getQuestion().getUser().getUsername());
        Post post = checkIfPostExists(toDelete.getKey());
        checkIfUserOwnsPost(user, post);

        entityManager.remove(post);
        orphanRemoval(post);
        return new PostDto(post);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public PostDto editPost(PostDto toEdit, UserDto userDto) {
        User user = checkIfUserExists(userDto.getUsername());
        Post post = checkIfPostExists(toEdit.getKey());
        checkIfUserOwnsPost(user, post);

        post.getQuestion().update(toEdit.getQuestion().getStudentQuestion());
        return new PostDto(post);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public PostDto changeStatus(PostDto postDto, UserDto userDto) {
        Post post = checkIfPostExists(postDto.getKey());
        User user = checkIfUserExists(userDto.getUsername());
        if(!checkIfUserHasRoleTeacher(user))
            checkIfUserOwnsPost(user, post);

        post.changePostStatus();
        return new PostDto(post);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public PostDto viewPost(Integer key) {
        Post post = checkIfPostExists(key);
        return new PostDto(post);
    }

    private boolean checkIfUserHasRoleTeacher(User user) {
        return user.getRole().compareTo(User.Role.TEACHER) == 0;
    }

    private void checkIfUserOwnsPost(User user, Post post) {
        user.getPostQuestions().stream().filter(x -> x.getPost() == post)
                .findAny().orElseThrow(() -> new TutorException(NOT_YOUR_POST));
    }

    private Post checkIfPostExists(Integer key) {
        return postRepository.findByKey(key).orElseThrow(() -> new TutorException(INVALID_POST, key));
    }

    private User checkIfUserExists(String username) {
        User u = userRepository.findByUsername(username);
        if(u == null)  throw new TutorException(USERNAME_NOT_FOUND);
        return u;
    }


    private int getMaxPostNumber() {
        return postRepository.getMaxPostNumber() == null ? 0
                    : postRepository.getMaxPostNumber() + 1;
    }

    private void checkIfUserAnsweredQuestion(Integer questionKey, User user) {
        user.getQuizAnswers().stream().map(x -> x.getQuestionAnswers()
                .stream().filter(y -> y.getQuizQuestion().getQuestion().getKey().equals(questionKey)))
                .findAny().orElseThrow(() -> new TutorException(USER_HAS_NOT_ANSWERED, questionKey));
    }

    private void checkIfUserHasRoleStudent(User user) {
        if(user.getRole().compareTo(User.Role.STUDENT) != 0) throw new TutorException(USER_HAS_WRONG_ROLE);
    }

    private Question checkIfQuestionExists(Integer questionKey) {
        return questionRepository.findByKey(questionKey)
                    .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionKey));
    }

    private void orphanRemoval(Post post) {
        post.getQuestion().setPost(null);
        if(post.getComments() != null)
            post.getComments().forEach(x -> x.setPost(null));
        post.setComments(null);
    }
}
