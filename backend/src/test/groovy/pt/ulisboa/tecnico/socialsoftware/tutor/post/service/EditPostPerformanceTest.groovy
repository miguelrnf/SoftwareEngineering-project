package pt.ulisboa.tecnico.socialsoftware.tutor.post.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.post.PostService
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.Post
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.PostQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostDto
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.post.repository.PostRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification

@DataJpaTest
class EditPostPerformanceTest extends Specification {

    @Autowired
    PostService postService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    PostRepository postRepository

    def "performance testing with 3000 post edits"() {
        given: "a valid question"
        def question = new Question()
        question.setKey(11133)
        question.setContent("VALID_QUESTION")
        question.setStatus(Question.Status.AVAILABLE)
        question.setNumberOfAnswers(2)
        question.setNumberOfCorrect(1)
        questionRepository.save(question)

        and: "two valid users"
        def user1 = new User("VALID_NAME_1", "VALID_USERNAME_1", 1, User.Role.STUDENT)
        userRepository.save(user1)

        and: "valid posts"
        for(int i = 0; i <= 3000; i++) {
            def postQuestion1 = new PostQuestion()
            postQuestion1.setQuestion(question)
            postQuestion1.setUser(user1)
            user1.addPostQuestion(postQuestion1)
            postQuestion1.setStudentQuestion("VALID_STUDENT_QUESTION")
            def post1 = new Post(i, postQuestion1)
            postQuestion1.setPost(post1)
            postRepository.save(post1)
        }
        def pqDto = new PostQuestionDto()
        pqDto.setUser(new UserDto(user1))
        def postDto = new PostDto()
        postDto.setQuestion(pqDto)
        pqDto.setPost(postDto)

        and: "a valid edit"
        def postQuestion2 = new PostQuestionDto()
        postQuestion2.setQuestion(new QuestionDto(question))
        postQuestion2.setUser(new UserDto(user1))
        postQuestion2.setStudentQuestion("VALID_STUDENT_QUESTION")

        when: "2000 posts get edited"
        for(int i = 0; i <= 3000; i++) {
            postDto.setKey(i)
            postQuestion2.setPost(postDto)
            postService.editPost(postQuestion2)
        }
        
        then:
        true
    }

    @TestConfiguration
    static class PostServiceImplTestContextConfiguration {
        @Bean
        PostService postService() {
            return new PostService()
        }
    }
}
