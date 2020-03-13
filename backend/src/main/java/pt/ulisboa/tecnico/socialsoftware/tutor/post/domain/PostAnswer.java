package pt.ulisboa.tecnico.socialsoftware.tutor.post.domain;


import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostAnswerDto;
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

    @Column(name = "is_redirected", columnDefinition = "boolean default false")
    private Boolean isRedirected;

    public PostAnswer() {
    }

    public PostAnswer(User user, String teacherAnswer) {
        final int MAX_LENGTH = 1024;
        checkPostAnswerConsistency(teacherAnswer, MAX_LENGTH);
        this.user = user;
        this.teacherAnswer = teacherAnswer;
        this.isEdited = false;
        this.isRedirected = false;
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

    private void checkPostAnswerConsistency(String pa, int length) {
        if(pa == null)
            throw new TutorException(ErrorMessage.NO_ANSWER);
        if(pa.trim().isEmpty())
            throw new TutorException(ErrorMessage.INVALID_ANSWER_BLANK);
        if(pa.trim().length() > length)
            throw new TutorException(ErrorMessage.INVALID_ANSWER_TOO_LONG);

    }

    public void update(String teacherAnswer) {
        checkPostAnswerConsistency(teacherAnswer, 1024);
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

    public Boolean getRedirected() {
        return isRedirected;
    }

    public void setRedirected(Boolean redirected) {
        isRedirected = redirected;
    }
}

