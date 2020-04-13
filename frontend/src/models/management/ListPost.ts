import { PostQuestion } from '@/models/management/PostQuestion';
import { PostAnswer } from '@/models/management/PostAnswer';
import Post from '@/models/management/Post';
import Option from '@/models/management/Option';

export default class ListPost {
  lists?: Post[] = [];
  totalPosts?: number | null = null;
  perPage!: number;
  page!: number;

  constructor(jsonObj?: ListPost) {
    if (jsonObj) {
      this.totalPosts = jsonObj.totalPosts;
      this.perPage = jsonObj.perPage;
      this.page = jsonObj.page;

      this.lists = jsonObj.lists;
    }
  }
}
