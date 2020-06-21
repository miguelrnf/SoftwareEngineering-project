package pt.ulisboa.tecnico.socialsoftware.tutor.post.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.post.PostService
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.Post
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.PostAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.PostQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostDto
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.post.repository.PostRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification

@DataJpaTest
class EditAnswerPerformanceTest extends Specification {

    @Autowired
    PostService postService

    @Autowired
    PostRepository postRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    UserRepository userRepository

    def "testing performance when editing the answer of 3000 posts"() {
        given: "a valid question"
        def question = new Question()
        question.setKey(12333)
        question.setContent("VALID_QUESTION")
        question.setStatus(Question.Status.AVAILABLE)
        question.setTitle('title')
        question.setNumberOfAnswers(2)
        question.setNumberOfCorrect(1)
        questionRepository.save(question)

        and: "two valid users"
        def user1 = new User("VALID_NAME_1", "VALID_USERNAME_1", 1, User.Role.TEACHER)
        def user2 = new User("VALID_NAME_2", "VALID_USERNAME_2", 2, User.Role.STUDENT)
        userRepository.save(user1)
        userRepository.save(user2)

        and: "valid posts"
        for(int i = 1; i <= 1; i++) {
            def postQuestion1 = new PostQuestion()
            postQuestion1.setQuestion(question)
            postQuestion1.setUser(user2)
            postQuestion1.setStudentQuestion("VALID_STUDENT_QUESTION")
            def postAnswer1 = new PostAnswer()
            postAnswer1.setUser(user1)
            postAnswer1.setTeacherAnswer("VALID_ANSWER")
            def post1 = new Post(i, postQuestion1)
            postQuestion1.setPost(post1)
            post1.setAnswer(postAnswer1)
            postAnswer1.setPost(post1)
            user2.addPostQuestion(postQuestion1)
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

        when: "editing the answer of 3000 posts"
        for(int i = 1; i <= 2; i++) {
            postADto.getPost().setKey(i)
            postService.editAnswer(postADto)
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

        @Bean
        QuizService quizService() {
            return new QuizService()
        }

        @Bean
        AnswerService answerService() {
            return new AnswerService()
        }

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }

        @Bean
        AnswersXmlImport xmlImporter() {
            return new AnswersXmlImport()
        }
    }
}
