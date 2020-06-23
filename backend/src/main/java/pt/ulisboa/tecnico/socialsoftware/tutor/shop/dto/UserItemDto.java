package pt.ulisboa.tecnico.socialsoftware.tutor.shop.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.shop.domain.UserItem;

import java.io.Serializable;


public class UserItemDto implements Serializable {
    private Integer id;
    private String name;
    private String description;
    private String icon;
    private String color;
    private Integer userId;

    public UserItemDto() {}

    public UserItemDto(UserItem item) {
        this.id = item.getId();
        this.name = item.getName();
        this.description = item.getDescription();
        this.icon = item.getIcon();
        this.color = item.getName();
        this.userId = item.getUser().getId();
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
        return "UserItemDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", icon='" + icon + '\'' +
                ", color='" + color + '\'' +
                ", userId=" + userId +
                '}';
    }
}
