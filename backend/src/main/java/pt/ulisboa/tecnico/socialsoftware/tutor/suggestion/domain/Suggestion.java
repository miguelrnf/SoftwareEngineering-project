package pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;
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
        TOAPPROVE, APPROVED, REJECTED, QUESTION
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique=true, nullable = false)
    private Integer key;

    @Column(name = "title")
    private String title;

    @Column(name = "student_question")
    private String studentQuestion;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "suggestions")
    private Set<Topic> topics = new HashSet<>();

    @Column(name = "teacher_explanation")
    private String teacherExplanation;

    @Column(name = "privacy")
    private Boolean isPrivate;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "checkMark", columnDefinition = "boolean default false")
    private Boolean checkMark;

    @Enumerated(EnumType.STRING)
    public Status status = Status.TOAPPROVE;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "course_execution_id")
    private CourseExecution courseExecution;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User student;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "suggestion", fetch = FetchType.EAGER, orphanRemoval=true)
    private List<Option> options = new ArrayList<>();

    public Suggestion() {
    }

    public Suggestion(CourseExecution courseExecution, User user, SuggestionDto suggestionDto) {
        checkConsistentSuggestion(suggestionDto);
        this.key= suggestionDto.getKey();
        this.student= user;
        this.studentQuestion = suggestionDto.getStudentQuestion();
        this.teacherExplanation = "";
        this.isPrivate = false;

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
        if (suggestionDto.getStudentQuestion().trim().length() == 0 ){
            throw new TutorException(SUGGESTION_EMPTY);
        }
        if (suggestionDto.getStudentQuestion().trim().length() > 500 ){
            throw new TutorException(SUGGESTION_TOO_LONG);
        }
    }

    public Set<Topic> getTopicsList() {
        return topics;
    }

    public void setTopicsList(Set<Topic> t) {
        this.topics = t;
    }

    public CourseExecution getCourse() {
        return courseExecution;
    }

    public void setCourse(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
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

    public String getStudentQuestion() {
        return studentQuestion;
    }

    public void setStudentQuestion(String studentQuestion) {
        this.studentQuestion = studentQuestion;
    }

    public String getTeacherExplanation() {
        return teacherExplanation;
    }

    public void setTeacherExplanation(String teacherExplanation) {
        if (teacherExplanation.length() == 0 ){
            throw new TutorException(JUSTIFICATION_EMPTY);
        }
        this.teacherExplanation = teacherExplanation;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
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

    public void addOptions(SuggestionDto suggestionDto){
        int index = 0;
        for (
                OptionDto optionDto : suggestionDto.getOptions()) {
            optionDto.setSequence(index++);
            Option option = new Option(optionDto);
            this.options.add(option);
            option.setSuggestion(this);
        }
    }

    public Boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Boolean isprivate) {
        this.isPrivate = isprivate;
    }

    public void remove(User.Role role) {
        if(role.equals(User.Role.STUDENT)){
            canRemove();
        }


        getTopicsList().forEach(topic -> topic.getSuggestions().remove(this));
        getTopicsList().clear();
        getOptions().forEach(option -> option.setSuggestion(null));
        getOptions().clear();
        getCourse().getSuggestions().remove(this);
        courseExecution = null;

    }

    private void canRemove() {
        if (this.getStatus().equals(Status.APPROVED)) {
            throw new TutorException(SUGGESTION__REM_ALREADY_APP);
        }
        if (this.getStatus().equals(Status.QUESTION)) {
            throw new TutorException(SUGGESTION__REM_ALREADY_QUESTION);
        }
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Suggestion that = (Suggestion) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(key, that.key) &&
                Objects.equals(studentQuestion, that.studentQuestion) &&
                Objects.equals(topics, that.topics) &&
                Objects.equals(teacherExplanation, that.teacherExplanation) &&
                status == that.status &&
                Objects.equals(courseExecution, that.courseExecution) &&
                Objects.equals(student, that.student);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, key, studentQuestion, topics, teacherExplanation, status, courseExecution, student);
    }

    @Override
    public String toString() {
        return "SuggestionDto{" +
                "id=" + id +
                ", key=" + key +
                ", studentQuestion='" + studentQuestion + '\'' +
                ", topicsList=" + topics +
                ", teacherExplanation='" + teacherExplanation + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", status='" + status + '\'' +
                ", student=" + student.getUsername() +
                ", options=" + options +
                ", isprivate=" + isPrivate +
                ", title='" + title + '\'' +
                '}';
    }

    public Boolean getCheckMark() {
        return checkMark;
    }

    public void setCheckMark(Boolean checkMark) {
        this.checkMark = checkMark;
    }
}
