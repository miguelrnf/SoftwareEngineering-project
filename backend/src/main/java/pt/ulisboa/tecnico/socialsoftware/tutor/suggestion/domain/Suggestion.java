package pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.dto.SuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    private Integer _id;

    @Column(unique=true, nullable = false)
    private Integer key;

    @Column(columnDefinition = "TEXT")
    private String _questionStr;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "suggestions")
    private Set<Topic> topics = new HashSet<>();

    @Column(name = "changed_status", columnDefinition = "boolean default false")
    private Boolean _changed;

    @Column(name = "justification")
    private String _justification;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Enumerated(EnumType.STRING)
    public Status status = Status.TOAPPROVE;

    @ManyToOne
    @JoinColumn(name = "course_execution_id")
    private CourseExecution courseExecution;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User student;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "suggestion", fetch = FetchType.EAGER, orphanRemoval=true)
    private List<Option> options = new ArrayList<>();

    private String title;

    public Suggestion() {
    }

    public Suggestion(CourseExecution courseExecution, User user, SuggestionDto suggestionDto) {
        checkConsistentSuggestion(suggestionDto);
        this.key= suggestionDto.getKey();
        this.student= user;
        this._questionStr= suggestionDto.get_questionStr();
        this._changed = false;
        this._justification = "";

        String str = suggestionDto.getCreationDate();
        if( str != null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            this.creationDate = LocalDateTime.parse(str, formatter);}

        this.status = Status.TOAPPROVE;
        this.courseExecution = courseExecution;

        courseExecution.addSuggestion(this);
        user.addSuggestion(this);

    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    private void checkConsistentSuggestion(SuggestionDto suggestionDto) {
        if (suggestionDto.get_questionStr().trim().length() == 0 ){
            throw new TutorException(SUGGESTION_EMPTY);
        }
        if (suggestionDto.get_questionStr().trim().length() > 500 ){
            throw new TutorException(SUGGESTION_TOO_LONG);
        }
    }

    public Set<Topic> get_topicsList() {
        return topics;
    }

    public void set_topicsList(Set<Topic> t) {
        this.topics = t;
    }

    public CourseExecution getCourse() {
        return courseExecution;
    }

    public void setCourse(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }
    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
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
        if (_justification.length() == 0 ){
            throw new TutorException(JUSTIFICATION_EMPTY);
        }
        this._justification = _justification;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public User get_student() {
        return student;
    }

    public void set_student(User student) {
        this.student = student;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Suggestion that = (Suggestion) o;
        return Objects.equals(_id, that._id) &&
                Objects.equals(key, that.key) &&
                Objects.equals(_questionStr, that._questionStr) &&
                Objects.equals(topics, that.topics) &&
                Objects.equals(_changed, that._changed) &&
                Objects.equals(_justification, that._justification) &&
                status == that.status &&
                Objects.equals(courseExecution, that.courseExecution) &&
                Objects.equals(student, that.student);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, key, _questionStr, topics, _changed, _justification, status, courseExecution, student);
    }
}
