package pt.ulisboa.tecnico.socialsoftware.tutor.post.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.PostService;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.*;

import javax.validation.Valid;
import java.util.Set;

@RestController
public class PostController {
    //TODO - USE POSTID
    private static Logger logger = LoggerFactory.getLogger(PostController.class);

    private PostService postService;

    @Value("${figures.dir}")
    private String figuresDir;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("executions/{executionId}/posts/submit")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public PostDto createPost(@PathVariable int executionId, @Valid @RequestBody PostQuestionDto postQ) {
        return postService.submitPost(postQ);
    }

    @PutMapping("executions/{executionId}/posts/{postId}/edit")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public PostDto editPost(@PathVariable int executionId, @PathVariable int postId,
                            @Valid @RequestBody PostQuestionDto postQ) {
        return postService.editPost(postQ);
    }

    @PutMapping("executions/{executionId}/posts/{postId}/edit/discuss")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public PostDto changeDiscussStatus(@PathVariable int executionId, @PathVariable int postId,
                            @Valid @RequestBody PostDto post) {
        return postService.changeDiscussStatus(post);
    }

    @DeleteMapping("executions/{executionId}/posts/{postId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public PostDto deletePost(@PathVariable int executionId, @PathVariable int postId,
                              @Valid @RequestBody PostDto post) {
        return postService.deletePost(post);
    }

    @PostMapping("executions/{executionId}/posts/{postId}/answer")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public PostDto answerPost(@PathVariable int executionId, @PathVariable int postId,
                              @Valid @RequestBody PostAnswerDto postA) {
        return postService.answerQuestion(postA);
    }

    @PutMapping("executions/{executionId}/posts/{postId}/answer/edit")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public PostDto editAnswer(@PathVariable int executionId, @PathVariable int postId,
                              @Valid @RequestBody PostAnswerDto postA) {
        return postService.editAnswer(postA);
    }

    @PutMapping("executions/{executionId}/posts/{postId}/edit/status")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS'))")
    public PostDto changePostStatus(@PathVariable int executionId, @PathVariable int postId,
                                    @Valid @RequestBody PostAndUserDto postUser) {
        return postService.changePostStatus(postUser.getPost(), postUser.getUser());
    }

    @PutMapping("executions/{executionId}/posts/{postId}/redirect")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS'))")
    public PostDto redirect(@PathVariable int executionId, @PathVariable int postId,
                            @Valid @RequestBody PostPostUserDto ppu) {
        return postService.redirect(ppu.getPostNotAnswered(), ppu.getPostAnswered(), ppu.getUserT());
    }
    @GetMapping("executions/{executionId}/posts/{postId}")
    @PreAuthorize("(hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS'))" +
            "or (hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS'))")
    public PostDto viewPost(@PathVariable int executionId, @PathVariable int postId) {
        return postService.viewPost(postId);
    }

    @GetMapping("executions/{executionId}/comments/search")
    @PreAuthorize("(hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS'))" +
            "or (hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS'))")
    public Set<PostCommentDto> searchComment(@PathVariable int executionId, @Valid @RequestBody String toFind) {
        return postService.searchComment(toFind);
    }

    @PutMapping("executions/{executionId}/posts/{postId}/comment")
    @PreAuthorize("(hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS'))" +
            "or (hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS'))")
    public PostCommentDto comment(@PathVariable int executionId, @PathVariable String postId, @Valid @RequestBody PostCommentDto comment) {
        return postService.postComment(comment);
    }
}
