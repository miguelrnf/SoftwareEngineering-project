package pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.TopicConjunction;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "suggestions",
        indexes = {
                @Index(name = "sugg_indx_0", columnList = "key")
        })
public class Suggestion {
    public enum Status {
        TOAPPROVE, APPROVED, REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int _id;


    @Column(unique=true, nullable = false)
    private Integer key;

    @Column(columnDefinition = "TEXT")
    private String _questionStr;

    @ManyToOne
    @JoinColumn(name = "topic_conjunction_id")
    private TopicConjunction topics;


    @Column(name = "changed_status", columnDefinition = "boolean default false")
    private Boolean _changed;

    @Column(name = "justification")
    private String _justification;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;



    @Enumerated(EnumType.STRING)
    private Status status = Status.TOAPPROVE;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User _student;

    public Suggestion() {
    }

    public Suggestion(Integer key, User student, String questionStr ) {
        this.key=key;
        this._student=student;
        //this.creationDate=date;
        this._questionStr=questionStr;
        this._changed = false;

    }
    public TopicConjunction get_topicsList() {
        return topics;
    }

    public void set_topicsList(TopicConjunction t) {
        this.topics = t;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String get_questionStr() {
        return _questionStr;
    }

    public void set_questionStr(String _questionStr) {
        this._questionStr = _questionStr;
    }


    public Boolean get_changed() {
        return _changed;
    }

    public void set_changed(Boolean _changed) {
        this._changed = _changed;
    }

    public String get_justification() {
        return _justification;
    }

    public void set_justification(String _justification) {
        this._justification = _justification;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public User get_student() {
        return _student;
    }

    public void set_student(User _student) {
        this._student = _student;
    }
}
