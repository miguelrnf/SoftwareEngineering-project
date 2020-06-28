package pt.ulisboa.tecnico.socialsoftware.tutor.classroom.dto;

public class YaDto {

    private String firstName;
    private String lastName;

    public YaDto(){}

    public YaDto(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
