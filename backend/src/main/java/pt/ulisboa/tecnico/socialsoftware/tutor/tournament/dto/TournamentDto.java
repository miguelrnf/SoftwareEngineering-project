package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import org.springframework.data.annotation.Transient;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.AssessmentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TournamentDto implements Serializable {

    private Integer id;
    private Integer key;
    private Integer numberOfQuestions;
    private String creationDate = null;
    private String availableDate = null;
    private String conclusionDate = null;
    private AssessmentDto assessmentDto;
    private String title;
    private UserDto owner;
    private String status;
    private List<UserDto> enrolledStudents = new ArrayList<>();

    @Transient
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public TournamentDto(){
    }

    public TournamentDto(Tournament tournament){
        this.id = tournament.getId();
        this.key = tournament.getKey();
        setTitle(tournament.getTitle());

        this.status = Tournament.TournamentStatus.CREATED.name();

        if (tournament.getStatus() != null)
            this.status = tournament.getStatus().name();

        this.owner = new UserDto(tournament.getOwner());

        if (tournament.getCreationDate() != null)
            this.creationDate = tournament.getCreationDate().format(formatter);
        if (tournament.getAvailableDate() != null)
            this.availableDate = tournament.getAvailableDate().format(formatter);
        if (tournament.getConclusionDate() != null)
            this.conclusionDate = tournament.getConclusionDate().format(formatter);

        this.numberOfQuestions = tournament.getNumberOfQuestions();
        this.assessmentDto = new AssessmentDto(tournament.getAssessment());

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UserDto getOwner() {
        return owner;
    }

    public void setOwner(UserDto owner) {
        this.owner = owner;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(String availableDate) {
        this.availableDate = availableDate;
    }

    public String getConclusionDate() {
        return conclusionDate;
    }

    public void setConclusionDate(String conclusionDate) {
        this.conclusionDate = conclusionDate;
    }

    public void setEnrolledStudents(List<UserDto> enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

    public LocalDateTime getCreationDateDate() {
        if (getCreationDate() == null || getCreationDate().isEmpty())
            return null;
        return LocalDateTime.parse(getCreationDate(), formatter);
    }

    public LocalDateTime getAvailableDateDate() {
        if (getAvailableDate() == null || getAvailableDate().isEmpty())
            return null;
        return LocalDateTime.parse(getAvailableDate(), formatter);
    }

    public LocalDateTime getConclusionDateDate() {
        if (getConclusionDate() == null || getConclusionDate().isEmpty())
            return null;
        return LocalDateTime.parse(getConclusionDate(), formatter);
    }

    public Integer getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(Integer numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public AssessmentDto getAssessmentDto() {
        return assessmentDto;
    }

    public void setAssessmentDto(AssessmentDto assessmentDto) {
        this.assessmentDto = assessmentDto;
    }

    @Override
    public String toString() {
        return "TournamentDto{" +
                "id=" + id +
                ", key=" + key +
                ", numberOfQuestions=" + numberOfQuestions +
                ", creationDate='" + creationDate + '\'' +
                ", availableDate='" + availableDate + '\'' +
                ", conclusionDate='" + conclusionDate + '\'' +
                ", assessmentDto=" + assessmentDto +
                ", title='" + title + '\'' +
                ", owner=" + owner +
                ", status='" + status + '\'' +
                ", enrolledStudents=" + enrolledStudents +
                ", formatter=" + formatter +
                '}';
    }
}
