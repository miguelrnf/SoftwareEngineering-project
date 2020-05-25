package pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.SuggestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.dto.ListByUsernameDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.dto.SuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
public class SuggestionController {
    private static Logger logger = LoggerFactory.getLogger(SuggestionController.class);

    @Autowired
    private SuggestionService suggestionService;

    @PostMapping(value = "/courses/{courseExecutionId}/suggestions")
    @PreAuthorize("(hasRole('ROLE_STUDENT') and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS'))")
    public SuggestionDto createSuggestion(@PathVariable int courseExecutionId, @Valid @RequestBody SuggestionDto suggDto) {
        return this.suggestionService.createSuggestion(courseExecutionId, suggDto);
    }

    @PutMapping(value = "/courses/{courseExecutionId}/suggestions/approve")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS'))")
    public SuggestionDto approveSuggestion(Principal principal, @PathVariable int courseExecutionId, @Valid @RequestBody SuggestionDto suggDto) {
        User user = (User)((Authentication)principal).getPrincipal();
        return this.suggestionService.approveSuggestion(courseExecutionId, suggDto, new UserDto(user));
    }

    @PutMapping(value = "/courses/{courseExecutionId}/suggestions/edit")
    @PreAuthorize("(hasRole('ROLE_STUDENT') and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS'))")
    public SuggestionDto editSuggestion(Principal principal,@PathVariable int courseExecutionId, @Valid @RequestBody SuggestionDto suggDto) {
        User user = (User)((Authentication)principal).getPrincipal();
        return this.suggestionService.editSuggestion(suggDto);
    }

    @DeleteMapping(value = "/courses/{courseExecutionId}/suggestions/delete/{suggestionId}")
    @PreAuthorize("((hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')) and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS'))")
    public SuggestionDto deleteSuggestion(Principal principal,@PathVariable int courseExecutionId, @PathVariable int suggestionId) {
        User user = (User)((Authentication)principal).getPrincipal();
        return this.suggestionService.deleteSuggestion(courseExecutionId,  suggestionId,  user.getUsername());
    }

    @GetMapping(value = "/courses/{courseExecutionId}/suggestions/listall")
    @PreAuthorize("( (hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')) and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS'))")
    public List<SuggestionDto> listAllSuggestions(Principal principal,@PathVariable int courseExecutionId) {

        User user = (User)((Authentication)principal).getPrincipal();

        return this.suggestionService.listAllSuggestions(new UserDto(user));
    }

    @GetMapping(value = "/courses/{courseExecutionId}/suggestions/listallbyusername/{username}")
    @PreAuthorize("( (hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')) )")
    public ListByUsernameDto listAllSuggestionsbyUsername(@PathVariable String courseExecutionId, @PathVariable String username) {

        return this.suggestionService.listAllSuggestionsbyUsername(username);
    }

    @PostMapping(value = "/courses/{courseId}/suggestions/newquestion")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and hasPermission(#courseId, 'COURSE.ACCESS')) or hasRole('ROLE_ADMIN')")
    public QuestionDto addQuestion(Principal principal, @PathVariable int courseId, @Valid @RequestBody SuggestionDto suggDto) {
        User user = (User)((Authentication)principal).getPrincipal();
        return this.suggestionService.addQuestion(courseId, suggDto, new UserDto(user));
    }

}
