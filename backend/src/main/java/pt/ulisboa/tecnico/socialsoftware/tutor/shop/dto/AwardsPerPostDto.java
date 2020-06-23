package pt.ulisboa.tecnico.socialsoftware.tutor.shop.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.shop.domain.PostAwardItem;

public class AwardsPerPostDto {

    private PostAwardItemDto award;
    private Integer number;

    public AwardsPerPostDto() {
    }

    public AwardsPerPostDto(PostAwardItemDto award, Integer number) {
        this.award = award;
        this.number = number;
    }

    public PostAwardItemDto getAward() {
        return award;
    }

    public void setAward(PostAwardItemDto award) {
        this.award = award;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "AwardsPerPostDto{" +
                "award=" + award +
                ", number=" + number +
                '}';
    }
}
