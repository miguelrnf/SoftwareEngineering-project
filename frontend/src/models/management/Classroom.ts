import Document from '@/models/management/Document';
import { ISOtoString } from '@/services/ConvertDateService';

export default class Classroom {
  id!: number;
  title: string = '';
  type: string = '';
  status: string = '';
  documents: Document[] = [];
  quizzes: number[] = [];
  availableDate!: String | null;

  constructor(jsonObj?: Classroom) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.title = jsonObj.title;
      this.status = jsonObj.status;
      this.type = jsonObj.type;

      this.documents = jsonObj.documents.map(
        (doc: Document) => new Document(doc)
      );

      this.quizzes = jsonObj.quizzes;

      if (jsonObj.availableDate)
        this.availableDate = ISOtoString(jsonObj.availableDate as string);
    }
  }
}
