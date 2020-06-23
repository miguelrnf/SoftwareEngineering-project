export class UserItem {
  id!: number;
  name!: string;
  description!: string;
  icon!: string;
  color!: string;
  userId!: number;

  constructor(jsonObj?: UserItem) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.name = jsonObj.name;
      this.description = jsonObj.description;
      this.color = jsonObj.color;
      this.userId = jsonObj.userId;
    }
  }
}
