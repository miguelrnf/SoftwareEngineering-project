package pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.dto.SuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

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
    @JoinColumn(name = "course_execution_id")
    private CourseExecution courseExecution;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User _student;

    public Suggestion() {
    }

    public Suggestion(CourseExecution courseExecution, User user, SuggestionDto suggestionDto) {
        checkConsistentSuggestion(suggestionDto);

        this.key= suggestionDto.getKey();
        this._student= user;

        this._questionStr= suggestionDto.get_questionStr();
        this._changed = false;
        this._justification = "";
        String str = suggestionDto.getCreationDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.creationDate = LocalDateTime.parse(str, formatter);

        this.status = Suggestion.Status.valueOf(suggestionDto.getStatus());
        this.courseExecution = courseExecution;

        courseExecution.addSuggestion(this);

    }
    private void checkConsistentSuggestion(SuggestionDto suggestionDto) {
        if (suggestionDto.get_topicsList().isEmpty()){
            throw new TutorException(EMPTY_TOPICS);
        }
        if (suggestionDto.get_questionStr().trim().length() == 0 ){
            throw new TutorException(SUGGESTION_EMPTY);
        }
        if (suggestionDto.get_questionStr().trim().length() > 500 ){
            throw new TutorException(SUGGESTION_TOO_LONG);
        }
    }

    public TopicConjunction get_topicsList() {
        return topics;
    }

    public void set_topicsList(TopicConjunction t) {
        this.topics = t;
    }

    public CourseExecution getCourse() {
        return courseExecution;
    }

    public void setCourse(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
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
