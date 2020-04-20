package pt.ulisboa.tecnico.socialsoftware.tutor.post.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.post.domain.Post;

import java.util.List;

public class ListPostsDto {
    private List<PostDto> lists;
    private int totalPosts;
    private int perPage;
    private int page;

    public ListPostsDto() {
    }

    public ListPostsDto(List<PostDto> lists, int totalPosts, int perPage, int page) {
        this.lists = lists;
        this.totalPosts = totalPosts;
        this.perPage = perPage;
        this.page = page;
    }

    public List<PostDto> getLists() {
        return lists;
    }

    public void setLists(List<PostDto> lists) {
        this.lists = lists;
    }

    public int getTotalPosts() {
        return totalPosts;
    }

    public void setTotalPosts(int totalPosts) {
        this.totalPosts = totalPosts;
    }

    public int getPerPage() {
        return perPage;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
