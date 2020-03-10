package pt.ulisboa.tecnico.socialsoftware.tutor.post.domain;


import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;

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

    public PostAnswer() {
    }

    public PostAnswer(User user, String teacherAnswer) {
        this.user = user;
        this.teacherAnswer = teacherAnswer;
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
}

