package pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.api.TopicController;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.SuggestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.domain.Suggestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.dto.SuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
public class SuggestionController {
    private static Logger logger = LoggerFactory.getLogger(SuggestionController.class);

    @Autowired
    private SuggestionService suggestionService;

    @GetMapping("/courses/{courseExecutionId}/suggestions")
    @PreAuthorize("(hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')) and hasPermission(#courseId, 'COURSE.ACCESS')")
    public List<SuggestionDto> getCourseExecutionSuggestions(@PathVariable int courseId, @Valid @RequestBody UserDto userDto) {
        return this.suggestionService.suggestionList(courseId, userDto);
    }

    @PostMapping(value = "/courses/{courseExecutionId}/suggestions")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public SuggestionDto createSuggestion(@PathVariable int courseExecutionId, @Valid @RequestBody SuggestionDto suggDto) {
        return this.suggestionService.createSuggestion(courseExecutionId, suggDto);
    }

    @PutMapping(value = "courses/{courseExecutionId}/suggestions")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS9')")
    public SuggestionDto approveSuggestion(@PathVariable int courseExecutionId, @Valid @RequestBody SuggestionDto suggDto, @Valid @RequestBody UserDto userDto) {
        return this.suggestionService.approveSuggestion(courseExecutionId, suggDto, userDto);
    }

    /*
    @DeleteMapping("/topics/{topicId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#topicId, 'TOPIC.ACCESS')")
    public ResponseEntity removeTopic(@PathVariable Integer topicId) {
        topicService.removeTopic(topicId);
        return ResponseEntity.ok().build();
    }*/

}
