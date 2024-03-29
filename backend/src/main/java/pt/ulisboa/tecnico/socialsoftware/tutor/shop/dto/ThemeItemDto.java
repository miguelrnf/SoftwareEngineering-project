package pt.ulisboa.tecnico.socialsoftware.tutor.shop.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.shop.domain.ThemeItem;

import java.io.Serializable;


public class ThemeItemDto implements Serializable {
    private UserItemDto userItemDto;

    private String success;
    private String secondary;
    private String accent;
    private String info;
    private String warning;
    private String primary;
    private String error;
    private Boolean dark;

    public ThemeItemDto() {}

    public ThemeItemDto(ThemeItem item) {

        this.userItemDto = new UserItemDto(item);

        this.success = item.getSuccess();
        this.secondary = item.getSecondary();
        this.accent = item.getAccent();
        this.info = item.getInfo();
        this.warning = item.getWarning();
        this.primary = item.getPrimary();
        this.error = item.getError();
        this.dark = item.getDark();
    }

    public UserItemDto getUserItemDto() {
        return userItemDto;
    }

    public void setUserItemDto(UserItemDto userItemDto) {
        this.userItemDto = userItemDto;

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
                "userItemDTO=" + userItemDto +
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
