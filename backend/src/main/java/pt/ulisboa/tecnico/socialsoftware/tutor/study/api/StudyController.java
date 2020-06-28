package pt.ulisboa.tecnico.socialsoftware.tutor.study.api;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.SolvedQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementCreationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.study.StudyService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.security.Principal;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;

@RestController
public class StudyController {

    private StudyService studyService;

    public StudyController(StudyService studyService) {
        this.studyService = studyService;
    }

    @GetMapping(value = "/courses/{courseId}/study/getTopicQuestions/{topicName}")
    @PreAuthorize("( (hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')) and hasPermission(#courseId, 'COURSE.ACCESS'))")
    public List<QuestionDto> answeredQuestionsOfTopic(Principal principal, @PathVariable int courseId, @PathVariable String topicName) {

        User user = (User)((Authentication)principal).getPrincipal();

        return this.studyService.answeredQuestionsOfTopic(courseId, new UserDto(user), topicName);
    }

    @GetMapping(value = "/courses/{executionId}/study/getAvailableTopics")
    @PreAuthorize("( (hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')) and hasPermission(#executionId, 'EXECUTION.ACCESS'))")
    public List<TopicDto> answeredQuestionsOfTopic(@PathVariable int executionId) {
        return this.studyService.getAvailableTopics(executionId);
    }

    @GetMapping(value = "/courses/{executionId}/study/getSuggestedTopic")
    @PreAuthorize("( (hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')) and hasPermission(#executionId, 'EXECUTION.ACCESS'))")
    public String suggestionStudentTopicQuiz(Principal principal, @PathVariable int executionId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }
        return this.studyService.suggestionStudentTopicQuiz(new UserDto(user), executionId);
    }

    @PostMapping("/courses/{executionId}/study/newTopicQuiz/{topicName}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public StatementQuizDto getNewQuiz(Principal principal, @PathVariable int executionId, @PathVariable String topicName, @RequestBody StatementCreationDto quizDetails) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return studyService.generateStudentTopicQuiz(new UserDto(user), executionId, quizDetails, topicName);
    }

    @GetMapping(value = "/courses/{executionId}/study/getMyOwnQuizzes")
    @PreAuthorize("( (hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')) and hasPermission(#executionId, 'EXECUTION.ACCESS'))")
    public List<SolvedQuizDto> getOwnQuizzes(Principal principal, @PathVariable int executionId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }
        return this.studyService.getOwnQuizzes(new UserDto(user), executionId);
    }
}
