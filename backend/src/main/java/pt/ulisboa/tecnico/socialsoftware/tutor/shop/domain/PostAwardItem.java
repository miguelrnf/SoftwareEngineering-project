package pt.ulisboa.tecnico.socialsoftware.tutor.shop.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@DiscriminatorValue("POST_AWARD")
@Table(name = "item_post_award")
public class PostAwardItem extends UserItem {
    public enum Type {
        SILVER, GOLD, PLATINUM
    }

    @Column(name = "post_award_type")
    private Type type;

    public PostAwardItem() {
    }

    public PostAwardItem(ShopItem item, User user) {
        this.setName(item.getName());
        this.setDescription(item.getDescription());
        this.setIcon(item.getIcon());
        this.setColor(item.getColor());
        this.setUser(user);
        checkContentConsistency(item.getContent());
    }

    @Override
    protected void checkContentConsistency(String content) {
       this.type = Type.valueOf(content);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }


    @Override
    public String toString() {
        return
                "UserItem{" +
                "id=" + this.getId() +
                ", name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", icon='" + this.getIcon() + '\'' +
                ", color='" + this.getColor() + '\'' +
                "}\n" +
                "PostAwardItem{" +
                "type=" + type +
                '}';
    }
}
