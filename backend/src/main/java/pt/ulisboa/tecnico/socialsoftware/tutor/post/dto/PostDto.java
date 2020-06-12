package pt.ulisboa.tecnico.socialsoftware.tutor.post.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.Post;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class PostDto implements Serializable {
    private Integer id;
    private Integer key;
    private PostQuestionDto question;
    private PostAnswerDto answer;
    private List<PostCommentDto> comments;
    private Boolean postStatus;
    private Boolean discussStatus;
    private Boolean postPrivacy;
    private Boolean answerPrivacy;
    private Integer upvotes;
    private Integer downvotes;

    public PostDto() {
    }

    public PostDto(Post p) {
        this.id = p.getId();
        this.key = p.getKey();
        this.question = p.getQuestion() != null ? new PostQuestionDto(p.getQuestion()) : null;
        this.answer = p.getAnswer() != null ? new PostAnswerDto(p.getAnswer()) : null;
        this.comments = p.getComments() != null ? p.getComments().stream()
                .map(x -> new PostCommentDto(x, true)).collect(Collectors.toList()) : null;
        this.postStatus = p.getPostStatus();
        this.discussStatus = p.getDiscussStatus();
        this.postPrivacy = p.getPostPrivacy();
        this.answerPrivacy = p.getAnswerPrivacy();
        this.upvotes = p.getUsersWhoUpvoted().size();
        this.downvotes = p.getUsersWhoDownvoted().size();
    }

    //Honestly? Im just too lazy to change the tests
    public PostDto(Post p, Boolean pq) {
        if(p != null) {
            if (p.getId() != null) {
                this.id = p.getId();
            } else this.id = null;
            if (p.getKey() != null) {
                this.key = p.getKey();
            } else this.key = null;
        }
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

    public List<PostCommentDto> getComments() {
        return comments;
    }

    public void setComments(List<PostCommentDto> comments) {
        this.comments = comments;
    }

    public Boolean getPostPrivacy() {
        return postPrivacy;
    }

    public void setPostPrivacy(Boolean postPrivacy) {
        this.postPrivacy = postPrivacy;
    }

    public Boolean getAnswerPrivacy() {
        return answerPrivacy;
    }

    public void setAnswerPrivacy(Boolean answerPrivacy) {
        this.answerPrivacy = answerPrivacy;
    }

    public Integer getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(Integer upvotes) {
        this.upvotes = upvotes;
    }

    public Integer getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(Integer downvotes) {
        this.downvotes = downvotes;
    }

    @Override
    public String toString() {
        return "PostDto{" +
                "id=" + id +
                ", key=" + key +
                ", question=" + question +
                ", answer=" + answer +
                ", comments=" + comments +
                ", postStatus=" + postStatus +
                ", discussStatus=" + discussStatus +
                '}';
    }
}
