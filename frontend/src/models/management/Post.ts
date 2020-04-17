import { PostQuestion } from '@/models/management/PostQuestion';
import { PostAnswer } from '@/models/management/PostAnswer';

export default class Post {
  id!: number;
  key: number | null = null;
  question!: PostQuestion;
  answer?: PostAnswer | null = null;
  postStatus!: boolean;
  discussStatus!: boolean;

  constructor(jsonObj?: Post) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.key = jsonObj.key;
      this.question = jsonObj.question;
      this.answer = jsonObj.answer;
      this.postStatus = jsonObj.postStatus;
      this.discussStatus = jsonObj.discussStatus;
    }
  }
}
