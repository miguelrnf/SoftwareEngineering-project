package pt.ulisboa.tecnico.socialsoftware.tutor.statistics;

import java.io.Serializable;

public class StatsDto implements Serializable {
    private Integer totalQuizzes = 0;
    private Integer totalAnswers = 0;
    private Integer totalUniqueQuestions = 0;
    private float correctAnswers = 0;
    private float improvedCorrectAnswers = 0;
    private Integer uniqueCorrectAnswers = 0;
    private Integer uniqueWrongAnswers = 0;
    private Integer totalAvailableQuestions = 0;
    private Integer approveSuggestions = 0 ;
    private Integer rejectedSuggestions = 0 ;
    private Integer pendingSuggestions = 0 ;
    private Integer tournamentDone = 0;
    private Integer postSubmitted = 0;




    public Integer getTotalQuizzes() {
        return totalQuizzes;
    }

    public Integer getTournamentDone() {
        return tournamentDone;
    }

    public Integer getPostSubmitted() {
        return postSubmitted;
    }

    public void setPostSubmitted(Integer postSubmitted) {
        this.postSubmitted = postSubmitted;
    }

    public void setTournamentDone(Integer tournamentDone) {
        this.tournamentDone = tournamentDone;
    }

    public void setTotalQuizzes(Integer totalQuizzes) {
        this.totalQuizzes = totalQuizzes;
    }

    public Integer getTotalAnswers() {
        return totalAnswers;
    }

    public void setTotalAnswers(Integer totalAnswers) {
        this.totalAnswers = totalAnswers;
    }

    public Integer getTotalUniqueQuestions() {
        return totalUniqueQuestions;
    }

    public void setTotalUniqueQuestions(Integer totalUniqueQuestions) {
        this.totalUniqueQuestions = totalUniqueQuestions;
    }

    public float getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(float correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public float getImprovedCorrectAnswers() {
        return improvedCorrectAnswers;
    }

    public void setImprovedCorrectAnswers(float improvedCorrectAnswers) {
        this.improvedCorrectAnswers = improvedCorrectAnswers;
    }

    public Integer getUniqueCorrectAnswers() {
        return uniqueCorrectAnswers;
    }

    public void setUniqueCorrectAnswers(Integer uniqueCorrectAnswers) {
        this.uniqueCorrectAnswers = uniqueCorrectAnswers;
    }

    public Integer getUniqueWrongAnswers() {
        return uniqueWrongAnswers;
    }

    public void setUniqueWrongAnswers(Integer uniqueWrongAnswers) {
        this.uniqueWrongAnswers = uniqueWrongAnswers;
    }

    public Integer getTotalAvailableQuestions() {
        return totalAvailableQuestions;
    }

    public void setTotalAvailableQuestions(Integer totalAvailableQuestions) {
        this.totalAvailableQuestions = totalAvailableQuestions;
    }

    public Integer getApproveSuggestions() {
        return approveSuggestions;
    }

    public void setApproveSuggestions(Integer approveSuggestions) {
        this.approveSuggestions = approveSuggestions;
    }

    public Integer getRejectedSuggestions() {
        return rejectedSuggestions;
    }

    public void setRejectedSuggestions(Integer rejectedSuggestions) {
        this.rejectedSuggestions = rejectedSuggestions;
    }

    public Integer getPendingSuggestions() {
        return pendingSuggestions;
    }

    public void setPendingSuggestions(Integer pendingSuggestions) {
        this.pendingSuggestions = pendingSuggestions;
    }

    @Override
    public String toString() {
        return "StatsDto{" +
                "totalQuizzes=" + totalQuizzes +
                ", totalAnswers=" + totalAnswers +
                ", totalUniqueQuestions=" + totalUniqueQuestions +
                ", correctAnswers=" + correctAnswers +
                ", improvedCorrectAnswers=" + improvedCorrectAnswers +
                ", uniqueCorrectAnswers=" + uniqueCorrectAnswers +
                ", uniqueWrongAnswers=" + uniqueWrongAnswers +
                '}';
    }
}
