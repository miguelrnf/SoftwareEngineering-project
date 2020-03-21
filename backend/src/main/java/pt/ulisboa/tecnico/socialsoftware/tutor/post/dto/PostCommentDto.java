package pt.ulisboa.tecnico.socialsoftware.tutor.post.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.PostComment;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class PostCommentDto {
    private Integer id;
    private Integer key;
    private UserDto user;
    private PostDto post;
    private LocalDateTime creationDate;
    private String comment;
    private PostCommentDto parent;
    private Set<PostCommentDto> children = new HashSet<>();

    public PostCommentDto() {
    }

    public PostCommentDto(PostComment pc, boolean isChild) {
        this.id = pc.getId();
        this.key = pc.getKey();
        this.user = new UserDto(pc.getUser());
        this.post = new PostDto(pc.getPost());
        this.creationDate = pc.getCreationDate();
        this.comment = pc.getComment();
        if(pc.getParent() != null && !isChild)
            this.parent = new PostCommentDto(pc.getParent(), true);
        if(!pc.getChildren().isEmpty() && !isChild)
            this.children = pc.getChildren().stream().map(x -> new PostCommentDto(x, true)).collect(Collectors.toSet());
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

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public PostDto getPost() {
        return post;
    }

    public void setPost(PostDto post) {
        this.post = post;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public PostCommentDto getParent() {
        return parent;
    }

    public void setParent(PostCommentDto parent) {
        this.parent = parent;
    }

    public Set<PostCommentDto> getChildren() {
        return children;
    }

    public void setChildren(Set<PostCommentDto> children) {
        this.children = children;
    }
}
