package pt.ulisboa.tecnico.socialsoftware.tutor.shop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.shop.domain.ShopItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.shop.dto.ShopItemDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.shop.dto.ShopItemListDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.shop.repository.ShopRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShopService {
    @Autowired
    private ShopRepository shopRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ShopItemListDto listShopItems() {
        ShopItemListDto dto = new ShopItemListDto();
        dto.setItemList(shopRepository.findAll().stream().map(ShopItemDto::new).collect(Collectors.toList()));
        return dto;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ShopItemDto buyShopItem(User user, int shopItemId) {
        ShopItem item = checkIfShopItemExistsById(shopItemId);
        checkIfUserHasEnoughAchandos(user, item.getPrice());
        return new ShopItemDto(item);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ShopItemDto addShopItem(ShopItemDto shopItemDto) {
        try {
            checkIfShopItemExistsByName(shopItemDto);
        } catch (TutorException e) {
            if(checkIfItemIsPowerUp(shopItemDto.getType())) throw new TutorException(ErrorMessage.CANT_ADD_POWER_UP);
            ShopItem item = new ShopItem(shopItemDto.getName(), shopItemDto.getType(), shopItemDto.getPrice(),
                    shopItemDto.getDescription(), shopItemDto.getIcon(), shopItemDto.getColor());
            entityManager.persist(item);
            return new ShopItemDto(item);
        }
        throw new TutorException(ErrorMessage.ITEM_ALREADY_EXISTS, shopItemDto.getName());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ShopItemDto deleteShopItem(int shopItemId) {
        ShopItem item = checkIfShopItemExistsById(shopItemId);
        if(checkIfItemIsPowerUp(item.getType().toString())) throw new TutorException(ErrorMessage.CANT_REMOVE_POWER_UP);

        entityManager.remove(item);
        return new ShopItemDto(item);
    }

    private void checkIfShopItemExistsByName(ShopItemDto item) {
        if(shopRepository.findShopItemByName(item.getName()).isEmpty())
            throw new TutorException(ErrorMessage.NON_EXISTING_ITEM_NAME, item.getName());
    }

    private void checkIfUserHasEnoughAchandos(User user, int price) {
        if(user.getScore() - price < 0) throw new TutorException(ErrorMessage.NOT_ENOUGH_ACHANDOS);
        // to remove achandos we multiply by -1
        else user.changeScore(price * -1);
    }

    private ShopItem checkIfShopItemExistsById(int id) {
        Optional<ShopItem> item = shopRepository.findById(id);
        if(item.isEmpty())
            throw new TutorException(ErrorMessage.NON_EXISTING_ITEM_ID, id);
        else return item.get();
    }

    private boolean checkIfItemIsPowerUp(String type) {
        return type.equals("POWER_UP");
    }
}
