export class UserItem {
  id!: number;
  name!: string;
  description!: string;
  icon!: string;
  color!: string;

  constructor(jsonObj?: UserItem) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.name = jsonObj.name;
      this.description = jsonObj.description;
      this.icon = jsonObj.icon;
      this.color = jsonObj.color;
    }
  }
}
