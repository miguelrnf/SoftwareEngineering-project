package pt.ulisboa.tecnico.socialsoftware.tutor.shop.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;

@Entity
@DiscriminatorValue("THEME")
@Table(name = "item_theme")
public class ThemeItem extends UserItem {
    @Column(name = "theme_success")
    private String success;

    @Column(name = "theme_secondary")
    private String secondary;

    @Column(name = "theme_accent")
    private String accent;

    @Column(name = "theme_info")
    private String info;

    @Column(name = "theme_warning")
    private String warning;

    @Column(name = "theme_primary")
    private String primary;

    @Column(name = "theme_error")
    private String error;

    public ThemeItem() {
    }

    public ThemeItem(ShopItem item, User user) {
        this.setName(item.getName());
        this.setDescription(item.getDescription());
        this.setIcon(item.getIcon());
        this.setColor(item.getColor());
        this.setUser(user);
        checkContentConsistency(item.getContent());
    }

    @Override
    protected void checkContentConsistency(String content) {
        String[] colors = content.split(",");
        if(colors.length != 7) throw new TutorException(ErrorMessage.INVALID_THEME);
        this.success = colors[0];
        this.secondary = colors[1];
        this.accent = colors[2];
        this.info = colors[3];
        this.warning = colors[4];
        this.primary = colors[5];
        this.error = colors[6];
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getSecondary() {
        return secondary;
    }

    public void setSecondary(String secondary) {
        this.secondary = secondary;
    }

    public String getAccent() {
        return accent;
    }

    public void setAccent(String accent) {
        this.accent = accent;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
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
                ", user=" + this.getUser() +
                "}\n" +
                "ThemeItem{" +
                "success='" + success + '\'' +
                ", secondary='" + secondary + '\'' +
                ", accent='" + accent + '\'' +
                ", info='" + info + '\'' +
                ", warning='" + warning + '\'' +
                ", primary='" + primary + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}
