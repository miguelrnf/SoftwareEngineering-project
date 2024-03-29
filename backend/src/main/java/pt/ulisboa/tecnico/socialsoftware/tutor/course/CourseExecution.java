package pt.ulisboa.tecnico.socialsoftware.tutor.course;

import pt.ulisboa.tecnico.socialsoftware.tutor.classroom.domain.Classroom;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.domain.Suggestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "course_executions")
public class CourseExecution implements DomainEntity {
     public enum Status {ACTIVE, INACTIVE, HISTORIC}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Course.Type type;

    private String acronym;
    private String academicTerm;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToMany(mappedBy = "courseExecutions", fetch=FetchType.LAZY)
    private final Set<User> users = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "courseExecution", fetch=FetchType.LAZY, orphanRemoval=true)
    private final Set<Quiz> quizzes = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "courseExecution", fetch=FetchType.LAZY, orphanRemoval=true)
    private final Set<Assessment> assessments = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "courseExecution", fetch = FetchType.LAZY, orphanRemoval = true)
    private final Set<Tournament> tournaments = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "courseExecution", fetch = FetchType.LAZY, orphanRemoval = true)
    private final Set<Classroom> classrooms = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "courseExecution", fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Suggestion> suggestions = new HashSet<>();


    private Integer scale;
    private Integer quizWeight;
    private Integer suggWeight;
    private Integer tournamentWeight;




    public CourseExecution() {
    }

    public CourseExecution(Course course, String acronym, String academicTerm, Course.Type type) {
        if (course.existsCourseExecution(acronym, academicTerm, type)) {
            throw new TutorException(DUPLICATE_COURSE_EXECUTION, acronym + academicTerm);
        }

        setType(type);
        setCourse(course);
        setAcronym(acronym);
        setAcademicTerm(academicTerm);
        setStatus(Status.ACTIVE);

        this.scale = 20;
        this.quizWeight = 80;
        this.suggWeight = 10;
        this.tournamentWeight = 10;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    public Integer getQuizWeight() {
        return quizWeight;
    }

    public void setQuizWeight(Integer quizWeight) {
        this.quizWeight = quizWeight;
    }

    public Integer getSuggWeight() {
        return suggWeight;
    }

    public void setSuggWeight(Integer suggWeight) {
        this.suggWeight = suggWeight;
    }

    public Integer getTournamentWeight() {
        return tournamentWeight;
    }

    public void setTournamentWeight(Integer tournamentWeight) {
        this.tournamentWeight = tournamentWeight;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitCourseExecution(this);
    }

    public Set<Classroom> getClassrooms() {
        return classrooms;
    }

    public void addClassroom(Classroom classroom){
        this.classrooms.add(classroom);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Course.Type getType() {
        return type;
    }

    public void setType(Course.Type type) {
        if (type == null)
            throw new TutorException(INVALID_TYPE_FOR_COURSE_EXECUTION);
        this.type = type;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        if (acronym == null || acronym.trim().isEmpty()) {
        throw new TutorException(INVALID_ACRONYM_FOR_COURSE_EXECUTION);
    }
        this.acronym = acronym;
    }

    public String getAcademicTerm() {
        return academicTerm;
    }

    public void setAcademicTerm(String academicTerm) {
        if (academicTerm == null || academicTerm.isBlank())
            throw new TutorException(INVALID_ACADEMIC_TERM_FOR_COURSE_EXECUTION);

        this.academicTerm = academicTerm;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
        course.addCourseExecution(this);
    }

    public Set<User> getUsers() {
        return users;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public Set<Quiz> getQuizzes() {
        return quizzes;
    }

    public void addQuiz(Quiz quiz) {
        quizzes.add(quiz);
    }

    public Set<Assessment> getAssessments() {
        return assessments;
    }

    public void addAssessment(Assessment assessment) {
        assessments.add(assessment);
    }

    public void remove() {
        if (!getQuizzes().isEmpty() || !getAssessments().isEmpty()) {
            throw new TutorException(CANNOT_DELETE_COURSE_EXECUTION, acronym + academicTerm);
        }

        course.getCourseExecutions().remove(this);
        users.forEach(user -> user.getCourseExecutions().remove(this));
    }


    public Set<Tournament> getTournaments() {
        return tournaments;
    }

    public void addTournament(Tournament tournament) {
        this.tournaments.add(tournament);
    }

    public void addSuggestion(Suggestion suggestion) {this.suggestions.add(suggestion); }

    public Set<Suggestion> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(Set<Suggestion> suggestions) {
        this.suggestions = suggestions;
    }

    @Override
    public String toString() {
        return "CourseExecution{" +
                "id=" + id +
                ", type=" + type +
                ", acronym='" + acronym + '\'' +
                ", academicTerm='" + academicTerm + '\'' +
                ", status=" + status +
                ", users=" + users +
                ", quizzes=" + quizzes +
                ", assessments=" + assessments +
                ", suggestions=" + suggestions +
                '}';
    }


}