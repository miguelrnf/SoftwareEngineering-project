package pt.ulisboa.tecnico.socialsoftware.tutor.post.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "post", orphanRemoval = true)
    private PostQuestion question;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "post", orphanRemoval = true)
    private PostAnswer answer;

    @Column(name = "post_status", columnDefinition = "boolean default true")
    private Boolean postStatus;

    @Column(name = "discuss_status", columnDefinition = "boolean default false")
    private Boolean discussStatus;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post", orphanRemoval = true)
    private Set<PostComment> comments = new HashSet<>();

    public Post() {
    }

    public Post(Integer key, PostQuestion question) {
        if(key == 0) this.key = 1;
        else this.key = key;
        this.question = question;
        this.postStatus = true;
        this.discussStatus = false;
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

    public PostQuestion getQuestion() {
        return question;
    }

    public void setQuestion(PostQuestion question) {
        this.question = question;
    }

    public PostAnswer getAnswer() {
        return answer;
    }

    public void setAnswer(PostAnswer answer) {
        this.answer = answer;
    }

    public Boolean getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(Boolean postStatus) {
        this.postStatus = postStatus;
    }

    public Boolean getDiscussStatus() {
        return discussStatus;
    }

    public void setDiscussStatus(Boolean discussStatus) {
        this.discussStatus = discussStatus;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void changePostStatus() {
        this.postStatus = !this.postStatus;
    }

    public Set<PostComment> getComments() {
        return comments;
    }

    public void setComments(Set<PostComment> comments) {
        this.comments = comments;
    }

    public void addComment(PostComment pc) {
        this.comments.add(pc);
    }

    public void remove() {
        this.question.setPost(null);
        if(this.comments != null)
            this.comments.forEach(x -> x.setPost(null));
        this.comments = null;
    }

    public void changeDiscussStatus() {
        this.discussStatus = !this.discussStatus;
    }
}
