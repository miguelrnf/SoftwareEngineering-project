package pt.ulisboa.tecnico.socialsoftware.tutor.post.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.PostService;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostAndUserDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import javax.validation.Valid;

@RestController
public class PostController {
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
        return postService.submitPost(executionId, postQ);
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
}

    //TODO - DO SOMETHING WITH POSTID AND EXECUTIONID
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
}
