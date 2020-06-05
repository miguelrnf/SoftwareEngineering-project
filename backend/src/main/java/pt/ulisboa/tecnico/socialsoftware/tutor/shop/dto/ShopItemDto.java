package pt.ulisboa.tecnico.socialsoftware.tutor.shop.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.shop.domain.ShopItem;

import java.io.Serializable;

public class ShopItemDto implements Serializable {
    private int id;
    private String name;
    private String type;
    private int price;
    private String description;
    private String icon;
    private String color;
    private String content;

    public ShopItemDto() {
    }

    public ShopItemDto(ShopItem item) {
        this.id = item.getId();
        this.name = item.getName();
        this.type = item.getType().toString();
        this.price = item.getPrice();
        this.description = item.getDescription();
        this.icon = item.getIcon();
        this.color = item.getColor();
        this.content = item.getContent();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ShopItemDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", icon='" + icon + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
