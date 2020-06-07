package pt.ulisboa.tecnico.socialsoftware.tutor.shop.dto;

import java.io.Serializable;
import java.util.List;

public class ShopItemListDto implements Serializable {
    private List<ShopItemDto> itemList;

    public ShopItemListDto() {
    }

    public ShopItemListDto(List<ShopItemDto> itemList) {
        this.itemList = itemList;
    }

    public List<ShopItemDto> getItemList() {
        return itemList;
    }

    public void setItemList(List<ShopItemDto> itemList) {
        this.itemList = itemList;
    }
}
