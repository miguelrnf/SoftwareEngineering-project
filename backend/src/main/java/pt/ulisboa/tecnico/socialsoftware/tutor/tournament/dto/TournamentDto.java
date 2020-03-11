package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;
import java.io.Serializable;


public class TournamentDto implements Serializable {

    private Integer id;
    private Integer key;
    private String title;
    private UserDto owner;
    private Tournament.TournamentStatus status;

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

    @Override
    public String toString() {
        return "TournamentDto{" +
                "id=" + id +
                ", key=" + key +
                ", title='" + title + '\'' +
                ", owner=" + owner +
                ", status=" + status +
                '}';
    }
}
