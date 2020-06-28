package pt.ulisboa.tecnico.socialsoftware.tutor.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.shop.dto.PostAwardItemDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.shop.dto.ThemeItemDto;
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

    @PutMapping("/users/dashboard/privacy")
    @PreAuthorize("(hasRole('ROLE_STUDENT'))")
    public UserDto changeDashboardPrivacy(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return userService.changeDashboardPrivacy(user.getId());
    }

    @GetMapping("/users/update")
    @PreAuthorize("(hasRole('ROLE_STUDENT')) or hasRole('ROLE_TEACHER')")
    public UserDto getLoggedUser(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return userService.getLoggedUser(user.getId());
    }

    @GetMapping("/users/currentTheme")
    @PreAuthorize("(hasRole('ROLE_STUDENT'))")
    public ThemeItemDto getCurrentTheme(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return userService.getCurrentTheme(user.getId());
    }

    @GetMapping("/users/themes")
    @PreAuthorize("(hasRole('ROLE_STUDENT'))")
    public List<ThemeItemDto> getUserThemes(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return userService.getUserThemes(user.getId());
    }

    @GetMapping("/users/themes/owned")
    @PreAuthorize("(hasRole('ROLE_STUDENT'))")
    public List<String> getOwnedThemes(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return userService.userOwnedThemes(user.getId());
    }

    @GetMapping("/users/update/theme/{themeName}")
    @PreAuthorize("(hasRole('ROLE_STUDENT'))")
    public UserDto updateCurrentTheme(Principal principal, @PathVariable String themeName) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return userService.updateCurrentTheme(user.getId(), themeName);
    }

    @GetMapping("/users/numberPowerUps/{type}")
    @PreAuthorize("(hasRole('ROLE_STUDENT'))")
    public Integer getNumberPowerUps(Principal principal, @PathVariable String type) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return userService.getTypeItemNumb(user.getId(), type);
    }


    @DeleteMapping("/users/{userId}")
    public ResponseEntity deleteUser(@PathVariable Integer userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    userRepository.delete(user);
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
    }

    @GetMapping("/users/updateAwards")
    @PreAuthorize("(hasRole('ROLE_STUDENT')) or hasRole('ROLE_TEACHER')")
    public List<PostAwardItemDto> getAwards(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return userService.getAwards(user.getId());
    }
}