package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tournaments",
       indexes = {
        @Index(name = "tournament_indx_0", columnList = "key")
       }
)
public class Tournament {

    public enum TournamentStatus {
        CREATED, OPEN, CLOSED, CANCELED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique=true, nullable = false)
    private Integer key;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    private TournamentStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "course_execution_id")
    private CourseExecution courseExecution;


    @ManyToMany(mappedBy = "tournaments")
    private Set<User> enrolledStudents = new HashSet<>();

    public Tournament(){

    }

    public Tournament(TournamentDto tournamentDto, User user){

        this.key = tournamentDto.getKey();
        setTitle(tournamentDto.getTitle());
        this.status = tournamentDto.getStatus();
        this.owner = user;
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

    public User getOwner() {
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

    public void setOwner(User owner) {
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

    public void enrollStudent(User user){
        enrolledStudents.add(user);
    }

    @Override
    public String toString() {
        return "Tournament {" +
                "id=" + id +
                ", key=" + key +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", owner=" + owner +
                ", courseExecution=" + courseExecution +
                ", enrolledStudents=" + enrolledStudents +
                '}';
    }
}
