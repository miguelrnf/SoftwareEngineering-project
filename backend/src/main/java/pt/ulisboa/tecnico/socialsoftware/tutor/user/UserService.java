package pt.ulisboa.tecnico.socialsoftware.tutor.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.Demo;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.UsersXmlExport;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.UsersXmlImport;
import pt.ulisboa.tecnico.socialsoftware.tutor.shop.ShopService;
import pt.ulisboa.tecnico.socialsoftware.tutor.shop.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.shop.dto.PostAwardItemDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.shop.dto.ThemeItemDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.shop.repository.ShopRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private ShopService shopService;

    @Autowired
    private ShopRepository shopRepository;

    public User findByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    public User findByKey(Integer key) {
        return this.userRepository.findByKey(key);
    }

    public Integer getMaxUserNumber() {
        Integer result = userRepository.getMaxUserNumber();
        return result != null ? result : 0;
    }

    public User createUser(String name, String username, User.Role role) {

        if (findByUsername(username) != null) {
            throw new TutorException(DUPLICATE_USER, username);
        }

        User user = new User(name, username, getMaxUserNumber() + 1, role);
        userRepository.save(user);
        return user;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String getEnrolledCoursesAcronyms(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        return user.getEnrolledCoursesAcronyms();
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<CourseDto> getCourseExecutions(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        return user.getCourseExecutions().stream().map(CourseDto::new).collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void addCourseExecution(int userId, int executionId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));

        user.addCourse(courseExecution);
        courseExecution.addUser(user);
    }

    public String exportUsers() {
        UsersXmlExport xmlExporter = new UsersXmlExport();

       return xmlExporter.export(userRepository.findAll());
    }


    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void importUsers(String usersXML) {
        UsersXmlImport xmlImporter = new UsersXmlImport();

        xmlImporter.importUsers(usersXML, this);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public User getDemoTeacher() {
        User user = this.userRepository.findByUsername(Demo.TEACHER_USERNAME);
        if (user == null)
            return createUser("Demo Teacher", Demo.TEACHER_USERNAME, User.Role.TEACHER);
        return user;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public User getDemoStudent() {
        User user = this.userRepository.findByUsername(Demo.STUDENT_USERNAME);
        if (user == null)
            return createUser("Demo Student", Demo.STUDENT_USERNAME, User.Role.STUDENT);
        return user;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public User getDemoAdmin() {
        User user =  this.userRepository.findByUsername(Demo.ADMIN_USERNAME);
        if (user == null)
            return createUser("Demo Admin", Demo.ADMIN_USERNAME, User.Role.DEMO_ADMIN);
        return user;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public User createDemoStudent() {
        String birthDate = DateHandler.now().toString();
        User newDemoUser = createUser("Demo-Student-" + birthDate, "Demo-Student-" + birthDate, User.Role.STUDENT);

        User demoUser = this.userRepository.findByUsername(Demo.STUDENT_USERNAME);

        CourseExecution courseExecution = demoUser.getCourseExecutions().stream().findAny().orElse(null);

        if (courseExecution != null) {
            courseExecution.addUser(newDemoUser);
            newDemoUser.addCourse(courseExecution);
        }

        return newDemoUser;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public UserDto changeDashboardPrivacy(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        user.changeDashboardPrivacy();
        return new UserDto(user);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ThemeItemDto getCurrentTheme(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        List<UserItem> temp = searchTheme(user);


        if (temp.isEmpty()){
            ShopItem defaultTheme = shopRepository.findShopItemByName("Default Light").orElseThrow(() -> new TutorException(DEFAULT_THEME_MISSING));
            shopService.buyShopItem(user.getUsername(),defaultTheme.getId());
            temp = searchTheme(user);
        }


        if (temp.size() > 1 || !(temp.get(0) instanceof ThemeItem))
            throw new TutorException(INVALID_THEME);


        return new ThemeItemDto((ThemeItem) temp.get(0));
    }

    private List<UserItem> searchTheme(User user){
        return user.getItems().stream().filter(userItem -> userItem.getName().equals(user.getCurrentTheme())).collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<String> userOwnedThemes(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        return user.getItems().stream().filter(userItem -> userItem instanceof ThemeItem).collect(Collectors.toList())
                .stream().map(UserItem::getName).collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public UserDto getLoggedUser(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        return new UserDto(user);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public UserDto updateCurrentTheme(int userId, String theme) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        user.setCurrentTheme(theme);
        return new UserDto(user);
    }

    private List<UserItem> userPowerUps(User user, String type) {

        return user.getItems().stream().filter(userItem -> {
            if(userItem instanceof PowerUpItem)
                    return ((PowerUpItem) userItem).getType() == PowerUpItem.Type.valueOf(type);
            return false;
        }).collect(Collectors.toList());
    }


    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<ThemeItemDto> getUserThemes(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        List<UserItem> temp = user.getItems().stream().filter(userItem -> userItem instanceof ThemeItem).collect(Collectors.toList());
        return temp.stream().map(userItem -> new ThemeItemDto((ThemeItem) userItem)).collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Integer getTypeItemNumb(int userId, String type) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        return userPowerUps(user, type).size();
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void usePowerUp(int userId, String type) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        if (userPowerUps(user, type).isEmpty()){
            throw new TutorException(INVALID_POWER_UP);
        }
        else
            user.getItems().stream().filter(userItem -> ((PowerUpItem) userItem).getType() == PowerUpItem.Type
                    .valueOf(type)).collect(Collectors.toList()).remove(0);
    }


    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<PostAwardItemDto> getAwards(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        return user.getItems().stream().filter(x -> x instanceof PostAwardItem)
                .map(x -> new PostAwardItemDto((PostAwardItem) x)).collect(Collectors.toList());

    }
}
