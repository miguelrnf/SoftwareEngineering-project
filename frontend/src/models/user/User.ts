import Course from '@/models/user/Course';
import Post from '@/models/management/Post';
import { PostComment } from '@/models/management/PostComment';

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
  dashboardPrivate: boolean = false;
  numberofsuggestions!: number;
  numberofsuggestionsapproved!: number;
  postsUpvoted: Post[] | null = null;
  postsDownvoted: Post[] | null = null;

  constructor(jsonObj?: User) {
    if (jsonObj) {
      this.name = jsonObj.name;
      this.username = jsonObj.username;
      this.role = jsonObj.role;
      this.score = jsonObj.score;
      this.dashboardPrivate = jsonObj.dashboardPrivate;
      this.numberofsuggestions = jsonObj.numberofsuggestions;
      this.numberofsuggestionsapproved = jsonObj.numberofsuggestionsapproved;

      if (jsonObj.courses) {
        for (let [name, courses] of Object.entries(jsonObj.courses)) {
          this.courses[name] = courses.map(course => new Course(course));
          this.coursesNumber += this.courses[name].length;
        }
      }
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
