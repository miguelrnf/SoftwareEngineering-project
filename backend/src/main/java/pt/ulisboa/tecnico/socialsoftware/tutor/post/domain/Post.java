package pt.ulisboa.tecnico.socialsoftware.tutor.post.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "posts",
        indexes = {
                @Index(name = "post_index_0", columnList = "key")
        }
)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique=true, nullable = false)
    private Integer key;

    @OneToOne(cascade = CascadeType.ALL,mappedBy = "post", orphanRemoval = true)
    private PostQuestion question;

    @Column(name = "post_status", columnDefinition = "boolean default true")
    private Boolean postStatus;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    public Post() {
    }

    public Post(Integer key, PostQuestion question) {
        this.key = key;
        this.question = question;
        this.postStatus = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public PostQuestion getQuestion() {
        return question;
    }

    public void setQuestion(PostQuestion question) {
        this.question = question;
    }

    public Boolean getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(Boolean postStatus) {
        this.postStatus = postStatus;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
}
