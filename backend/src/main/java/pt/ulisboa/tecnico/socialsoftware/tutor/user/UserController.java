package pt.ulisboa.tecnico.socialsoftware.tutor.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.security.Principal;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_NOT_FOUND;

@RestController
@Secured({ "ROLE_ADMIN", "ROLE_TEACHER" })
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public List<User> getUser(@RequestParam("page") int pageIndex, @RequestParam("size") int pageSize){
        return userRepository.findAll(PageRequest.of(pageIndex, pageSize)).getContent();
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("(hasRole('ROLE_STUDENT')) or hasRole('ROLE_TEACHER')")
    public User getUser(@PathVariable Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
    }

//    @PostMapping("/users")
//    public User createUser(@Valid @RequestBody UserDto user) {
//        return userService.createUser(user.getName(), user.getUsername(), user.getRole());
//    }

//    @PutMapping("/users/{userId}")
//    public User updateUser(@PathVariable Integer userId,
//                              @Valid @RequestBody UserDto user) {
//
//        return userRepository.findById(userId)
//                .map(usr -> userRepository.save(usr)).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
//    }

    @PutMapping("/users/dashboard/privacy")
    @PreAuthorize("(hasRole('ROLE_STUDENT'))")
    public UserDto changeDashboardPrivacy(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return userService.changeDashboardPrivacy(user.getId());
    }

    @GetMapping("/users/update")
    @PreAuthorize("(hasRole('ROLE_STUDENT'))")
    public UserDto getLoggedUser(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return userService.getLoggedUser(user.getId());
    }

    @GetMapping("/users/update/theme/{themeName}")
    @PreAuthorize("(hasRole('ROLE_STUDENT'))")
    public UserDto updateCurrentTheme(Principal principal, @PathVariable String themeName) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return userService.updateCurrentTheme(user.getId(), themeName);
    }


    @DeleteMapping("/users/{userId}")
    public ResponseEntity deleteUser(@PathVariable Integer userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    userRepository.delete(user);
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
    }
}