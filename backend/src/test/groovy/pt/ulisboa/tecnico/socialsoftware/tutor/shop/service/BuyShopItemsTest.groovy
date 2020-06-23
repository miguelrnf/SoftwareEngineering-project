package pt.ulisboa.tecnico.socialsoftware.tutor.shop.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.shop.ShopService
import pt.ulisboa.tecnico.socialsoftware.tutor.shop.domain.ShopItem
import pt.ulisboa.tecnico.socialsoftware.tutor.shop.repository.ShopRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@DataJpaTest
class BuyShopItemsTest extends Specification {
    public static final int VALID_ID_1 = 1
    public static final int VALID_ID_2 = 2
    public static final int VALID_ID_3 = 3
    public static final String VALID_NAME = "Joe Mama"
    public static final String VALID_USERNAME = "Joe-Mama"
    public static final String VALID_NAME_1 = "Ben Dover"
    public static final String VALID_NAME_2 = "Mike Litoris"
    public static final String VALID_NAME_3 = "Peixe Acha"
    public static final String VALID_DESCRIPTION_1 = "We're no strangers to love"
    public static final String VALID_DESCRIPTION_2 = "You know the rules"
    public static final String VALID_DESCRIPTION_3 = "And so, do I"
    public static final String VALID_TYPE_1 = ShopItem.Type.THEME.toString()
    public static final String VALID_TYPE_2 = ShopItem.Type.POST_AWARD.toString()
    public static final String VALID_TYPE_3 = ShopItem.Type.POWER_UP.toString()
    public static final int VALID_PRICE_1 = 100
    public static final int VALID_PRICE_2 = 200
    public static final int VALID_PRICE_3 = 300
    public static final String VALID_CONTENT_THEME = "1,1,1,1,1,1,1"
    public static final String VALID_CONTENT_POST_AWARD = "TYPE_1"
    public static final String VALID_CONTENT_POWER_UP = "TYPE_1"

    @Autowired
    ShopService shopService

    @Autowired
    ShopRepository shopRepository

    @Autowired
    UserRepository userRepository

    @Shared
    def ITEM_1

    @Shared
    def ITEM_2

    @Shared
    def ITEM_3

    @Shared
    def USER_1

    def setupSpec() {
        ITEM_1 = new ShopItem()
        ITEM_1.setId(VALID_ID_1)
        ITEM_1.setType(VALID_TYPE_1)
        ITEM_1.setName(VALID_NAME_1)
        ITEM_1.setDescription(VALID_DESCRIPTION_1)
        ITEM_1.setPrice(VALID_PRICE_1)
        ITEM_1.setContent(VALID_CONTENT_THEME)

        ITEM_2 = new ShopItem()
        ITEM_2.setId(VALID_ID_2)
        ITEM_2.setType(VALID_TYPE_2)
        ITEM_2.setName(VALID_NAME_2)
        ITEM_2.setDescription(VALID_DESCRIPTION_2)
        ITEM_2.setPrice(VALID_PRICE_2)
        ITEM_2.setContent(VALID_CONTENT_POST_AWARD)

        ITEM_3 = new ShopItem()
        ITEM_3.setId(VALID_ID_3)
        ITEM_3.setType(VALID_TYPE_3)
        ITEM_3.setName(VALID_NAME_3)
        ITEM_3.setDescription(VALID_DESCRIPTION_3)
        ITEM_3.setPrice(VALID_PRICE_3)
        ITEM_3.setContent(VALID_CONTENT_POWER_UP)

        USER_1 = new User(VALID_NAME, VALID_USERNAME, 1, User.Role.STUDENT)
    }

    def setup() {
        given: "a number of shop items"
        def i1 = new ShopItem()
        i1.setId(VALID_ID_1)
        i1.setType(VALID_TYPE_1)
        i1.setName(VALID_NAME_1)
        i1.setDescription(VALID_DESCRIPTION_1)
        i1.setPrice(VALID_PRICE_1)
        i1.setContent(VALID_CONTENT_THEME)

        def i2 = new ShopItem()
        i2.setId(VALID_ID_2)
        i2.setType(VALID_TYPE_2)
        i2.setName(VALID_NAME_2)
        i2.setDescription(VALID_DESCRIPTION_2)
        i2.setPrice(VALID_PRICE_2)
        i2.setContent(VALID_CONTENT_POST_AWARD)

        def i3 = new ShopItem()
        i3.setId(VALID_ID_3)
        i3.setType(VALID_TYPE_3)
        i3.setName(VALID_NAME_3)
        i3.setDescription(VALID_DESCRIPTION_3)
        i3.setPrice(VALID_PRICE_3)
        i3.setContent(VALID_CONTENT_POWER_UP)

        and: "a user with enough score to buy some items"
        def user1 = new User(VALID_NAME, VALID_USERNAME, 1, User.Role.STUDENT)

        then: "add to repository"
        userRepository.save(user1)
        shopRepository.save(i1)
        shopRepository.save(i2)
        shopRepository.save(i3)
    }

    @Unroll
    def "buy each one of the items"() {
        when:
        def id
        if(item.getId() == 2)
            id = 5
        else if(item.getId() == 3)
            id = 9
        else id = 1
        USER_1.changeScore(300)

        def result = shopService.buyShopItem(USER_1 as User, id)

        then:
        result.getName() == expected

        where:
        item                    ||       expected
        ITEM_1 as ShopItem      ||       VALID_NAME_1
        ITEM_2 as ShopItem      ||       VALID_NAME_2
        ITEM_3 as ShopItem      ||       VALID_NAME_3
    }

    def "buy all of the items when there is not enough achandos"() {
        when:
        USER_1.changeScore(100)

        shopService.buyShopItem(USER_1 as User, 10)
        shopService.buyShopItem(USER_1 as User, 11)
        shopService.buyShopItem(USER_1 as User, 12)

        then:
        def result = thrown(TutorException)
        result.message == ErrorMessage.NOT_ENOUGH_ACHANDOS.label

    }

    @TestConfiguration
    static class ShopServiceImplTestContextConfiguration {
        @Bean
        ShopService shopService() {
            return new ShopService()
        }
    }
}
