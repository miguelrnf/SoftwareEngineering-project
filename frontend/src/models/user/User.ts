import Course from '@/models/user/Course';
import Post from '@/models/management/Post';

interface CourseMap {
  [key: string]: Course[];
}

export default class User {
  name!: string;
  username!: string;
  role!: string;
  courses: CourseMap = {};
  coursesNumber: number = 0;
  score!: number;
  currentTheme!: string;
  dashboardPrivate: boolean = false;
  numberofsuggestions!: number;
  numberofsuggestionsapproved!: number;
  numberOfQuizzesSolved!: number;
  numberOfPostsSubmitted!: number;
  numberOfPTournamentsParticipated!: number;
  postsUpvoted: Post[] | null = null;
  postsDownvoted: Post[] | null = null;
  grade!: number;

  constructor(jsonObj?: User) {
    if (jsonObj) {
      this.name = jsonObj.name;
      this.username = jsonObj.username;
      this.role = jsonObj.role;
      this.score = jsonObj.score;
      this.currentTheme = jsonObj.currentTheme;
      this.dashboardPrivate = jsonObj.dashboardPrivate;
      this.numberofsuggestions = jsonObj.numberofsuggestions;
      this.numberofsuggestionsapproved = jsonObj.numberofsuggestionsapproved;
      this.numberOfQuizzesSolved = jsonObj.numberOfQuizzesSolved;
      this.numberOfPostsSubmitted = jsonObj.numberOfPostsSubmitted;
      this.numberOfPTournamentsParticipated =
        jsonObj.numberOfPTournamentsParticipated;

      if (jsonObj.courses) {
        for (let [name, courses] of Object.entries(jsonObj.courses)) {
          this.courses[name] = courses.map(course => new Course(course));
          this.coursesNumber += this.courses[name].length;
        }
      }
      this.grade = jsonObj.grade;

      if (jsonObj.postsUpvoted != null) {
        this.postsUpvoted = jsonObj.postsUpvoted.map(
          (postsUpvoted: Post) => new Post(postsUpvoted)
        );
      }
      if (jsonObj.postsDownvoted != null) {
        this.postsDownvoted = jsonObj.postsDownvoted.map(
          (postsDownvoted: Post) => new Post(postsDownvoted)
        );
      }
    }
  }
}
