import Image from "@/models/management/Image";

export default class Document {
  id: number | null = null;
  type: string = '';
  title: string = '';
  content: string = '';
  image: Image | null = null;
  url: string = '';
  classroomId: number | null = null;
  pdf: string | undefined;


  constructor(jsonObj?: Document) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.title = jsonObj.title;
      this.type = jsonObj.type;
      this.content = jsonObj.content;
      this.image = jsonObj.image;
      this.url = jsonObj.url;
      this.classroomId = jsonObj.classroomId;
      this.pdf = jsonObj.pdf;
    }
  }
}
