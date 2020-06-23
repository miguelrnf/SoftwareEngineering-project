import { PostQuestion } from '@/models/management/PostQuestion';
import { PostAnswer } from '@/models/management/PostAnswer';
import { PostComment } from '@/models/management/PostComment';
import { PostAwardItem } from '@/models/management/PostAwardItem';
import { AwardsPerPost } from '@/models/management/AwardsPerPost';

export default class Post {
  id!: number;
  key: number | null = null;
  question!: PostQuestion;
  answer?: PostAnswer | null = null;
  comments!: PostComment[];
  postStatus!: boolean;
  discussStatus!: boolean;
  postPrivacy!: boolean;
  answerPrivacy!: boolean;
  upvotes!: number;
  downvotes!: number;
  awards!: AwardsPerPost[];

  constructor(jsonObj?: Post) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.key = jsonObj.key;
      this.question = jsonObj.question;
      this.answer = jsonObj.answer;
      this.postStatus = jsonObj.postStatus;
      this.discussStatus = jsonObj.discussStatus;
      this.postPrivacy = jsonObj.postPrivacy;
      this.answerPrivacy = jsonObj.answerPrivacy;
      this.upvotes = jsonObj.upvotes;
      this.downvotes = jsonObj.downvotes;

      if (jsonObj.comments != null)
        this.comments = jsonObj.comments.map(
          (comment: PostComment) => new PostComment(comment)
        );
      if (jsonObj.awards != null)
        this.awards = jsonObj.awards.map(
          (award: AwardsPerPost) => new AwardsPerPost(award)
        );
    }
  }
}
