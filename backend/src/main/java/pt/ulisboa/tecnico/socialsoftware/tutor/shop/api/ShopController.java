package pt.ulisboa.tecnico.socialsoftware.tutor.shop.api;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.shop.ShopService;
import pt.ulisboa.tecnico.socialsoftware.tutor.shop.dto.ShopItemDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;

@RestController
public class ShopController {
    private ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @GetMapping("shop")
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_ADMIN')")
    public List<ShopItemDto> listShopItems() {
        return shopService.listShopItems();
    }

    @PutMapping("shop/buy/{itemId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_ADMIN')")
    public ShopItemDto buyShopItem(Principal principal, @PathVariable int itemId) {
        User user = (User) ((Authentication) principal).getPrincipal();
        if(user == null)
            throw new TutorException(AUTHENTICATION_ERROR);
        return shopService.buyShopItem(user.getUsername(), itemId);
    }

    // TODO - REMOVE DEMO ADMIN

    @PostMapping("shop/add")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('DEMO_ADMIN')")
    public ShopItemDto addShopItem(@Valid @RequestBody ShopItemDto shopItem) {
        return shopService.addShopItem(shopItem);
    }

    @DeleteMapping("shop/delete/{itemId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('DEMO_ADMIN')")
    public ShopItemDto deleteShopItem(@PathVariable int itemId) {
        return shopService.deleteShopItem(itemId);
    }
}
