import { UserItem } from '@/models/management/UserItem';

export class PostAwardItem {
  type!: string;
  item!: UserItem;

  constructor(jsonObj?: PostAwardItem) {
    if (jsonObj) {
      this.type = jsonObj.type;
      this.item = jsonObj.item;
    }
  }
}
