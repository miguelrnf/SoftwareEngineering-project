package pt.ulisboa.tecnico.socialsoftware.tutor.post.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.PostQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.io.Serializable;

public class PostQuestionDto implements Serializable {
    private Integer id;
    private QuestionDto question;
    private UserDto user;
    private String studentQuestion;
    private PostDto post;

    public PostQuestionDto() {
    }

    public PostQuestionDto(PostQuestion pq) {
        this.id = pq.getId();
        this.question = new QuestionDto(pq.getQuestion());
        this.user = new UserDto(pq.getUser());
        this.studentQuestion = pq.getStudentQuestion();
        this.post = new PostDto(pq.getPost(), true);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public QuestionDto getQuestion() {
        return question;
    }

    public void setQuestion(QuestionDto question) {
        this.question = question;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public String getStudentQuestion() {
        return studentQuestion;
    }

    public void setStudentQuestion(String studentQuestion) {
        this.studentQuestion = studentQuestion;
    }

    public PostDto getPost() {
        return post;
    }

    public void setPost(PostDto post) {
        this.post = post;
    }

    @Override
    public String toString() {
        return "PostQuestionDto{" +
                "id=" + id +
                ", question=" + question +
                ", user=" + user +
                ", studentQuestion='" + studentQuestion + '\'' +
                ", post=" + post +
                '}';
    }
}
