package pt.ulisboa.tecnico.socialsoftware.tutor.post.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostCommentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(
        name = "comments",
        indexes = {
                @Index(name = "comment_index_0", columnList = "key")
        }
)
public class PostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique=true, nullable = false)
    private Integer key;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "content")
    private String comment;

    @ManyToOne
    private PostComment parent;

    @OneToMany(mappedBy = "parent")
    private Set<PostComment> children;

    public PostComment() {
    }

    public PostComment(Integer key, User u, PostCommentDto dto) {
        int MAX_LENGTH = 1024;
        this.user = u;
        this.key = key;
        checkPostCommentConsistency(dto.getCreationDate(), dto.getComment(), MAX_LENGTH);
        this.creationDate = dto.getCreationDate();
        this.comment = dto.getComment();
    }

    private void checkPostCommentConsistency(LocalDateTime creationDate, String comment, int length) {
        if(creationDate == null || creationDate.isAfter(LocalDateTime.now()))
            throw new TutorException(ErrorMessage.INVALID_CREATION_DATE);
        if(comment == null || comment.isEmpty() || comment.length() > length)
            throw new TutorException(ErrorMessage.INVALID_COMMENT);
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public PostComment getParent() {
        return parent;
    }

    public void setParent(PostComment parent) {
        this.parent = parent;
    }

    public Set<PostComment> getChildren() {
        return children;
    }

    public void setChildren(Set<PostComment> children) {
        this.children = children;
    }

    public void addChild(PostComment pc) {
        this.children.add(pc);
    }
}
