package pt.ulisboa.tecnico.socialsoftware.tutor.post.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.PostService;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostCommentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostQuestionDto;

import javax.validation.Valid;
import java.util.Set;

@RestController
public class PostController {
    private static Logger logger = LoggerFactory.getLogger(PostController.class);

    private PostService postService;

    @Value("${figures.dir}")
    private String figuresDir;

    public PostController(PostService postService) {
        this.postService = postService;
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

    //TODO - REMOVE WHEN MERGING IMNTO DEV
    @PutMapping("executions/{executionId}/posts/{postId}/comment")
    @PreAuthorize("(hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS'))" +
            "or (hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS'))")
    public PostCommentDto comment(@PathVariable int executionId, @PathVariable String postId, @Valid @RequestBody PostCommentDto comment) {
        return postService.postComment(comment);
    }
    //TODO - REMOVE WHEN MERGING IMNTO DEV

    //TODO - REMOVE WHEN MERGING INTO DEVELOP
    @PostMapping("executions/{executionId}/posts/submit")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public PostDto createPost(@PathVariable int executionId, @Valid @RequestBody PostQuestionDto postQ) {
        return postService.submitPost(postQ);
    }
    //TODO - REMOVE WHEN MERGING INTO DEVELOP

}
