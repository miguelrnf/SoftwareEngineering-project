package pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.SuggestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.domain.Suggestion
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.dto.SuggestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.repository.SuggestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification

import java.time.LocalDateTime

@DataJpaTest
class SuggestionDashboardPerfomanceTest extends Specification{

    @Autowired
    SuggestionService suggestionService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    SuggestionRepository suggestionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    TopicRepository topicRepository

    def "testing listing 3000 times 3000 suggestions"() {

        given: "a course"
        def course = new Course("COURSE_NAME", Course.Type.TECNICO)
        def courseExecution = new CourseExecution(course, "ACRONYM", "ACADEMIC_TERM", Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        and: "a user with the role teacher"
        def userT = new User("VALID_NAME1", "VALID_USERNAME_TEACHER", 2, User.Role.TEACHER)

        and: "a user with the role student"
        def userS = new User("VALID_NAME", "VALID_USERNAME", 1, User.Role.STUDENT)

        and: "a user with the role student that didn't create that suggestion"
        def userS2 = new User("VALID_NAME2", "VALID_USERNAME2", 3, User.Role.STUDENT)

        and: "a valid topicDto"
        def topicDto = new TopicDto()
        topicDto.setId(1)
        topicDto.setName("VALID_NAME_TOPIC")

        and: "a valid topic"
        def topic = new Topic(course, topicDto);

        and: "a valid list of topics"
        def topicList = new HashSet<Topic>();
        topicList.add(topic)

        and: "3000 valid suggestions"
        for (int i = 1; i <= 3000; i++) {
            def suggestion = new Suggestion()
            suggestion.setKey(i)
            suggestion.set_student(userS)
            suggestion.set_questionStr("SUGGESTION_CONTENT")
            suggestion.setCreationDate(LocalDateTime.now())
            suggestion.set_topicsList(topicList)
            suggestion.setStatus(Suggestion.Status.TOAPPROVE)
            suggestionRepository.save(suggestion)
        }

        and: "add to repository"
        courseRepository.save(course)
        courseExecutionRepository.save(courseExecution)
        userRepository.save(userS)
        userRepository.save(userS2)
        userRepository.save(userT)
        topicRepository.save(topic)


        def userSDto = new UserDto()
        userSDto.setUsername("VALID_USERNAME")
        userSDto.setRole('STUDENT' as User.Role)
        userSDto.setName("VALID_NAME")
        userSDto.setId(1)

        def sugDto = new SuggestionDto()
        sugDto.set_student(userSDto)
        sugDto.setStatus("TOAPPROVE")
        sugDto.set_justification("JUSTIFICATION")

        when: "Listing 3000 times all 3000 suggestions"
        for (int i = 1; i <= 3000; i++) {
            sugDto.setKey(i)
            suggestionService.listAllSuggestions(userSDto)

        }

        then:
        true

    }


    @TestConfiguration
    static class SuggestionServiceImplTestContextConfiguration {

        @Bean
        SuggestionService suggestionService() {
            return new SuggestionService()
        }
    }
}