import User from '@/models/user/User';
import Post from '@/models/management/Post';

export class Leaderboards {
  bestScores: User[]  = [];
  mostApprovedSuggestions: User[]  = [];
  mostPosts: User[] = [];
  mostQuizzesSolved: User[] = [];
  mostTournamentsParticipated: User[] = [];
  mostUpvotedPosts: Post[] = [];

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
      if (jsonObj.mostUpvotedPosts) {
        this.mostUpvotedPosts = jsonObj.mostUpvotedPosts.map(
          (post: Post) => new Post(post)
        );
      }
    }
  }
}
