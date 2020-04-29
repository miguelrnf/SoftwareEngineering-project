package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.TopicConjunction;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.dto.SuggestionDto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TopicConjunctionDto implements Serializable {
    private Integer id;
    private List<TopicDto> topics = new ArrayList<>();
    private List<SuggestionDto> suggestions = new ArrayList<>();

    public TopicConjunctionDto() {}

    public TopicConjunctionDto(TopicConjunction topicConjunction) {
        this.id = topicConjunction.getId();
        this.topics = topicConjunction.getTopics().stream().map(TopicDto::new).collect(Collectors.toList());
    }

    public List<SuggestionDto> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<SuggestionDto> suggestions) {
        this.suggestions = suggestions;
    }

    public Integer getId() {
        return id;
    }

    public List<TopicDto> getTopics() {
        return topics;
    }

    public void setTopics(List<TopicDto> topics) {
        this.topics = topics;
    }

    @Override
    public String toString() {
        return "TopicConjunctionDto{" +
                "id=" + id +
                ", topics=" + topics +
                '}';
    }

    public void addTopic(TopicDto topic) {
        this.topics.add(topic);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TopicConjunctionDto that = (TopicConjunctionDto) o;

        if (!Objects.equals(id, that.id)) return false;
        return Objects.equals(topics, that.topics);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (topics != null ? topics.hashCode() : 0);
        return result;
    }
}