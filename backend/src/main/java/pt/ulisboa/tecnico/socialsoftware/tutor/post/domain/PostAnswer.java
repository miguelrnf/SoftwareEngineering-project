package pt.ulisboa.tecnico.socialsoftware.tutor.post.domain;


import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_answers")

public class PostAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "teacher_answer")
    private String teacherAnswer;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "is_edited", columnDefinition = "boolean default false")
    private Boolean isEdited;

    public PostAnswer() {
    }

    public PostAnswer(User user, String teacherAnswer) {
        this.user = user;
        this.teacherAnswer = teacherAnswer;
        this.isEdited = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTeacherAnswer() {
        return teacherAnswer;
    }

    public void setTeacherAnswer(String teacherAnswer) {
        this.teacherAnswer = teacherAnswer;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Boolean getEdited() {
        return isEdited;
    }

    public void setEdited(Boolean edited) {
        isEdited = edited;
    }
}

