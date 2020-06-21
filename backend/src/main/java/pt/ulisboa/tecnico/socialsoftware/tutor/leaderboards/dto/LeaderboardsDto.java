package pt.ulisboa.tecnico.socialsoftware.tutor.leaderboards.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.util.List;

public class LeaderboardsDto {
    private List<UserDto> bestScores;
    private List<UserDto> mostApprovedSuggestions;
    private List<UserDto> mostPosts;
    private List<UserDto> mostQuizzesSolved;
    private List<UserDto> mostTournamentsParticipated;

    public LeaderboardsDto(List<UserDto> bestScores,
                           List<UserDto> mostApprovedSuggestions,
                           List<UserDto> mostPosts,
                           List<UserDto> mostQuizzesSolved,
                           List<UserDto> mostTournamentsParticipated) {
        this.bestScores = bestScores;
        this.mostApprovedSuggestions = mostApprovedSuggestions;
        this.mostPosts = mostPosts;
        this.mostQuizzesSolved = mostQuizzesSolved;
        this.mostTournamentsParticipated = mostTournamentsParticipated;
    }

    public LeaderboardsDto() {
    }

    public List<UserDto> getBestScores() {
        return bestScores;
    }

    public void setBestScores(List<UserDto> bestScores) {
        this.bestScores = bestScores;
    }

    public List<UserDto> getMostApprovedSuggestions() {
        return mostApprovedSuggestions;
    }

    public void setMostApprovedSuggestions(List<UserDto> mostApprovedSuggestions) {
        this.mostApprovedSuggestions = mostApprovedSuggestions;
    }

    public List<UserDto> getMostPosts() {
        return mostPosts;
    }

    public void setMostPosts(List<UserDto> mostPosts) {
        this.mostPosts = mostPosts;
    }

    public List<UserDto> getMostQuizzesSolved() {
        return mostQuizzesSolved;
    }

    public void setMostQuizzesSolved(List<UserDto> mostQuizzesSolved) {
        this.mostQuizzesSolved = mostQuizzesSolved;
    }

    public List<UserDto> getMostTournamentsParticipated() {
        return mostTournamentsParticipated;
    }

    public void setMostTournamentsParticipated(List<UserDto> mostTournamentsParticipated) {
        this.mostTournamentsParticipated = mostTournamentsParticipated;
    }

    @Override
    public String toString() {
        return "LeaderboardsDto{" +
                "bestScores=" + bestScores +
                ", mostApprovedSuggestions=" + mostApprovedSuggestions +
                ", mostPosts=" + mostPosts +
                ", mostQuizzesSolved=" + mostQuizzesSolved +
                ", mostTournamentsParticipated=" + mostTournamentsParticipated +
                '}';
    }
}
