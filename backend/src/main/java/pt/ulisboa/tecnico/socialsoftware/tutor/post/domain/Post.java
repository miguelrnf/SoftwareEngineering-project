package pt.ulisboa.tecnico.socialsoftware.tutor.post.domain;

public class Post {
    private int id;
    private PostQuestion question;
    private Boolean postStatus;

    public Post(PostQuestion question, Boolean postStatus) {
        this.question = question;
        this.postStatus = postStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PostQuestion getQuestion() {
        return question;
    }

    public void setQuestion(PostQuestion question) {
        this.question = question;
    }

    public Boolean getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(Boolean postStatus) {
        this.postStatus = postStatus;
    }
}
