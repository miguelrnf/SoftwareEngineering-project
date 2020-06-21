package pt.ulisboa.tecnico.socialsoftware.tutor.shop.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.shop.domain.ShopItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.shop.domain.ThemeItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.shop.domain.UserItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;


public class ThemeItemDTO implements Serializable {
    private UserItemDTO userItemDTO;

    private String success;
    private String secondary;
    private String accent;
    private String info;
    private String warning;
    private String primary;
    private String error;
    private Boolean dark;

    public ThemeItemDTO() {}

    public ThemeItemDTO(ThemeItem item) {

        this.userItemDTO = new UserItemDTO(item);

        this.success = item.getSuccess();
        this.secondary = item.getSecondary();
        this.accent = item.getAccent();
        this.info = item.getInfo();
        this.warning = item.getWarning();
        this.primary = item.getPrimary();
        this.error = item.getError();
        this.dark = item.getDark();
    }

    public UserItemDTO getUserItemDTO() {
        return userItemDTO;
    }

    public void setUserItemDTO(UserItemDTO userItemDTO) {
        this.userItemDTO = userItemDTO;
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

    public Boolean getDark() {
        return dark;
    }

    public void setDark(Boolean dark) {
        this.dark = dark;
    }

    @Override
    public String toString() {
        return "ThemeItemDTO{" +
                "userItemDTO=" + userItemDTO +
                ", success='" + success + '\'' +
                ", secondary='" + secondary + '\'' +
                ", accent='" + accent + '\'' +
                ", info='" + info + '\'' +
                ", warning='" + warning + '\'' +
                ", primary='" + primary + '\'' +
                ", error='" + error + '\'' +
                ", dark=" + dark +
                '}';
    }
}
