package pt.ulisboa.tecnico.socialsoftware.tutor.post.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.Post;

public class PostDto {
    private Integer id;
    private Integer key;
    private PostQuestionDto question;
    private Boolean postStatus;

    public PostDto() {
    }

    public PostDto(Post p) {
        this.id = p.getId();
        this.key = p.getKey();
        this.question = new PostQuestionDto(p.getQuestion());
        this.postStatus = p.getPostStatus();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public Boolean getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(Boolean postStatus) {
        this.postStatus = postStatus;
    }
}
