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
import pt.ulisboa.tecnico.socialsoftware.tutor.shop.ShopService
import pt.ulisboa.tecnico.socialsoftware.tutor.shop.domain.ShopItem
import pt.ulisboa.tecnico.socialsoftware.tutor.shop.repository.ShopRepository
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
    public static final String VALID_TYPE_3 = ShopItem.Type.POWER_UP.toString()
    public static final int VALID_PRICE_1 = 100
    public static final int VALID_PRICE_2 = 200
    public static final int VALID_PRICE_3 = 300

    @Autowired
    ShopService shopService

    @Autowired
    ShopRepository shopRepository

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
        i2.setPrice(VALID_PRICE_2)

        def i3 = new ShopItem()
        i3.setId(VALID_ID_3)
        i3.setType(VALID_TYPE_3)
        i3.setName(VALID_NAME_3)
        i3.setDescription(VALID_DESCRIPTION_3)
        i3.setPrice(VALID_PRICE_3)

        then: "add to repository"
        shopRepository.save(i1)
        shopRepository.save(i2)
        shopRepository.save(i3)
    }

    def "list items available to buy"() {
        when:
        def result = shopService.listShopItems()

        then:
        result.getItemList().size() == 3
    }

    @TestConfiguration
    static class ShopServiceImplTestContextConfiguration {
        @Bean
        ShopService shopService() {
            return new ShopService()
        }
    }
}
