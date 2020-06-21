package pt.ulisboa.tecnico.socialsoftware.tutor.post.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification

@DataJpaTest
class ChangeDiscussStatusPerformanceTest extends Specification {

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
        question.setKey(123412)
        question.setContent("VALID_QUESTION")
        question.setStatus(Question.Status.AVAILABLE)
        question.setTitle('title')
        question.setNumberOfAnswers(2)
        question.setNumberOfCorrect(1)
        questionRepository.save(question)


        and: "three valid users"
        def user1 = new User("VALID_NAME_1", "VALID_USERNAME_1", 1, User.Role.STUDENT)
        def user2 = new User("VALID_NAME_2", "VALID_USERNAME_2", 2, User.Role.TEACHER)
        userRepository.save(user1)
        userRepository.save(user2)

        and: "valid posts"
        for(int i = 1; i <= 1; i++) {
            def postQuestion1 = new PostQuestion()
            def postAnswer1 = new PostAnswer()
            postAnswer1.setUser(user2)
            postAnswer1.setTeacherAnswer("VALID_ANSWER")
            postQuestion1.setQuestion(question)
            postQuestion1.setUser(user1)
            user1.addPostQuestion(postQuestion1)
            postQuestion1.setStudentQuestion("VALID_STUDENT_QUESTION")
            def post1 = new Post(i, postQuestion1)
            postQuestion1.setPost(post1)
            postAnswer1.setPost(post1)
            post1.setAnswer(postAnswer1)
            postRepository.save(post1)
        }
        def pqDto = new PostQuestionDto()
        pqDto.setUser(new UserDto(user1))
        def postDto = new PostDto()
        postDto.setQuestion(pqDto)
        pqDto.setPost(postDto)
        def paDto = new PostAnswerDto()
        paDto.setPost(postDto)
        postDto.setAnswer(paDto)

        and: "a valid pq"
        def postQuestion2 = new PostQuestionDto()
        postQuestion2.setQuestion(new QuestionDto(question))
        postQuestion2.setUser(new UserDto(user1))
        postQuestion2.setStudentQuestion("VALID_STUDENT_QUESTION")

        and: "a valid pa"
        def postAnswer2 = new PostAnswerDto()
        postAnswer2.setUser(new UserDto(user2))
        postAnswer2.setTeacherAnswer("VALID_ANSWER")

        when: "3000 posts get discuss status changed"
        for(int i = 1; i <= 2; i++) {
            postDto.setKey(i)
            postQuestion2.setPost(postDto)
            postAnswer2.setPost(postDto)
            postService.changeDiscussStatus(1, user1)
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

