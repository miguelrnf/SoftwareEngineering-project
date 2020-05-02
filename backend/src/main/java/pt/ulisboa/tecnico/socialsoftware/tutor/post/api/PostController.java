package pt.ulisboa.tecnico.socialsoftware.tutor.post.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.PostService;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Set;

@RestController
public class PostController {

    private PostService postService;

    @Value("${figures.dir}")
    private String figuresDir;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("executions/{executionId}/posts/{postId}")
    @PreAuthorize("(hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS'))" +
            "or (hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS'))")
    public PostDto viewPost(Principal principal, @PathVariable int executionId, @PathVariable int postId) {
        return postService.viewPost(postId);
    }

    @GetMapping("executions/{executionId}/comments/search")
    @PreAuthorize("(hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS'))" +
            "or (hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS'))")
    public Set<PostCommentDto> searchComment(Principal principal, @PathVariable int executionId,
                                             @Valid @RequestBody String toFind) {
        return postService.searchComment(toFind);
    }

    @PutMapping("executions/{executionId}/posts/{postId}/comment")
    @PreAuthorize("(hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS'))" +
            "or (hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS'))")
    public PostCommentDto comment(Principal principal, @PathVariable int executionId,
                                  @PathVariable String postId, @Valid @RequestBody PostCommentDto comment) {
        User user = (User) ((Authentication) principal).getPrincipal();
        if(comment.getUser() == null) comment.setUser(new UserDto(user));
        return postService.postComment(comment);
    }

    @PostMapping("executions/{executionId}/posts/submit")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public PostDto createPost(Principal principal, @PathVariable int executionId,
                              @Valid @RequestBody PostQuestionDto postQ) {
        User user = (User) ((Authentication) principal).getPrincipal();
        if(postQ.getUser() == null) postQ.setUser(new UserDto(user));
        return postService.submitPost(postQ);
    }

    @PutMapping("executions/{executionId}/posts/{postId}/edit")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public PostDto editPost(Principal principal, @PathVariable int executionId, @PathVariable int postId,
                            @Valid @RequestBody PostQuestionDto postQ) {
        User user = (User) ((Authentication) principal).getPrincipal();
        if(postQ.getUser() == null) postQ.setUser(new UserDto(user));
        return postService.editPost(postQ);
    }

    @PutMapping("executions/{executionId}/posts/{postId}/edit/discuss")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public PostDto changeDiscussStatus(Principal principal, @PathVariable int executionId, @PathVariable int postId) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return postService.changeDiscussStatus(postId, user);
    }

    @DeleteMapping("executions/{executionId}/posts/{postId}")
    @PreAuthorize("hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public PostDto deletePost(Principal principal, @PathVariable int executionId, @PathVariable int postId) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return postService.deletePost(postId, user);
    }

    //TODO - DO SOMETHING WITH POSTID
    @PostMapping("executions/{executionId}/posts/{postId}/answer")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public PostDto answerPost(Principal principal, @PathVariable int executionId, @PathVariable int postId,
                              @Valid @RequestBody PostAnswerDto postA) {
        User user = (User) ((Authentication) principal).getPrincipal();
        if(postA.getUser() == null) postA.setUser(new UserDto(user));
        return postService.answerQuestion(postA);
    }

    @PutMapping("executions/{executionId}/posts/{postId}/answer/edit")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public PostDto editAnswer(Principal principal, @PathVariable int executionId, @PathVariable int postId,
                              @Valid @RequestBody PostAnswerDto postA) {
        User user = (User) ((Authentication) principal).getPrincipal();
        if(postA.getUser() == null) postA.setUser(new UserDto(user));
        return postService.editAnswer(postA);
    }

    @PutMapping("executions/{executionId}/posts/{postId}/edit/status")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS'))")
    public PostDto changePostStatus(Principal principal, @PathVariable int executionId, @PathVariable int postId) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return postService.changePostStatus(postId, user);
    }

    @PutMapping("executions/{executionId}/posts/{postId}/redirect")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS'))")
    public PostDto redirect(Principal principal, @PathVariable int executionId, @PathVariable int postId,
                            @Valid @RequestBody PostPostUserDto ppu) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return postService.redirect(ppu.getPostNotAnswered(), ppu.getPostAnswered(), ppu.getUserT());
    }

    @GetMapping("executions/{executionId}/posts/{perPage}/{page}")
    @PreAuthorize("hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public ListPostsDto getPage(@PathVariable int executionId, @PathVariable int perPage, @PathVariable int page) {
        return postService.postPagination(perPage, page);
    }

    @PutMapping("executions/{executionId}/posts/{postId}/edit/privacy")
    @PreAuthorize("(hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS'))")
    public PostDto changePostPrivacy(Principal principal, @PathVariable int executionId, @PathVariable int postId) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return postService.changePostPrivacy(postId, user);
    }

    @PutMapping("executions/{executionId}/posts/{postId}/answer/edit/privacy")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS'))")
    public PostDto changeAnswerPrivacy(Principal principal, @PathVariable int executionId, @PathVariable int postId) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return postService.changeAnswerPrivacy(postId, user);
    }
}
