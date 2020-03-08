package pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "suggestions",
        indexes = {
                @Index(name = "question_indx_0", columnList = "key")
        })
public class Suggestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int _id;

    @Column(unique=true, nullable = false)
    private Integer key;

    @Column(columnDefinition = "TEXT")
    private String _questionStr;

    @ManyToMany(mappedBy = "questions")
    private Set<Topic> _topicsList = new HashSet<>();

    @Column(name = "changed_status", columnDefinition = "boolean default false")
    private Boolean _changed = false;

    @Column(columnDefinition = "JUSTIFICATION")
    private String _justification;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    public enum Status {
        TOAPPROVE, APPROVED, REJECTED
    }
    @Enumerated(EnumType.STRING)
    private Suggestion.Status status = Suggestion.Status.TOAPPROVE;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;


}
