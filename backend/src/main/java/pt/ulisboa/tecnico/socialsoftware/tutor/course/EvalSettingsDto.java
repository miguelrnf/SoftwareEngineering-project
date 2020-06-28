package pt.ulisboa.tecnico.socialsoftware.tutor.course;

import java.io.Serializable;

public class EvalSettingsDto implements Serializable {
    private int scale;
    private int quizWeight;
    private int tournamentWeight;
    private int suggWeight;

    public EvalSettingsDto(){
    }

    public EvalSettingsDto(int scale, int quizWeight, int tournamentWeight,int suggWeight) {
        this.scale = scale;
        this.quizWeight = quizWeight;
        this.suggWeight = suggWeight;
        this.tournamentWeight = tournamentWeight;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public int getQuizWeight() {
        return quizWeight;
    }

    public void setQuizWeight(int quizWeight) {
        this.quizWeight = quizWeight;
    }

    public int getSuggWeight() {
        return suggWeight;
    }

    public void setSuggWeight(int suggWeight) {
        this.suggWeight = suggWeight;
    }

    public int getTournamentWeight() {
        return tournamentWeight;
    }

    public void setTournamentWeight(int tournamentWeight) {
        this.tournamentWeight = tournamentWeight;
    }
}
