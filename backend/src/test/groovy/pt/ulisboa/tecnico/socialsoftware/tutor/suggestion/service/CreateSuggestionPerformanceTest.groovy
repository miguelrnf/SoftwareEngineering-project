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
import spock.lang.Specification

//Testing

@DataJpaTest
class CreateSuggestionPerformanceTest extends Specification{
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

    def "testing performance when creating 3000 suggestions"() {
        given: "a valid course"

        def course = new Course("Course", Course.Type.TECNICO)
        def courseExecution = new CourseExecution(course, "CS", "1", Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        and: "a user with the role teacher"
        def userT = new User("VALID_NAME", "VALID_USERNAME_TEACHER", 2, User.Role.TEACHER)

        and: "a user with the role student"
        def userS = new User("VALID_NAME", "VALID_USERNAME", 1, User.Role.STUDENT)

        and: "a valid topicDto"
        def topicDto = new TopicDto()
        topicDto.setId(1)
        topicDto.setName("VALID_NAME_TOPIC")

        and: "a valid topic"
        def topic = new Topic(course, topicDto)


        and: "a valid list of topics"
        def topicList = new HashSet<Topic>();
        topicList.add(topic)

        and: "add to repository"
        courseRepository.save(course)
        courseExecutionRepository.save(courseExecution)
        userRepository.save(userS)
        userRepository.save(userT)
        topicRepository.save(topic)

        when: "create 3000 suggestions"
        for (int i = 1; i <= 10; i++) {
            def sug = new Suggestion()
            sug.set_student(userS)
            sug.set_topicsList(topicList)
            def sugDto = new SuggestionDto(sug)
            sugDto.set_questionStr("question")
            suggestionService.createSuggestion(course.getId(), sugDto)
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