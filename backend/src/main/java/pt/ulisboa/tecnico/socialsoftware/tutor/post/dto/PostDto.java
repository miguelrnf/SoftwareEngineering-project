package pt.ulisboa.tecnico.socialsoftware.tutor.post.dto;

public class PostDto {
    private int id;
    private PostQuestionDto question;
    private Boolean postStatus;

    public PostDto() {
    }

    public PostDto(PostQuestionDto question, Boolean postStatus) {
        this.question = question;
        this.postStatus = postStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
