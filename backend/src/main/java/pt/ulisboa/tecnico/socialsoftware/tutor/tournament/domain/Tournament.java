package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain;


import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_NOT_CONSISTENT;

@Entity
@Table(name = "tournaments",
        indexes = {
                @Index(name = "tournament_indx_0", columnList = "id")
        }
)
public class Tournament {

    public enum TournamentStatus {
        CREATED, OPEN, CLOSED, CANCELED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer numberOfQuestions;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "available_date")
    private LocalDateTime availableDate;

    @Column(name = "conclusion_date")
    private LocalDateTime conclusionDate;

    @Enumerated(EnumType.STRING)
    private TournamentStatus status;

    @ManyToOne
    @JoinColumn(name = "assessment_id")
    private Assessment assessment;

    @OneToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "course_execution_id")
    private CourseExecution courseExecution;

    @ManyToMany(mappedBy = "tournaments")
    private Set<User> enrolledStudents = new HashSet<>();

    public Tournament(){
    }

    public Tournament(TournamentDto tournamentDto, User user, Assessment assessment){

        setTitle(tournamentDto.getTitle());
        setStatus(Tournament.TournamentStatus.valueOf(tournamentDto.getStatus()));
        setCreationDate(DateHandler.toLocalDateTime(tournamentDto.getCreationDate()));
        setAvailableDate(DateHandler.toLocalDateTime(tournamentDto.getAvailableDate()));
        setConclusionDate(DateHandler.toLocalDateTime(tournamentDto.getConclusionDate()));
        this.owner = user;
        this.numberOfQuestions = tournamentDto.getNumberOfQuestions();
        this.assessment = assessment;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public TournamentStatus getStatus() {
        return status;
    }

    public User getOwner() {
        return owner;
    }

    public Set<User> getEnrolledStudents() {
        return enrolledStudents;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStatus(TournamentStatus status) {
        this.status = status;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setEnrolledStudents(Set<User> enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }

    public void enrollStudent(User user){
        enrolledStudents.add(user);
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(LocalDateTime availableDate) {
        checkAvailableDate(availableDate);
        this.availableDate = availableDate;
    }

    public LocalDateTime getConclusionDate() {
        return conclusionDate;
    }

    public void setConclusionDate(LocalDateTime conclusionDate) {
        checkConclusionDate(conclusionDate);
        this.conclusionDate = conclusionDate;
    }

    private void checkAvailableDate(LocalDateTime availableDate) {
        if (availableDate == null) {
            throw new TutorException(TOURNAMENT_NOT_CONSISTENT, "Available date");
        }
        if (this.conclusionDate != null && (conclusionDate.isBefore(availableDate) || availableDate.isBefore(DateHandler.now()))) {
            throw new TutorException(TOURNAMENT_NOT_CONSISTENT, "Available date");
        }
    }

    private void checkConclusionDate(LocalDateTime conclusionDate) {
        if (conclusionDate == null) {
            throw new TutorException(TOURNAMENT_NOT_CONSISTENT, "Conclusion date");
        }
        if ((conclusionDate.isBefore(availableDate) || conclusionDate.isBefore(DateHandler.now()))) {
            throw new TutorException(TOURNAMENT_NOT_CONSISTENT, "Conclusion date");
        }
    }

    public Integer getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(Integer numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }

    public void remove() {

        for(User s : this.enrolledStudents){
            s.getTournaments().remove(this);
        }
        this.enrolledStudents.clear();
        this.owner = null;
        this.assessment = null;

        courseExecution.getTournaments().remove(this);
        courseExecution = null;
    }

    public void generateQuiz(List<Question> questions, Quiz quiz) {
        this.setQuiz(quiz);
        IntStream.range(0,questions.size())
                .forEach(index -> new QuizQuestion(this.quiz, questions.get(index), index));

        this.quiz.setType("TOURNAMENT");
        this.quiz.setAvailableDate(this.availableDate);
        this.quiz.setConclusionDate(this.conclusionDate);
        this.quiz.setCreationDate(LocalDateTime.now());
        this.quiz.setTitle(this.title);
    }


    @Override
    public String toString() {
        return "Tournament{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", numberOfQuestions=" + numberOfQuestions +
                ", creationDate=" + creationDate +
                ", availableDate=" + availableDate +
                ", conclusionDate=" + conclusionDate +
                ", status=" + status +
                ", assessment=" + assessment +
                ", owner=" + owner +
                ", courseExecution=" + courseExecution +
                ", enrolledStudents=" + enrolledStudents +
                '}';
    }
}