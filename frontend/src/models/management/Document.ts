export default class Document {
  id: number | null = null;
  type: string = '';
  title: string = '';
  content: string = '';
  url: string = '';
  classroomId: number | null = null;
  pdf: string | undefined;
  extension: string = '';

  constructor(jsonObj?: Document) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.title = jsonObj.title;
      this.type = jsonObj.type;
      this.content = jsonObj.content;
      this.url = jsonObj.url;
      this.classroomId = jsonObj.classroomId;
      this.pdf = jsonObj.pdf;
      this.extension = jsonObj.extension;
    }
  }
}
