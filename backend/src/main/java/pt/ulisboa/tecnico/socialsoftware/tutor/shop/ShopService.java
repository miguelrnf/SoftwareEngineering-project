package pt.ulisboa.tecnico.socialsoftware.tutor.shop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.shop.domain.PostAwardItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.shop.domain.PowerUpItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.shop.domain.ShopItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.shop.domain.ThemeItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.shop.dto.ShopItemDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.shop.repository.ShopRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USERNAME_NOT_FOUND;

@Service
public class ShopService {
    @Autowired
    private ShopRepository shopRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<ShopItemDto> listShopItems() {
        return shopRepository.findAll().stream().map(ShopItemDto::new).collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ShopItemDto buyShopItem(String username, int shopItemId) {
        ShopItem item = checkIfShopItemExistsById(shopItemId);
        User u = findUsername(username);
        if(u.getRole() != User.Role.ADMIN) checkIfUserHasEnoughAchandos(u, item.getPrice());

        addItemToUser(u, item);
      
        return new ShopItemDto(item);
    }

    private User findUsername(String username){
        User user = userRepository.findByUsername(username);

        if(user == null)
            throw new TutorException(USERNAME_NOT_FOUND, username);

        return user;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ShopItemDto addShopItem(ShopItemDto shopItemDto) {
        try {
            checkIfShopItemExistsByName(shopItemDto);
        } catch (TutorException e) {
            if(!shopItemDto.getType().equals("THEME")) throw new TutorException(ErrorMessage.CANT_ADD_ITEM);
            checkConsistency(shopItemDto);
            ShopItem item = new ShopItem(shopItemDto.getName(), shopItemDto.getType(), shopItemDto.getPrice(),
                    shopItemDto.getDescription(), shopItemDto.getIcon(), shopItemDto.getColor(), shopItemDto.getContent());
            entityManager.persist(item);
            return new ShopItemDto(item);
        }
        throw new TutorException(ErrorMessage.ITEM_ALREADY_EXISTS, shopItemDto.getName());
    }

    private void checkConsistency(ShopItemDto item) {
        if (item.getName() == null || item.getName().isBlank() ||
                item.getName().isEmpty() || item.getName().length() > 50)
            throw new TutorException(ErrorMessage.ITEM_NOT_CONSISTENT, "Name");

        if (item.getDescription() == null || item.getDescription().isBlank() ||
                item.getDescription().isEmpty() || item.getDescription().length() > 250)
            throw new TutorException(ErrorMessage.ITEM_NOT_CONSISTENT, "Description");

        if (item.getPrice() < 1)
            throw new TutorException(ErrorMessage.ITEM_NOT_CONSISTENT, "Price");
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
        else user.changeScore(-price);
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

    private void addItemToUser(User user, ShopItem item) {
        if(item.getType() == ShopItem.Type.THEME){
            if (hasTheme(user, item))
                return;
            user.addItem(new ThemeItem(item, user));
        }
        else if(item.getType() == ShopItem.Type.POWER_UP)
            user.addItem(new PowerUpItem(item, user));
        else if(item.getType() == ShopItem.Type.POST_AWARD)
            user.addItem(new PostAwardItem(item, user));
        else throw new TutorException(ErrorMessage.INVALID_ITEM_TYPE);
    }

    private boolean hasTheme(User user, ShopItem item){
        return user.getItems().stream().anyMatch(userItem -> userItem.getName().equals(item.getName()));
    }
}
