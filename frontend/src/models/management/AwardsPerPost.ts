import { PostAwardItem } from '@/models/management/PostAwardItem';

export class AwardsPerPost {
  award!: PostAwardItem;
  number!: number;

  constructor(jsonObj?: AwardsPerPost) {
    if (jsonObj) {
      this.award = jsonObj.award;
      this.number = jsonObj.number;
    }
  }
}
