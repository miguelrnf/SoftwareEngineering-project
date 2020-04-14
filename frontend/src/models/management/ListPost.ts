import Post from '@/models/management/Post';

export default class ListPost {
  lists!: Post[];
  totalPosts!: number;
  perPage!: number;
  page!: number;

  constructor(jsonObj?: ListPost) {
    if (jsonObj) {
      this.totalPosts = jsonObj.totalPosts;
      this.perPage = jsonObj.perPage;
      this.page = jsonObj.page;

      this.lists = jsonObj.lists.map((post: Post) => new Post(post));
    }
  }
}
