import Post from '@/models/management/Post';
import Question from '@/models/management/Question';
import User from '@/models/user/User';

export class PostQuestion {
  id!: number;
  question: Question | null = null;
  studentQuestion!: string;
  user!: User;
  post!: Post;

  constructor(jsonObj?: PostQuestion) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.question = jsonObj.question;
      this.studentQuestion = jsonObj.studentQuestion;
      this.user = jsonObj.user;
      this.post = jsonObj.post;
    }
  }
}
