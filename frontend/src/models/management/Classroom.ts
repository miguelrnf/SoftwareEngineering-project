import Document from "@/models/management/Document";
import {Quiz} from "@/models/management/Quiz";
import {ISOtoString} from "@/services/ConvertDateService";
import StatementQuiz from '@/models/statement/StatementQuiz';

export default class Classroom {
    id: number | null = null;
    title: string = '';
    type: string = '';
    status: string = '';
    documents: Document[] = [];
    quizzes: StatementQuiz[] = [];
    availableDate!: string;



    constructor(jsonObj?: Classroom) {
        if (jsonObj) {
            this.id = jsonObj.id;
            this.title = jsonObj.title;
            this.status = jsonObj.status
            this.type = jsonObj.type;

            this.documents = jsonObj.documents.map(
                (doc: Document) => new Document(doc));

            this.quizzes = jsonObj.quizzes.map(
                (quiz: StatementQuiz) => new StatementQuiz(quiz));

            if (jsonObj.availableDate)
                this.availableDate = ISOtoString(jsonObj.availableDate);
        }
    }
}