package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Assessment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AssessmentDto implements Serializable {
    private Integer id;
    private Integer sequence;
    private Integer numberOfQuestions;
    private String title;
    private String status;
    private List<TopicConjunctionDto> topicConjunctions;

    public AssessmentDto() {
    }

    public AssessmentDto(Assessment assessment) {
        this.id = assessment.getId();
        this.sequence = assessment.getSequence();
        this.numberOfQuestions = assessment.getQuestions().size();
        this.title = assessment.getTitle();
        this.status = assessment.getStatus().name();
        this.topicConjunctions = assessment.getTopicConjunctions().stream().map(TopicConjunctionDto::new).collect(Collectors.toList());
    }

    public Integer getId() {
        return id;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Integer getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(Integer numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<TopicConjunctionDto> getTopicConjunctions() {
        return topicConjunctions;
    }

    public void setTopicConjunctions(List<TopicConjunctionDto> topicConjunctions) {
        this.topicConjunctions = topicConjunctions;
    }

    public void addTopicConjunction(TopicConjunctionDto topicConjunctionDto) {
        this.topicConjunctions.add(topicConjunctionDto);
    }

    public void setTopicConjunctionsFromUnit(TopicConjunctionDto topicConjunctionDto) {
        List<TopicConjunctionDto> temp = new ArrayList<>();
        temp.add(topicConjunctionDto);
        this.topicConjunctions = temp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AssessmentDto that = (AssessmentDto) o;

        if (!Objects.equals(id, that.id)) return false;
        if (!Objects.equals(sequence, that.sequence)) return false;
        if (!Objects.equals(title, that.title)) return false;
        if (!Objects.equals(status, that.status)) return false;
        return Objects.equals(topicConjunctions, that.topicConjunctions);
    }

    @Override
    public String toString() {
        return "AssessmentDto{" +
                "id=" + id +
                ", sequence=" + sequence +
                ", numberOfQuestions=" + numberOfQuestions +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                ", topicConjunctions=" + topicConjunctions +
                '}';
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (sequence != null ? sequence.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (topicConjunctions != null ? topicConjunctions.hashCode() : 0);
        return result;
    }
}