export class ShopItem {
  id!: number;
  name!: string;
  description!: string;
  icon!: string;
  color!: string;
  type!: string;
  content!: string;
  price!: number;

  constructor(jsonObj?: ShopItem) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.name = jsonObj.name;
      this.description = jsonObj.description;
      this.icon = jsonObj.icon;
      this.color = jsonObj.color;
      this.type = jsonObj.type;
      this.content = jsonObj.content;
      this.price = jsonObj.price;
    }
  }
}
