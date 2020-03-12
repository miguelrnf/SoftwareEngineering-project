package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;


import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;

import java.io.Serializable;
import java.util.Objects;

public class TopicDto implements Serializable {
    private Integer id;
    private String name;
    private String parentTopic;
    private Integer numberOfQuestions;

    public TopicDto() {
    }

    public TopicDto(Topic topic) {
        this.id = topic.getId();
        this.name = topic.getName();
        if (topic.getParentTopic() != null) {
            this.parentTopic = topic.getParentTopic().getName();
        }
        this.numberOfQuestions = topic.getQuestions().size();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentTopic() {
        return parentTopic;
    }

    public void setParentTopic(String parentTopic) {
        this.parentTopic = parentTopic;
    }

    public Integer getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(Integer numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    @Override
    public String toString() {
        return "TopicDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", parentTopic='" + parentTopic + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TopicDto topicDto = (TopicDto) o;

        if (!Objects.equals(id, topicDto.id)) return false;
        if (!Objects.equals(name, topicDto.name)) return false;
        if (!Objects.equals(parentTopic, topicDto.parentTopic))
            return false;
        return Objects.equals(numberOfQuestions, topicDto.numberOfQuestions);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (parentTopic != null ? parentTopic.hashCode() : 0);
        result = 31 * result + (numberOfQuestions != null ? numberOfQuestions.hashCode() : 0);
        return result;
    }
}
