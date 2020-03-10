package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TournamentDto implements Serializable {

    private Integer id;
    private Integer key;
    private String title;
    private UserDto owner;
    private Tournament.TournamentStatus status;
    private List<UserDto> enrolledStudents = new ArrayList<>();

    public TournamentDto(){
    }

    public TournamentDto(Tournament tournament){
        this.id = tournament.getId();
        this.key = tournament.getKey();
        setTitle(tournament.getTitle());

        this.status = Tournament.TournamentStatus.CREATED;

        if (tournament.getStatus() != null){
            this.status = tournament.getStatus();
        }
        this.owner = new UserDto(tournament.getOwner());

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

    public Tournament.TournamentStatus getStatus() {
        return status;
    }

    public void setStatus(Tournament.TournamentStatus status) {
        this.status = status;
    }

    public List<UserDto> getEnrolledStudents() {
        return enrolledStudents;
    }

    @Override
    public String toString() {
        return "TournamentDto{" +
                "id=" + id +
                ", key=" + key +
                ", title='" + title + '\'' +
                ", owner=" + owner +
                ", status=" + status +
                ", enrolledStudents=" + enrolledStudents +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        System.out.println("1");
        if (this == o) return true;
        System.out.println("2");
        if (o == null || getClass() != o.getClass()) return false;

        TournamentDto t = (TournamentDto) o;

        System.out.println("3");
        if (!Objects.equals(id, t.id)) return false;
        System.out.println("4");
        if (!Objects.equals(key, t.key)) return false;
        System.out.println("5");
        if (!Objects.equals(title, t.title)) return false;
        System.out.println("6");
        if (!Objects.equals(owner, t.owner)) return false;
        System.out.println("7");
        if (status != t.status) return false;
        System.out.println("8");
        return Objects.equals(enrolledStudents, t.enrolledStudents);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (key != null ? key.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (enrolledStudents != null ? enrolledStudents.hashCode() : 0);
        return result;
    }
}
