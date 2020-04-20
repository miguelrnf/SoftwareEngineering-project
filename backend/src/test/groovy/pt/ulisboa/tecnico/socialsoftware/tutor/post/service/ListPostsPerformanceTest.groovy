package pt.ulisboa.tecnico.socialsoftware.tutor.post.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.post.PostService
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.ListPostsDto
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
class ListPostsPerformanceTest extends Specification {

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

    def "performance testing with 5000 post submissions"() {
        given: "a question"
        def question = new Question()
        question.setKey(123)
        question.setContent("VALID_QUESTION")
        question.setStatus(Question.Status.AVAILABLE)
        question.setNumberOfAnswers(2)
        question.setNumberOfCorrect(1)

        and: "a quiz"
        def quiz = new Quiz()
        def quizQuestion = new QuizQuestion()
        quizQuestion.setQuestion(question)
        quiz.setKey(123123)
        quiz.addQuizQuestion(quizQuestion)

        and: "a quiz answer"
        def user = new User("VALID_NAME", "VALID_USERNAME", 1, User.Role.STUDENT)
        def quizAnswer = new QuizAnswer(user, quiz)
        def questionAnswer = new QuestionAnswer()
        questionAnswer.setQuizQuestion()
        quizAnswer.addQuestionAnswer()

        and: "a user that answered the question"
        user.addQuizAnswer(quizAnswer)

        and: "a post question"
        def pq = new PostQuestionDto()
        pq.setUser(new UserDto(user))
        pq.setStudentQuestion("QUESTION")
        pq.setQuestion(new QuestionDto(question))

        and: "page info"
        def list = new ListPostsDto();

        and: "added to repository"
        questionRepository.save(question)
        quizRepository.save(quiz)
        userRepository.save(user)


        and: "100 posts get submitted"
        1.upto(100, {
            postService.submitPost(11, pq)
        })

        when: "100 posts get listed"
        list.setTotalPosts(100)
        list.setPage(1)
        list.setPerPage(15)
        postService.postPagination(list)
        
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
