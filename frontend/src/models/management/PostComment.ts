import Post from '@/models/management/Post';
import User from '@/models/user/User';

export class PostComment {
  id!: number;
  key!: number;
  user!: User;
  post: Post = new Post();
  creationDate!: string;
  comment!: string;
  parent: PostComment | null = null;
  children: PostComment[] = [];

  constructor(jsonObj?: PostComment) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.key = jsonObj.key;
      this.creationDate = jsonObj.creationDate;
      this.comment = jsonObj.comment;
      this.parent = jsonObj.parent;
      if (jsonObj.children != null)
        this.children = jsonObj.children.map(
          (comment: PostComment) => new PostComment(comment)
        );
      this.user = jsonObj.user;
      this.post.id = jsonObj.post.id;
    }
  }
}
