package pt.ulisboa.tecnico.socialsoftware.tutor.shop.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.post.PostService
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.Post
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.PostAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.PostQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostDto
import pt.ulisboa.tecnico.socialsoftware.tutor.post.repository.PostRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.shop.domain.ShopItem
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@DataJpaTest
class ListShopItemsTest extends Specification {
    public static final int VALID_ID_1 = 1
    public static final int VALID_ID_2 = 2
    public static final int VALID_ID_3 = 3
    public static final String VALID_NAME_1 = "Ben Dover"
    public static final String VALID_NAME_2 = "Mike Litoris"
    public static final String VALID_NAME_3 = "Peixe Acha"
    public static final String VALID_DESCRIPTION_1 = "We're no strangers to love"
    public static final String VALID_DESCRIPTION_2 = "You know the rules"
    public static final String VALID_DESCRIPTION_3 = "And so, do I"
    public static final String VALID_TYPE_1 = ShopItem.Type.PRIMARY_COLOR.toString()
    public static final String VALID_TYPE_2 = ShopItem.Type.POST_AWARD.toString()
    public static final String VALID_TYPE_3 = "sum ting wong"
    public static final int VALID_PRICE_1 = 100
    public static final int VALID_PRICE_2 = 200
    public static final int VALID_PRICE_3 = 300

    @Autowired
    PostService postService

    @Autowired
    PostRepository postRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    UserRepository userRepository


    def setup() {
        given: "a number of shop items"
        def i1 = new ShopItem()
        i1.setId(VALID_ID_1)
        i1.setType(VALID_TYPE_1)
        i1.setName(VALID_NAME_1)
        i1.setDescription(VALID_DESCRIPTION_1)
        i1.setPrice(VALID_PRICE_1)

        def i2 = new ShopItem()
        i2.setId(VALID_ID_2)
        i2.setType(VALID_TYPE_2)
        i2.setName(VALID_NAME_2)
        i2.setDescription(VALID_DESCRIPTION_2)
        i1.setPrice(VALID_PRICE_1)

        then: "add to repository"
        userRepository.save(user1)
        userRepository.save(user2)
        userRepository.save(user3)
        questionRepository.save(question)
        postRepository.save(postNo)
        postRepository.save(postAnswered)

    }


    @Unroll
    def "valid change answer privacy"() {
        when:
        def dto = new PostDto(post)
        dto.getQuestion().setUser(new UserDto(user))
        def result = postService.changeAnswerPrivacy(expected.getKey(), user)

        then:
        result.getAnswerPrivacy() != expected.getAnswerPrivacy()

        where:
        user                         | post                    ||       expected
        VALID_U_TEACHER as User      | VALID_P as Post         ||       VALID_P as Post


    }

    @Unroll
    def "invalid change answer privacy"() {
        when:
        def dto = new PostDto(post)
        dto.getQuestion().setUser(new UserDto(user))
        postService.changeAnswerPrivacy(VALID_KEY, user)

        then:
        def result = thrown(TutorException)
        result.message == expected

        where:
        user                            | post                       || expected
        INVALID_U_NOT_OWNER as User     | VALID_P as Post            || ErrorMessage.USER_HAS_WRONG_ROLE.label




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
