package pt.ulisboa.tecnico.socialsoftware.tutor.post.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.post.PostService
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.Post
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.PostComment
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.PostQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.post.repository.PostCommentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.post.repository.PostRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime

@DataJpaTest
class SearchCommentPerformanceTest extends Specification {

    @Autowired
    PostService postService

    @Autowired
    PostRepository postRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    PostCommentRepository commentRepository

    def "testing performance when 3000 searches are called on different posts"() {
        given: "a valid question"
        def question = new Question()
        question.setKey(45345)
        question.setContent("VALID_QUESTION")
        question.setStatus(Question.Status.AVAILABLE)
        question.setTitle('title')
        question.setNumberOfAnswers(2)
        question.setNumberOfCorrect(1)
        questionRepository.save(question)

        and: "two valid users"
        def user1 = new User("VALID_NAME_1", "VALID_USERNAME_1", 1, User.Role.STUDENT)
        userRepository.save(user1)

        and: "valid posts"
        for(int i = 1; i <= 1; i++) {
            def postQuestion1 = new PostQuestion()
            postQuestion1.setQuestion(question)
            postQuestion1.setUser(user1)
            postQuestion1.setStudentQuestion("VALID_STUDENT_QUESTION")
            def post1 = new Post(i, postQuestion1)
            postQuestion1.setPost(post1)
            user1.addPostQuestion(postQuestion1)
            postRepository.save(post1)
            and: "a valid comment"
            def comment = new PostComment()
            comment.setKey(i)
            comment.setComment("VALID_COMMENT")
            comment.setPost(post1)
            comment.setUser(user1)
            comment.setCreationDate(LocalDateTime.now())
            post1.addComment(comment)
            commentRepository.save(comment)
        }

        when: "3000 searches are called"
        for(int i = 1; i <= 2; i++) {
            postService.searchComment("VALID_COMMENT")
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
