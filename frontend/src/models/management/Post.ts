import { PostQuestion } from '@/models/management/PostQuestion';
import { PostAnswer } from '@/models/management/PostAnswer';
import { PostComment } from '@/models/management/PostComment';

export default class Post {
  id!: number;
  key: number | null = null;
  question!: PostQuestion;
  answer?: PostAnswer | null = null;
  comments!: PostComment[];
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

      if (jsonObj.comments != null)
        this.comments = jsonObj.comments.map(
          (comment: PostComment) => new PostComment(comment)
        );
    }
  }
}
