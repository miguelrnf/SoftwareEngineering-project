package pt.ulisboa.tecnico.socialsoftware.tutor.classroom.api;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.classroom.ClassroomService;
import pt.ulisboa.tecnico.socialsoftware.tutor.classroom.dto.ClassroomDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.classroom.dto.DocumentDto;

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

    @PostMapping(value = "/courses/{courseExecutionId}/classroom/newDoc")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS'))")
    public DocumentDto newDocument(@PathVariable int courseExecutionId, @Valid @RequestBody DocumentDto documentDto) {
        return this.classroomService.addDocument(documentDto.getClassroomId(), documentDto);
    }

    @PutMapping(value = "/courses/{courseExecutionId}/classroom/editDoc")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS'))")
    public DocumentDto editClassroom(@PathVariable int courseExecutionId, @Valid @RequestBody DocumentDto documentDto) {
        return this.classroomService.editDocument(documentDto.getClassroomId(), documentDto);
    }

    @GetMapping(value = "/courses/{courseExecutionId}/classroom/list/{type}")
    @PreAuthorize("( (hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')) and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS'))")
    public List<ClassroomDto> listClassrooms(Principal principal, @PathVariable int courseExecutionId, @PathVariable String type) {

        User user = (User)((Authentication)principal).getPrincipal();

        return this.classroomService.getClassroomsByType(user.getRole(), courseExecutionId, type);
    }

}
