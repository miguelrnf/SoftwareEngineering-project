import Post from '@/models/management/Post';
import Question from '@/models/management/Question';
import User from '@/models/user/User';

export class PostAnswer {
  id!: number;
  teacherAnswer: string = '';
  user!: User;
  post!: Post;

  constructor(jsonObj?: PostAnswer) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.teacherAnswer = jsonObj.teacherAnswer;
      this.user = jsonObj.user;
      this.post = jsonObj.post;
    }
  }
}
