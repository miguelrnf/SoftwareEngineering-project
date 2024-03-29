package pt.ulisboa.tecnico.socialsoftware.tutor.classroom.api;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.classroom.ClassroomService;
import pt.ulisboa.tecnico.socialsoftware.tutor.classroom.dto.ClassroomDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.classroom.dto.DocumentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.EvalSettingsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.SolvedQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;


@RestController
public class ClassroomController {

    private ClassroomService classroomService;

    public ClassroomController(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }

    @PostMapping(value = "/courses/{courseExecutionId}/classroom/create")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS'))")
    public ClassroomDto createClassroom(@PathVariable int courseExecutionId, @Valid @RequestBody ClassroomDto classroomDto) {
        return this.classroomService.createClassroom(courseExecutionId, classroomDto);
    }

    @PutMapping(value = "/courses/{courseExecutionId}/classroom/edit")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS'))")
    public ClassroomDto editClassroom(@PathVariable int courseExecutionId, @Valid @RequestBody ClassroomDto classroomDto) {
        return this.classroomService.editClassroom(courseExecutionId, classroomDto);
    }

    @PostMapping(value = "/courses/{courseExecutionId}/classroom/newFile/{documentId}")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS'))")
    public byte[] newFile(@PathVariable int courseExecutionId, @PathVariable int documentId, @Valid @RequestBody byte[] o) {

        return this.classroomService.upload(courseExecutionId,documentId, java.util.Base64.getEncoder().encode(o) );
    }

    @GetMapping(value = "/courses/{courseExecutionId}/classroom/getFile/{documentId}")
    @PreAuthorize("( (hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')) and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS'))")
    public byte[] getFile(@PathVariable int courseExecutionId, @PathVariable int documentId) {
        return this.classroomService.getFile(courseExecutionId,documentId);
    }

    @PostMapping(value = "/courses/{courseExecutionId}/classroom/newDoc")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS'))")
    public DocumentDto newDocument(@PathVariable int courseExecutionId, @Valid @RequestBody DocumentDto documentDto) {
        return this.classroomService.addDocument(documentDto.getClassroomId(), documentDto);
    }



    @PutMapping(value = "/courses/{courseExecutionId}/classroom/editDoc")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS'))")
    public DocumentDto editDocument(@PathVariable int courseExecutionId, @Valid @RequestBody DocumentDto documentDto) {
        return this.classroomService.editDocument(documentDto.getClassroomId(), documentDto);
    }

    @GetMapping(value = "/courses/{courseExecutionId}/classroom/list/{type}")
    @PreAuthorize("( (hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')) and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS'))")
    public List<ClassroomDto> listClassrooms(Principal principal, @PathVariable int courseExecutionId, @PathVariable String type) {

        User user = (User)((Authentication)principal).getPrincipal();

        return this.classroomService.getClassroomsByType(user.getRole(), courseExecutionId, type);
    }

    @GetMapping(value = "/courses/{courseExecutionId}/classroom/listQuizzes/{classroomId}")
    @PreAuthorize("( (hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')) and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS'))")
    public List<StatementQuizDto> listClassroomAvailableQuizzes(Principal principal, @PathVariable int courseExecutionId, @PathVariable int classroomId) {

        User user = (User)((Authentication)principal).getPrincipal();

        return this.classroomService.getClassroomAvailableQuizzes(user.getId(), courseExecutionId, classroomId);
    }

    @GetMapping(value = "/courses/{courseExecutionId}/classroom/listSolvedQuizzes/{classroomId}")
    @PreAuthorize("( (hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')) and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS'))")
    public List<SolvedQuizDto> listClassroomSolvedQuizzes(Principal principal, @PathVariable int courseExecutionId, @PathVariable int classroomId) {

        User user = (User)((Authentication)principal).getPrincipal();

        return this.classroomService.getSolvedClassroomQuizzes(user.getId(), courseExecutionId, classroomId);
    }

    @PutMapping(value = "/courses/{courseExecutionId}/classroom/changeStatus")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS'))")
    public ClassroomDto changeStatus(@PathVariable int courseExecutionId, @Valid @RequestBody ClassroomDto classroomDto) {
        return this.classroomService.changeStatus(courseExecutionId, classroomDto);
    }

    @DeleteMapping(value = "/courses/{courseExecutionId}/classroom/delete/{classroomId}")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS'))")
    public ClassroomDto deleteClassroom(@PathVariable int courseExecutionId, @PathVariable int classroomId) {
        return this.classroomService.removeClassroom(courseExecutionId, classroomId);
    }

    @DeleteMapping(value = "/courses/{courseExecutionId}/classroom/deleteDoc/{classroomId}/{documentId}")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS'))")
    public DocumentDto deleteDocument(@PathVariable int courseExecutionId, @PathVariable int classroomId, @PathVariable int documentId) {
        return this.classroomService.removeDocument(courseExecutionId, classroomId, documentId);
    }

    @PutMapping(value = "/courses/{courseExecutionId}/classroom/addQuizzes")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS'))")
    public ClassroomDto addQuizzes(@PathVariable int courseExecutionId, @Valid @RequestBody ClassroomDto classroomDto) {
        return this.classroomService.addQuizzes(courseExecutionId, classroomDto);
    }

    @DeleteMapping(value = "/courses/{courseExecutionId}/classroom/deleteQuiz/{classroomId}/{quizId}")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS'))")
    public ClassroomDto deleteQuiz(@PathVariable int courseExecutionId, @PathVariable int classroomId, @PathVariable int quizId) {
        return this.classroomService.removeQuiz(courseExecutionId, classroomId, quizId);
    }

    @PutMapping(value = "/courses/{courseExecutionId}/classroom/{classroomId}/{quizId}/{eval}")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS'))")
    public ClassroomDto setEval(@PathVariable int courseExecutionId, @PathVariable int classroomId, @PathVariable int quizId, @PathVariable boolean eval) {
        return this.classroomService.setEvaluation(courseExecutionId, classroomId, quizId, eval);
    }

    @PutMapping(value = "/courses/{courseExecutionId}/classroom/evalSettings")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS'))")
    public EvalSettingsDto setEvalSettings(@PathVariable int courseExecutionId, @Valid @RequestBody EvalSettingsDto evalSettingsDto) {
        return this.classroomService.changeEvalSettings(courseExecutionId, evalSettingsDto);
    }

    @GetMapping(value = "/courses/{courseExecutionId}/classroom/getEvalSettings")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS'))")
    public EvalSettingsDto getEvalSettings(@PathVariable int courseExecutionId) {
        return this.classroomService.getEvalSettings(courseExecutionId);
    }
}
