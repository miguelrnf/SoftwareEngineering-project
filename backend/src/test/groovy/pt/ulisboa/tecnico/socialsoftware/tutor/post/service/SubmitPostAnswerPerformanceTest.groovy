package pt.ulisboa.tecnico.socialsoftware.tutor.post.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.post.PostService
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.Post
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.PostQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostDto
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.post.repository.PostRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification

@DataJpaTest
class SubmitPostAnswerPerformanceTest extends Specification {
    @Autowired
    PostService postService

    @Autowired
    PostRepository postRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    QuestionRepository questionRepository

    def "testing performance when submitting 3000 post answers"() {
        given: "a valid question"
        def question = new Question()
        question.setKey(43412)
        question.setContent("VALID_QUESTION")
        question.setStatus(Question.Status.AVAILABLE)
        question.setNumberOfAnswers(2)
        question.setNumberOfCorrect(1)
        questionRepository.save(question)

        and: "two valid users"
        def user1 = new User("VALID_NAME_TEACHER", "VALID_USERNAME_TEACHER", 1, User.Role.TEACHER)
        def user2 = new User("VALID_NAME", "VALID_USERNAME", 2, User.Role.STUDENT)
        userRepository.save(user1)
        userRepository.save(user2)

        and: "valid posts"
        for(int i = 1; i <= 3000; i++) {
            def postQuestion1 = new PostQuestion()
            postQuestion1.setQuestion(question)
            postQuestion1.setUser(user1)
            postQuestion1.setStudentQuestion("VALID_STUDENT_QUESTION")
            def post1 = new Post(i, postQuestion1)
            postQuestion1.setPost(post1)
            user1.addPostQuestion(postQuestion1)
            postRepository.save(post1)
        }

        def pqDto = new PostQuestionDto()
        pqDto.setUser(new UserDto(user2))
        def postDto = new PostDto()
        postDto.setQuestion(pqDto)
        pqDto.setPost(postDto)
        def postADto = new PostAnswerDto()
        postADto.setTeacherAnswer("ANSWER_ANSWER")
        postADto.setUser(new UserDto(user1))
        postADto.setPost(postDto)

        when: "answering 3000 posts"
        for(int i = 1; i <= 3000; i++) {
            postADto.getPost().setKey(i)
            postService.answerQuestion(postADto)
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
