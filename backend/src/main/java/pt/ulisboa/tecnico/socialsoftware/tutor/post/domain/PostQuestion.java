package pt.ulisboa.tecnico.socialsoftware.tutor.post.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;

@Entity
@Table(name = "post_questions")
public class PostQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "students_question")
    private String studentQuestion;

    public PostQuestion() {
    }

    public PostQuestion(Question q, User u, PostQuestionDto pq) {
        final int MAX_LENGTH = 1024;
        checkPostQuestionConsistency(pq.getStudentQuestion(), MAX_LENGTH);
        this.question = q;
        this.user = u;
        this.studentQuestion = pq.getStudentQuestion();
    }

    private void checkPostQuestionConsistency(String sq, int length) {
        if(sq == null)
            throw new TutorException(ErrorMessage.NO_STUDENT_QUESTION);
        if(sq.trim().isEmpty())
            throw new TutorException(ErrorMessage.NO_STUDENT_QUESTION);
        if(sq.trim().length() > length)
            throw new TutorException(ErrorMessage.STUDENT_QUESTION_TOO_LONG);

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStudentQuestion() {
        return studentQuestion;
    }

    public void setStudentQuestion(String studentQuestion) {
        this.studentQuestion = studentQuestion;
    }

    public void update(String studentQuestion) {
        checkPostQuestionConsistency(studentQuestion, 1024);
        this.studentQuestion = studentQuestion;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
