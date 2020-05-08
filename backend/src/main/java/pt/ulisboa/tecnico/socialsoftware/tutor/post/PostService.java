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
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.repository.PostCommentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.repository.PostQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.repository.PostRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostQuestionRepository postQuestionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private PostCommentRepository commentRepository;

    @Autowired
    private QuizService quizService;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public PostDto submitPost(PostQuestionDto postQuestionDto) { //TODO - add executionId to post domain
        String username = postQuestionDto.getUser().getUsername();
        User user = checkIfUserExistsByUsername(username);

        checkIfUserHasRoleStudent(user);
        Question question = checkIfQuestionExists(postQuestionDto);
        checkIfUserAnsweredQuestion(postQuestionDto.getQuestion(), user);
        int maxPostNumber = getMaxPostNumber();
        Post post = new Post(maxPostNumber, new PostQuestion(question, user, postQuestionDto.getStudentQuestion()));

        post.setCreationDate(LocalDateTime.now());
        post.getQuestion().setPost(post);
        user.addPostQuestion(post.getQuestion());
        entityManager.persist(post);
        return new PostDto(post);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public PostDto deletePost(int toDelete, User user) {
        User u = checkIfUserExistsByUsername(user.getUsername());
        Post post = checkIfPostExists(null, toDelete);
        if(user.getRole() == User.Role.STUDENT) checkIfUserOwnsPost(user, post);

        post.remove();
        postRepository.delete(post);
        return new PostDto(post);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public PostDto editPost(PostQuestionDto toEdit) {
        User user = checkIfUserExistsByUsername(toEdit.getUser().getUsername());
        Post post = checkIfPostExists(toEdit.getPost(), null);
        checkIfUserOwnsPost(user, post);

        post.getQuestion().update(toEdit.getStudentQuestion());
        return new PostDto(post);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public PostDto changePostStatus(int id, User u) {
        Post post = checkIfPostExists(null, id);
        User user = checkIfUserExistsByUsername(u.getUsername());
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
        User user = checkIfUserExistsByUsername(toAnswer.getUser().getUsername());
        Post post = checkIfPostExists(toAnswer.getPost(), null);

        checkIfUserHasRoleTeacher(user);
        post.getAnswer().update(toAnswer.getTeacherAnswer());
        return new PostDto(post);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public PostDto changeDiscussStatus(int id, User u) {
        Post post = checkIfPostExists(null, id);
        User user = checkIfUserExistsByUsername(u.getUsername());
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
        Post post = checkIfPostExists(answerDto.getPost(), null);
        User user = checkIfUserExistsByUsername(answerDto.getUser().getUsername());
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
        Post postNotAnswered = checkIfPostExists(postDto1, null);
        Post postAnswered = checkIfPostExists(postDto2, null);
        PostAnswer answer = checkIfAnswered(postAnswered);
        User user = checkIfUserExistsByUsername(userDto.getUsername());
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
        Post post = checkIfPostExists(null, key);
        return new PostDto(post);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ListPostsDto postPagination(ListPostsDto dto) {
        int offset = dto.getPage() * dto.getPerPage() - dto.getPerPage();
        List<Post> posts = postRepository.findByPage(dto.getPerPage(), offset).orElse(null);
        List<PostDto> postDto = posts != null ? posts.stream().map(PostDto::new).collect(Collectors.toList()) : null;
        dto.setLists(postDto);

        return dto;
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
        Post post = checkIfPostExists(dto.getPost(), null);
        User user = checkIfUserExistsByUsername(dto.getUser().getUsername());
        PostComment comment = new PostComment(dto.getKey(), user, post, dto);
        if(dto.getParent() != null) {
            PostComment parent = checkIfCommentParentExists(dto);
            comment.setParent(parent);
            parent.addChild(comment);
            post.addComment(comment);
        }
        comment.setPost(post);
        commentRepository.save(comment);
        return new PostCommentDto(comment, false);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ListPostsDto postPagination(int perPage, int page) {
        if (perPage == - 1) perPage = postRepository.getTotalPosts();
        //int offset = page * perPage - perPage;
        int offset = postRepository.getTotalPosts() - page * perPage;
        List<Post> posts = postRepository.findByPage(perPage, Math.max(offset, 0)).orElse(null);
        List<PostDto> postDto = posts != null ? posts.stream().map(PostDto::new).collect(Collectors.toList()) : null;
        ListPostsDto dto = new ListPostsDto();
        dto.setLists(postDto);
        dto.setPage(page);
        dto.setPerPage(perPage);
        dto.setTotalPosts(postRepository.getTotalPosts());
        return dto;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ListPostsDto postsByUser(String user) {
        User u = checkIfUserExistsByUsername(user);
        List<PostQuestion> postQs = postQuestionRepository.findByUser(u.getId());
        List<PostDto> postDto = postQs != null ? postQs.stream().map(PostQuestion::getPost).map(PostDto::new).collect(Collectors.toList()) : null;
        ListPostsDto dto = new ListPostsDto();
        dto.setLists(postDto);
        dto.setTotalPosts(postQs != null ? postDto.size() : 0);
        return dto;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ListPostsDto postsByQuiz(int quizid) {
        QuizDto quiz = quizService.findById(quizid);
        List<PostDto> posts = quiz.getQuestions().stream()
                .flatMap(x -> postQuestionRepository.findByQuestion(x.getId()).filter(y -> !y.isEmpty()).orElse(new ArrayList<>()).stream())
                .map(y -> new PostDto(y.getPost())).collect(Collectors.toList());

        ListPostsDto dto = new ListPostsDto();
        dto.setLists(posts);
        dto.setTotalPosts(posts.size());
        return dto;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public PostDto changePostPrivacy(int id, User u) {
        Post post = checkIfPostExists(null, id);
        User user = checkIfUserExistsByUsername(u.getUsername());
        checkIfUserOwnsPost(user, post);

        post.changePostPrivacy();
        return new PostDto(post);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public PostDto changeAnswerPrivacy(int id, User u) {
        Post post = checkIfPostExists(null, id);
        User user = checkIfUserExistsByUsername(u.getUsername());
        checkIfUserHasRoleTeacher(user);
        checkIfAnswered(post);

        post.changeAnswerPrivacy();
        return new PostDto(post);
    }

    private PostComment checkIfCommentParentExists(PostCommentDto dto) {
        if(dto.getKey() != null) {
            return commentRepository.findByKey(dto.getParent().getKey()).orElseThrow(() -> new TutorException(COMMENT_NO_PARENT));
        }
        else {
            return commentRepository.findById(dto.getParent().getId()).orElseThrow(() -> new TutorException(COMMENT_NO_PARENT));
        }

    }

    private void checkIfPostsHaveSameQuestion(PostDto postDto1, PostDto postDto2) {
        if(postDto2.getQuestion() == null)
            throw new TutorException(NO_ANSWER);
         if(!postDto1.getQuestion().getQuestion().getContent().equals(postDto2.getQuestion().getQuestion().getContent()))
             throw new TutorException(DIFFERENT_QUESTION);
    }

    private void checkIfUserOwnsPost(User user, Post post) {
        int toSearch = post.getKey() != null ? post.getKey() : post.getId();
        user.getPostQuestions().stream().filter(x -> x.getPost().getKey() != null ? x.getPost().getKey().equals(toSearch) : x.getPost().getId().equals(toSearch))
                .findAny().orElseThrow(() -> new TutorException(NOT_YOUR_POST));
    }

    private User checkIfUserExistsByUsername(String username) {
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

    private void checkIfUserAnsweredQuestion(QuestionDto question, User user) {
        if(question.getKey() != null) {
            int questionKey = question.getKey();
            user.getQuizAnswers().stream().map(x -> x.getQuestionAnswers()
                    .stream().filter(y -> y.getQuizQuestion().getQuestion().getKey().equals(questionKey)))
                    .findAny().orElseThrow(() -> new TutorException(USER_HAS_NOT_ANSWERED, questionKey));
        }
        else {
            int questionId = question.getId();
            user.getQuizAnswers().stream().map(x -> x.getQuestionAnswers()
                    .stream().filter(y -> y.getQuizQuestion().getQuestion().getId().equals(questionId)))
                    .findAny().orElseThrow(() -> new TutorException(USER_HAS_NOT_ANSWERED, questionId));
        }
    }

    private void checkIfUserHasRoleStudent(User user) {
        if(user.getRole().compareTo(User.Role.STUDENT) != 0) throw new TutorException(USER_HAS_WRONG_ROLE);
    }

    private void checkIfUserHasRoleTeacher(User user) {
        if(user.getRole().compareTo(User.Role.TEACHER) != 0) throw new TutorException(USER_HAS_WRONG_ROLE);
    }

    private Question checkIfQuestionExists(PostQuestionDto question) {
        if(question.getQuestion().getKey() != null) {
            return questionRepository.findByKey(question.getQuestion().getKey())
                    .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, question.getQuestion().getKey()));
        }
        else {
            return questionRepository.findByKey(question.getQuestion().getId())
                    .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, question.getQuestion().getId()));
        }
    }

    private Post checkIfPostExists(PostDto post, Integer pid) {
        if (post != null) {
            if(post.getKey() != null) {
                return postRepository.findByKey(post.getKey()).orElseThrow(() -> new TutorException(INVALID_POST, post.getKey()));
            }
            else {
                return postRepository.findById(post.getId()).orElseThrow(() -> new TutorException(INVALID_POST, post.getId()));
            }
        }
        else {
            try {
                return postRepository.findByKey(pid).orElseThrow(Exception::new);
            } catch(Exception e) {
                return postRepository.findById(pid).orElseThrow(() -> new TutorException(INVALID_POST, pid));
            }

        }
    }
}
