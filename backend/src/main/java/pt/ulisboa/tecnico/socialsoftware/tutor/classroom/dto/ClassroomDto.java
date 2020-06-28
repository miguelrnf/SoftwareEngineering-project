package pt.ulisboa.tecnico.socialsoftware.tutor.classroom.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.classroom.domain.Classroom;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClassroomDto implements Serializable {
    private Integer id;
    private List<Integer> quizzes = new ArrayList<>();
    private List<DocumentDto> documents = new ArrayList<>();
    private String status;
    private String type;
    private String title;
    private Integer courseExecution;
    private String availableDate;

    public ClassroomDto(){
    }

    public ClassroomDto(Classroom classroom){
        this.id = classroom.getId();
        this.title = classroom.getTitle();
        this.status = classroom.getStatus().name();
        this.type = classroom.getType().name();
        this.documents = classroom.getDocuments() != null ? classroom.getDocuments().stream().map(DocumentDto::new).collect(Collectors.toList()) : null;
        this.quizzes = classroom.getQuizzes().stream().map(quiz -> quiz.getId()).collect(Collectors.toList());
        this.courseExecution = classroom.getCourseExecution() != null ? classroom.getCourseExecution().getId() : null;
        if (classroom.getAvailableDate() != null)
            this.availableDate = DateHandler.toISOString(classroom.getAvailableDate());
    }

    public Integer getCourseExecution() {
        return courseExecution;
    }

    public String getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(String availableDate) {
        this.availableDate = availableDate;
    }

    public void setCourseExecution(Integer courseExecution) {
        this.courseExecution = courseExecution;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Integer> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(List<Integer> quizzes) {
        this.quizzes = quizzes;
    }

    public List<DocumentDto> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentDto> documents) {
        this.documents = documents;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "ClassroomDto{" +
                "id=" + id +
                ", quizzes=" + quizzes +
                ", documents=" + documents +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", courseExecution=" + courseExecution +
                ", availableDate='" + availableDate + '\'' +
                '}';
    }
}
