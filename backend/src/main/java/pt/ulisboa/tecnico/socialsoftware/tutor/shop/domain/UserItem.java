package pt.ulisboa.tecnico.socialsoftware.tutor.shop.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;

@Entity
@Table(name = "user_items")
public class UserItem {
    public enum Type {
        PRIMARY_COLOR, POST_AWARD, POWER_UP
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "item_name")
    private String name;

    @Column(name = "item_type")
    private Type type;

    @Column(name = "item_description")
    private String description;

    @Column(name = "item_icon")
    private String icon;

    @Column(name = "item_color")
    private String color;

    @ManyToOne
    private User user;

    public UserItem() {
    }

    public UserItem(ShopItem item, User user) {
        this.name = item.getName();
        this.type = Type.valueOf(item.getType().toString());
        this.description = item.getDescription();
        this.icon = item.getIcon();
        this.color = item.getColor();
        this.user = user;
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

    @Override
    public String toString() {
        return "UserItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", description='" + description + '\'' +
                ", icon='" + icon + '\'' +
                ", color='" + color + '\'' +
                ", user=" + user +
                '}';
    }
}
