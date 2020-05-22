package pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.domain.Suggestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SuggestionDto implements Serializable{


    private Integer id;
    private Integer key;
    private String studentQuestion;
    private List<TopicDto> topicsList = new ArrayList<>();
    private String justification;
    private String creationDate = null;
    private String status;
    private UserDto student;
    private CourseExecution courseExecution;
    private List<OptionDto> options = new ArrayList<>();
    private Boolean isprivate = false;

    private String title;


    public SuggestionDto(){
    }

    public SuggestionDto(Suggestion suggestion) {
        this.id = suggestion.getId();
        this.key=suggestion.getKey();
        this.topicsList = suggestion.getTopicsList().stream().map(TopicDto::new).collect(Collectors.toList());
        this.courseExecution = suggestion.getCourse();
        this.justification =suggestion.getTeacherExplanation();
        this.student =new UserDto(suggestion.getStudent());
        this.studentQuestion =suggestion.getStudentQuestion();
        this.status =suggestion.getStatus().name();
        this.isprivate = suggestion.getIsprivate();

        if (suggestion.getCreationDate() != null)
            this.creationDate = suggestion.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        this.options = suggestion.getOptions().stream().map(OptionDto::new).collect(Collectors.toList());

        this.title= suggestion.getTitle();

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

    public List<TopicDto> getTopicsList() {
        return topicsList;
    }

    public void setTopicsList(List<TopicDto> topicsList) {
        this.topicsList = topicsList;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserDto getStudent() {
        return student;
    }

    public void setStudent(UserDto student) {
        this.student = student;
    }

    public void setCourse(CourseExecution _courseexecution) {
        this.courseExecution = _courseexecution;
    }

    public List<OptionDto> getOptions() {
        return options;
    }

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }

    public void setOptions(List<OptionDto> options) {
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
        SuggestionDto that = (SuggestionDto) o;
        return id.equals(that.id) &&
                Objects.equals(key, that.key) &&
                Objects.equals(studentQuestion, that.studentQuestion) &&
                Objects.equals(topicsList, that.topicsList) &&
                Objects.equals(justification, that.justification) &&
                Objects.equals(creationDate, that.creationDate) &&
                Objects.equals(status, that.status) &&
                Objects.equals(student, that.student);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, key, studentQuestion, topicsList, justification, creationDate, status, student);
    }

    @Override
    public String toString() {
        return "SuggestionDto{" +
                "_id=" + id +
                ", key=" + key +
                ", _questionStr='" + studentQuestion + '\'' +
                ", _topicsList=" + topicsList +
                ", _justification='" + justification + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", _status='" + status + '\'' +
                ", _student=" + student +
                '}';
    }

    public Boolean getIsprivate() {
        return isprivate;
    }

    public void setIsprivate(Boolean isprivate) {
        this.isprivate = isprivate;
    }
}
