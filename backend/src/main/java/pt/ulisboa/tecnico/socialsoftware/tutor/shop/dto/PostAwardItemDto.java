package pt.ulisboa.tecnico.socialsoftware.tutor.shop.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.shop.domain.PostAwardItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.shop.domain.UserItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.io.Serializable;

public class PostAwardItemDto implements Serializable {
    private String type;
    private UserItemDto item;

    public PostAwardItemDto() {
    }

    public PostAwardItemDto(PostAwardItem award) {
        this.type = award.getType().toString();
        this.item = new UserItemDto(award);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UserItemDto getItem() {
        return item;
    }

    public void setItem(UserItemDto item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return "PostAwardItemDto{" +
                "type='" + type + '\'' +
                ", item=" + item +
                '}';
    }
}
