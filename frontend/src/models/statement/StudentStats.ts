export default class StudentStats {
  totalQuizzes!: number;
  totalAnswers!: number;
  totalUniqueQuestions!: number;
  correctAnswers!: number;
  improvedCorrectAnswers!: number;
  totalAvailableQuestions!: number;

  uniqueCorrectAnswers!: number;
  uniqueWrongAnswers!: number;
  approveSuggestions!: number;
  rejectedSuggestions!: number;
  pendingSuggestions!: number;
  tournamentDone!: number;
  postSubmitted!: number;

  constructor(jsonObj?: StudentStats) {
    if (jsonObj) {
      this.totalQuizzes = jsonObj.totalQuizzes;
      this.totalAnswers = jsonObj.totalAnswers;
      this.totalUniqueQuestions = jsonObj.totalUniqueQuestions;
      this.correctAnswers = jsonObj.correctAnswers;
      this.improvedCorrectAnswers = jsonObj.improvedCorrectAnswers;
      this.uniqueCorrectAnswers = jsonObj.uniqueCorrectAnswers;
      this.uniqueWrongAnswers = jsonObj.uniqueWrongAnswers;
      this.totalAvailableQuestions = jsonObj.totalAvailableQuestions;
      this.approveSuggestions = jsonObj.approveSuggestions;
      this.pendingSuggestions = jsonObj.pendingSuggestions;
      this.rejectedSuggestions = jsonObj.rejectedSuggestions;
      this.tournamentDone = jsonObj.tournamentDone;
      this.postSubmitted = jsonObj.postSubmitted;
    }
  }
}
