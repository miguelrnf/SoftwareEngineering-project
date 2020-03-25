package pt.ulisboa.tecnico.socialsoftware.tutor.post.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

public class PostPostUserDto {
    private PostDto postNotAnswered;
    private PostDto postAnswered;
    private UserDto userT;

    public PostPostUserDto() {
    }

    public PostDto getPostNotAnswered() {
        return postNotAnswered;
    }

    public void setPostNotAnswered(PostDto postNotAnswered) {
        this.postNotAnswered = postNotAnswered;
    }

    public PostDto getPostAnswered() {
        return postAnswered;
    }

    public void setPostAnswered(PostDto postAnswered) {
        this.postAnswered = postAnswered;
    }

    public UserDto getUserT() {
        return userT;
    }

    public void setUserT(UserDto userT) {
        this.userT = userT;
    }
}
