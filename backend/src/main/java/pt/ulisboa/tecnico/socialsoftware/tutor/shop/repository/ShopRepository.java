package pt.ulisboa.tecnico.socialsoftware.tutor.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pt.ulisboa.tecnico.socialsoftware.tutor.shop.domain.ShopItem;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public interface ShopRepository extends JpaRepository<ShopItem, Integer> {
    @Query(value = "SELECT * FROM shop_items WHERE item_name = :name", nativeQuery = true)
    Optional<ShopItem> findShopItemByName(String name);
}

