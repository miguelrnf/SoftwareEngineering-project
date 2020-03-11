package pt.ulisboa.tecnico.socialsoftware.tutor.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.Post;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.PostAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.PostQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostAnswerDto;
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

        Post post = new Post(maxPostNumber, new PostQuestion(question, user, postQuestionDto.getStudentQuestion()));
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
    public PostDto changePostStatus(PostDto postDto, UserDto userDto) {
        Post post = checkIfPostExists(postDto.getKey());
        User user = checkIfUserExists(userDto.getUsername());
        try {
            checkIfUserHasRoleTeacher(user);
        } catch(TutorException e) {
            checkIfUserOwnsPost(user, post);
        }
        post.changePostStatus();
        return new PostDto(post);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public PostDto changeDiscussStatus(PostDto postDto, UserDto userDto) {
        Post post = checkIfPostExists(postDto.getKey());
        User user = checkIfUserExists(userDto.getUsername());
        checkIfUserOwnsPost(user, post);
        checkIfAnswered(post);

        post.changeDiscussStatus();
        return new PostDto(post);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public PostDto answerQuestion(PostAnswerDto answerDto) {
        Post post = checkIfPostExists(answerDto.getPost().getKey());
        User user = checkIfUserExists(answerDto.getUser().getUsername());
        checkIfUserHasRoleTeacher(user);

        PostAnswer answer = new PostAnswer(user, answerDto.getTeacherAnswer());
        post.setAnswer(answer);
        return new PostDto(post);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public PostDto redirect(PostDto postDto1, PostDto postDto2, UserDto userDto) {
        System.out.println("*************************************************************");
        System.out.println(postDto1.getKey());
        System.out.println(postDto2.getKey());
        System.out.println("*************************************************************");
        Post postNotAnswered = checkIfPostExists(postDto1.getKey());
        Post postAnswered = checkIfPostExists(postDto2.getKey());
        PostAnswer answer = checkIfAnswered(postAnswered);
        User user = checkIfUserExists(userDto.getUsername());
        checkIfUserHasRoleTeacher(user);
        if(!postDto1.getQuestion().getQuestion().getKey().equals(postDto2.getQuestion().getQuestion().getKey())){
            throw new TutorException(DIFFERENT_QUESTION);
        }
        if(postDto2.getQuestion() == null){
            throw new TutorException(NO_ANSWER);
        }
        postNotAnswered.setAnswer(answer);
        postNotAnswered.setDiscussStatus(false);
        postNotAnswered.setPostStatus(true);
        if((postNotAnswered.getAnswer()) != postAnswered.getAnswer()){
            throw new TutorException(ERROR_WHILE_REDIRECTING);
        }
        return new PostDto(postNotAnswered);
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

    private PostAnswer checkIfAnswered(Post post) {
        if(post.getAnswer() == null){throw new TutorException(NO_ANSWER);}
        return post.getAnswer();
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

    private void checkIfUserHasRoleTeacher(User user) {
        if(user.getRole().compareTo(User.Role.TEACHER) != 0) throw new TutorException(USER_HAS_WRONG_ROLE);
    }

    private Question checkIfQuestionExists(Integer questionKey) {
        return questionRepository.findByKey(questionKey)
                    .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionKey));
    }
}
