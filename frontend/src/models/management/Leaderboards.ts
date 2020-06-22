import User from '@/models/user/User';

export class Leaderboards {
  bestScores: User[] | null = [];
  mostApprovedSuggestions: User[] | null = [];;
  mostPosts: User[]| null = [];;
  mostQuizzesSolved: User[]| null = [];;
  mostTournamentsParticipated: User[]| null = [];;

  constructor(jsonObj?: Leaderboards) {
    if (jsonObj) {
      if (jsonObj.bestScores) {
        this.bestScores = jsonObj.bestScores.map(
          (user: User) => new User(user)
        );
      }
      if (jsonObj.mostApprovedSuggestions) {
        this.mostApprovedSuggestions = jsonObj.mostApprovedSuggestions.map(
          (user: User) => new User(user)
        );
      }
      if (jsonObj.mostPosts) {
        this.mostPosts = jsonObj.mostPosts.map((user: User) => new User(user));
      }
      if (jsonObj.mostQuizzesSolved) {
        this.mostQuizzesSolved = jsonObj.mostQuizzesSolved.map(
          (user: User) => new User(user)
        );
      }
      if (jsonObj.mostTournamentsParticipated) {
        this.mostTournamentsParticipated = jsonObj.mostTournamentsParticipated.map(
          (user: User) => new User(user)
        );
      }
    }
  }
}
