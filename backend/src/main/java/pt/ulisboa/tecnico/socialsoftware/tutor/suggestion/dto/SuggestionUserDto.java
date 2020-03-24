package pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.io.Serializable;

public class SuggestionUserDto implements Serializable{

    private SuggestionDto suggDto;
    private UserDto userDto;

    public SuggestionUserDto(SuggestionDto s, UserDto t){
        suggDto=s;
        userDto=t;
    }

    public SuggestionDto getSuggDto() {
        return suggDto;
    }

    public UserDto getUserDto() {
        return userDto;
    }

    public void setUserDto(UserDto userDto) {
        this.userDto = userDto;
    }

    public void setSuggDto(SuggestionDto suggDto) {
        this.suggDto = suggDto;
    }
}
