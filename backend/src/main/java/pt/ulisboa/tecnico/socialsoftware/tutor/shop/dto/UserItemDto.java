package pt.ulisboa.tecnico.socialsoftware.tutor.shop.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.shop.domain.PostAwardItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.shop.domain.UserItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.io.Serializable;

public class UserItemDto implements Serializable {
    private Integer id;
    private String name;
    private String description;
    private String icon;
    private String color;

    public UserItemDto() {
    }

    public UserItemDto(UserItem item) {
        this.id = item.getId();
        this.name = item.getName();
        this.description = item.getDescription();
        this.icon = item.getIcon();
        this.color = item.getColor();
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
}
