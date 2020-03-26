package pt.ulisboa.tecnico.socialsoftware.tutor.post.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.Post;
import java.io.Serializable;

public class PostDto implements Serializable {
    private Integer id;
    private Integer key;
    private PostQuestionDto question;
    private PostAnswerDto answer;
    private Boolean postStatus;
    private Boolean discussStatus;

    public PostDto() {
    }

    public PostDto(Post p) {
        this.id = p.getId();
        this.key = p.getKey();
        this.question = new PostQuestionDto(p.getQuestion());
        this.answer = p.getAnswer() != null ? new PostAnswerDto(p.getAnswer()) : null;
        this.postStatus = p.getPostStatus();
        this.discussStatus = p.getDiscussStatus();
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

    public PostQuestionDto getQuestion() {
        return question;
    }

    public void setQuestion(PostQuestionDto question) {
        this.question = question;
    }

    public PostAnswerDto getAnswer() {
        return answer;
    }

    public void setAnswer(PostAnswerDto answer) {
        this.answer = answer;
    }

    public Boolean getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(Boolean postStatus) {
        this.postStatus = postStatus;
    }

    public Boolean getDiscussStatus() {
        return discussStatus;
    }

    public void setDiscussStatus(Boolean discussStatus) {
        this.discussStatus = discussStatus;
    }
}
