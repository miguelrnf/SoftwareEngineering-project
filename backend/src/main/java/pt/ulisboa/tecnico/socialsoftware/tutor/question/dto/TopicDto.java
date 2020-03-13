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

    public TopicDto(TopicDto topicDto) {
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
        return Objects.equals(id, topicDto.id) &&
                Objects.equals(name, topicDto.name) &&
                Objects.equals(parentTopic, topicDto.parentTopic) &&
                Objects.equals(numberOfQuestions, topicDto.numberOfQuestions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, parentTopic, numberOfQuestions);
    }
}
