export default class EvalSettings {
    scale!: number;
    quizWeight!: number;
    tournamentWeight!: number;
    suggWeight!: number;

    constructor(jsonObj?: EvalSettings) {
        if (jsonObj) {
            this.scale = jsonObj.scale;
            this.quizWeight = jsonObj.quizWeight;
            this.tournamentWeight = jsonObj.tournamentWeight;
            this.suggWeight = jsonObj.suggWeight;
        }
    }
}