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
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.PostComment;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.PostQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostCommentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.repository.PostCommentRepository;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class PostService {
    //TODO - USE PRINCIPAL FOR LOGGED IN USER
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private PostCommentRepository commentRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public PostDto submitPost(PostQuestionDto postQuestionDto) { //TODO - add executionId to post domain
        Integer questionKey = postQuestionDto.getQuestion().getKey();
        String username = postQuestionDto.getUser().getUsername();
        User user = checkIfUserExists(username);

        checkIfUserHasRoleStudent(user);
        Question question = checkIfQuestionExists(questionKey);
        checkIfUserAnsweredQuestion(questionKey, user);
        int maxPostNumber = getMaxPostNumber();
        Post post = new Post(maxPostNumber, new PostQuestion(question, user, postQuestionDto.getStudentQuestion()));

        post.setCreationDate(LocalDateTime.now());
        post.getQuestion().setPost(post);
        this.entityManager.persist(post);
        return new PostDto(post);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public PostDto deletePost(PostDto toDelete) {
        User user = checkIfUserExists(toDelete.getQuestion().getUser().getUsername());
        Post post = checkIfPostExistsKey(toDelete.getKey());
        checkIfUserOwnsPost(user, post);

        entityManager.remove(post);
        post.remove();
        return new PostDto(post);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public PostDto editPost(PostQuestionDto toEdit) {
        User user = checkIfUserExists(toEdit.getUser().getUsername());
        Post post = checkIfPostExistsKey(toEdit.getPost().getKey());
        checkIfUserOwnsPost(user, post);

        post.getQuestion().update(toEdit.getStudentQuestion());
        return new PostDto(post);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public PostDto changePostStatus(PostDto postDto, UserDto userDto) {
        Post post = checkIfPostExistsKey(postDto.getKey());
        User user = checkIfUserExists(userDto.getUsername());
        try {
            checkIfUserHasRoleTeacher(user);
        } catch (TutorException e) {
            checkIfUserOwnsPost(user, post);
        }
        post.changePostStatus();
        return new PostDto(post);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public PostDto editAnswer(PostAnswerDto toAnswer) {
        User user = checkIfUserExists(toAnswer.getUser().getUsername());
        Post post = checkIfPostExistsKey(toAnswer.getPost().getKey());

        checkIfUserHasRoleTeacher(user);
        post.getAnswer().update(toAnswer.getTeacherAnswer());
        return new PostDto(post);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public PostDto changeDiscussStatus(PostDto postDto) {
        Post post = checkIfPostExistsKey(postDto.getKey());
        User user = checkIfUserExists(postDto.getQuestion().getUser().getUsername());
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
        Post post = checkIfPostExistsKey(answerDto.getPost().getKey());
        User user = checkIfUserExists(answerDto.getUser().getUsername());
        checkIfUserHasRoleTeacher(user);

        PostAnswer answer = new PostAnswer(user, answerDto.getTeacherAnswer());
        post.setAnswer(answer);
        answer.setPost(post);
        return new PostDto(post);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public PostDto redirect(PostDto postDto1, PostDto postDto2, UserDto userDto) {
        Post postNotAnswered = checkIfPostExistsKey(postDto1.getKey());
        Post postAnswered = checkIfPostExistsKey(postDto2.getKey());
        PostAnswer answer = checkIfAnswered(postAnswered);
        User user = checkIfUserExists(userDto.getUsername());
        checkIfUserHasRoleTeacher(user);
        checkIfPostsHaveSameQuestion(postDto1, postDto2);

        postNotAnswered.setAnswer(answer);
        postNotAnswered.setDiscussStatus(false);
        postNotAnswered.setPostStatus(true);
        if((postNotAnswered.getAnswer()) != postAnswered.getAnswer()){
            throw new TutorException(ERROR_WHILE_REDIRECTING);
        }
        return new PostDto(postNotAnswered);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public PostDto viewPost(Integer key) {
        Post post = checkIfPostExistsKey(key);
        return new PostDto(post);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Set<PostCommentDto> searchComment(String string) {
        List<PostComment> comments = commentRepository.findByComment(string.trim());
        if (comments.isEmpty()) {
            throw new TutorException(INVALID_COMMENT_SEARCH);
        }
        return comments.stream().map(x -> new PostCommentDto(x, false)).collect(Collectors.toSet());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public PostCommentDto postComment(PostCommentDto dto) {
        Post post = checkIfPostExistsKey(dto.getPost().getKey());
        User user = checkIfUserExists(dto.getUser().getUsername());
        PostComment comment = new PostComment(dto.getKey(), user, post, dto);
        if(dto.getParent() != null) {
            PostComment parent = checkIfCommentParentExists(dto);
            comment.setParent(parent);
            parent.addChild(comment);
            post.addComment(comment);
        }
        comment.setPost(post);
        return new PostCommentDto(comment, false);
    }

    private PostComment checkIfCommentParentExists(PostCommentDto dto) {
        return commentRepository.findByKey(dto.getParent().getKey()).orElseThrow(() -> new TutorException(COMMENT_NO_PARENT));
    }

    private void checkIfPostsHaveSameQuestion(PostDto postDto1, PostDto postDto2) {
        if(postDto2.getQuestion() == null)
            throw new TutorException(NO_ANSWER);
         if(!postDto1.getQuestion().getQuestion().getContent().equals(postDto2.getQuestion().getQuestion().getContent()))
             throw new TutorException(DIFFERENT_QUESTION);
    }

    private void checkIfUserOwnsPost(User user, Post post) {
        user.getPostQuestions().stream().filter(x -> x.getPost() == post)
                .findAny().orElseThrow(() -> new TutorException(NOT_YOUR_POST));
    }

    private Post checkIfPostExistsKey(Integer key) {
        return postRepository.findByKey(key).orElseThrow(() -> new TutorException(INVALID_POST, key));
    }

    private Post checkIfPostExistsId(Integer id) {
        return postRepository.findById(id).orElseThrow(() -> new TutorException(INVALID_POST, id));
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
