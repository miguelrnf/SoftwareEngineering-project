package pt.ulisboa.tecnico.socialsoftware.tutor.post.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.PostService;
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

    @PostMapping("executions/{executionId}/posts/{postId}/answer")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public PostDto answerPost(@PathVariable int executionId, @PathVariable int postId,
                              @Valid @RequestBody PostAnswerDto postA) {
        return postService.answerQuestion(postA);
    }

    //TODO - REMOVE TEMP
    @PostMapping("executions/{executionId}/posts/submit")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public PostDto createPost(@PathVariable int executionId, @Valid @RequestBody PostQuestionDto postQ) {
        return postService.submitPost(postQ);
    }

}
