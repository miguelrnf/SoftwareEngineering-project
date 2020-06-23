package pt.ulisboa.tecnico.socialsoftware.tutor.shop.domain;

import javax.persistence.*;

@Entity
@Table(name = "shop_items")
public class ShopItem {
    public enum Type {
        THEME, POST_AWARD, POWER_UP
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "item_name", unique = true)
    private String name;

    @Column(name = "item_type")
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(name = "item_price")
    private Integer price;

    @Column(name = "item_description")
    private String description;

    @Column(name = "item_icon")
    private String icon;

    @Column(name = "item_color")
    private String color;

    @Column(name = "item_content")
    private String content;

    public ShopItem() {
    }

    public ShopItem(String name, String type, int price, String description, String icon, String color, String content) {
        this.name = name;
        this.type = Type.valueOf(type);
        this.price = price;
        this.description = description;
        this.icon = icon;
        this.color = color;
        this.content = content;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(String type) {
        this.type = Type.valueOf(type);
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
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
        return "ShopItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", icon='" + icon + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
