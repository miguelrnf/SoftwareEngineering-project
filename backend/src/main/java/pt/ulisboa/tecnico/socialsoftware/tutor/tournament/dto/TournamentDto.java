package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.AssessmentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.SolvedQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TournamentDto implements Serializable {
    private Integer id;
    private Integer numberOfQuestions;
    private String creationDate = null;
    private String availableDate = null;
    private String conclusionDate = null;
    private AssessmentDto assessmentDto;
    private String title;
    private UserDto owner;
    private String status = "CREATED";
    private List<UserDto> enrolledStudents = new ArrayList<>();
    private StatementQuizDto quiz;
    private SolvedQuizDto solved;
    private boolean isCompleted = false;
    private String type;
    private Integer cost;
    private Integer prize;

    public TournamentDto(){
    }

    public TournamentDto(Tournament tournament){
        this.id = tournament.getId();
        setTitle(tournament.getTitle());

        this.status = Tournament.TournamentStatus.CREATED.name();

        if (tournament.getStatus() != null)
            this.status = tournament.getStatus().name();

        if (tournament.getType() != null)
            this.type = tournament.getType().name();

        this.owner = new UserDto(tournament.getOwner());

        if (tournament.getCreationDate() != null)
            this.creationDate = DateHandler.toISOString(tournament.getCreationDate());
        if (tournament.getAvailableDate() != null)
            this.availableDate = DateHandler.toISOString(tournament.getAvailableDate());
        if (tournament.getConclusionDate() != null)
            this.conclusionDate = DateHandler.toISOString(tournament.getConclusionDate());

        this.numberOfQuestions = tournament.getNumberOfQuestions();
        this.assessmentDto = new AssessmentDto(tournament.getAssessment());
        this.enrolledStudents = tournament.getEnrolledStudents().stream().map(UserDto::new).collect(Collectors.toList());
        this.quiz = new StatementQuizDto();
        this.cost = tournament.getCost();
        this.prize = tournament.getPrize();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Integer getPrize() {
        return prize;
    }

    public void setPrize(Integer prize) {
        this.prize = prize;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public StatementQuizDto getQuiz() {
        return quiz;
    }

    public void setQuiz(StatementQuizDto quiz) {
        this.quiz = quiz;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public SolvedQuizDto getSolved() {
        return solved;
    }

    public void setSolved(SolvedQuizDto solved) {
        this.solved = solved;
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

    public List<UserDto> getEnrolledStudents() {
        return enrolledStudents;
    }

    @Override
    public String toString() {
        return "TournamentDto{" +
                "id=" + id +
                ", numberOfQuestions=" + numberOfQuestions +
                ", creationDate='" + creationDate + '\'' +
                ", availableDate='" + availableDate + '\'' +
                ", conclusionDate='" + conclusionDate + '\'' +
                ", assessmentDto=" + assessmentDto +
                ", title='" + title + '\'' +
                ", owner=" + owner +
                ", status='" + status + '\'' +
                ", enrolledStudents=" + enrolledStudents +
                ", statementQuiz=" + quiz +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TournamentDto t = (TournamentDto) o;

        if (!Objects.equals(id, t.id)) return false;
        if (!Objects.equals(title, t.title)) return false;
        if (!Objects.equals(owner, t.owner)) return false;
        if (!status.equals(t.status)) return false;
        return Objects.equals(enrolledStudents, t.enrolledStudents);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (enrolledStudents != null ? enrolledStudents.hashCode() : 0);
        return result;
    }
}
