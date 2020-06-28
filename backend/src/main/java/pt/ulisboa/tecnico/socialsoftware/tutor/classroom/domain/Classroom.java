package pt.ulisboa.tecnico.socialsoftware.tutor.classroom.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.classroom.dto.ClassroomDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "classrooms")
public class Classroom implements DomainEntity {
    public enum Status {ACTIVE, INACTIVE}
    public enum Type {LECTURE, LAB, PROJECT}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "classroom", fetch = FetchType.LAZY)
    private Set<Quiz> quizzes = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "classroom", fetch = FetchType.EAGER)
    private Set<Document> documents = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private Classroom.Status status;

    @Enumerated(EnumType.STRING)
    private Classroom.Type type;

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "course_execution_id")
    private CourseExecution courseExecution;

    @Column(name = "available_date")
    private LocalDateTime availableDate;

    public Classroom(){
    }

    public Classroom(ClassroomDto classroomDto){


        checkInfo(classroomDto);
        this.type = Classroom.Type.valueOf(classroomDto.getType());
        this.title = classroomDto.getTitle();
        this.availableDate = DateHandler.toLocalDateTime(classroomDto.getAvailableDate());
        this.status = Status.INACTIVE;
    }

    public void checkInfo(ClassroomDto classroomDto){

        if (classroomDto.getTitle()==null || classroomDto.getTitle().isBlank()){
            throw new TutorException(INVALID_TITLE_FOR_CLASSROOM);
        }
        if (classroomDto.getType()==null || classroomDto.getType().trim().length() == 0){
            throw new TutorException(NO_TYPE);
        }

        if (!DateHandler.isValidDateFormat(classroomDto.getAvailableDate()))
            throw new TutorException(NO_DATE);
    }

    public void editClassroom(ClassroomDto classroomDto){
        checkInfo(classroomDto);
        this.type = Classroom.Type.valueOf(classroomDto.getType());
        this.title = classroomDto.getTitle();
        this.availableDate = DateHandler.toLocalDateTime(classroomDto.getAvailableDate());

        if (classroomDto.getStatus()==null || classroomDto.getStatus().trim().length() == 0){
            throw new TutorException(NO_CHANGED_STATUS);
        }
        this.status = Classroom.Status.valueOf(classroomDto.getStatus());
    }

    public LocalDateTime getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(LocalDateTime availableDate) {
        this.availableDate = availableDate;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Set<Quiz> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(Set<Quiz> quizzes) {
        this.quizzes = quizzes;
    }

    public void addQuiz(Quiz q){
        this.quizzes.add(q);
        q.setClassroom(this);
    }

    public Set<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<Document> documents) {
        this.documents = documents;
    }

    public void addDocument(Document d){
        this.documents.add(d);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void remove(){
        getDocuments().forEach(document -> document.setClassroom(null));
        getQuizzes().forEach(quiz -> quiz.setClassroom(null));
        getQuizzes().clear();
        getCourseExecution().getClassrooms().remove(this);
        setCourseExecution(null);
    }


    @Override
    public void accept(Visitor visitor) {
    }

    @Override
    public String toString() {
        return "Classroom{" +
                "id=" + id +
                ", quizzes=" + quizzes +
                ", documents=" + documents +
                ", status=" + status +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", courseExecution=" + courseExecution +
                ", availableDate=" + availableDate +
                '}';
    }
}
