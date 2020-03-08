package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.util.HashSet;
import java.util.Set;

public class Tournament {

    public enum TournamentStatus {
        CREATED, OPEN, CLOSED, CANCELED
    }

    private Integer id;
    private Integer key;
    private String title;
    private TournamentStatus status;
    private QuizDto quiz;
    private UserDto owner;
    private CourseExecution courseExecution;
    private Set<User> enrolledStudents = new HashSet<>();

    public Tournament(){

    }

    public Tournament(TournamentDto tournamentDto){
        this.id = tournamentDto.getId();
        this.key = tournamentDto.getKey();
        setTitle(tournamentDto.getTitle());
        this.status = tournamentDto.getStatus();
        this.quiz = tournamentDto.getQuiz();
        this.owner = tournamentDto.getOwner();
    }

    public Integer getId() {
        return id;
    }

    public Integer getKey() {
        return key;
    }


    public String getTitle() {
        return title;
    }

    public TournamentStatus getStatus() {
        return status;
    }

    public QuizDto getQuiz() {
        return quiz;
    }

    public UserDto getOwner() {
        return owner;
    }

    public Set<User> getEnrolledStudents() {
        return enrolledStudents;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStatus(TournamentStatus status) {
        this.status = status;
    }

    public void setQuiz(QuizDto quiz) {
        this.quiz = quiz;
    }

    public void setOwner(UserDto owner) {
        this.owner = owner;
    }

    public void setEnrolledStudents(Set<User> enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }

    @Override
    public String toString() {
        return "Tournament{" +
                "id=" + id +
                ", key=" + key +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", quiz=" + quiz +
                ", owner=" + owner +
                ", courseExecution=" + courseExecution +
                ", enrolledStudents=" + enrolledStudents +
                '}';
    }
}
