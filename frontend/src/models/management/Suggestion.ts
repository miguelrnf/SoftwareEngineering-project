import Topic from '@/models/management/Topic';
import User from '@/models/user/User';
import Option from '@/models/management/Option';

export default class Suggestion {
  id: number | null = null;
  title: string = '';
  hint: string = '';
  status: string = 'TOAPPROVE';
  creationDate!: string | null;
  teacherExplanation: string = '';
  student!: User | null;
  studentQuestion: string = '';
  isprivate: boolean = false;

  options: Option[] = [new Option(), new Option(), new Option(), new Option()];
  topicsList: Topic[] = [];

  constructor(jsonObj?: Suggestion) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.title = jsonObj.title;
      this.hint = jsonObj.hint;
      this.status = jsonObj.status;
      this.creationDate = jsonObj.creationDate;
      this.teacherExplanation = jsonObj.teacherExplanation;
      this.student = jsonObj.student;
      this.studentQuestion = jsonObj.studentQuestion;
      this.isprivate = jsonObj.isprivate;

      this.options = jsonObj.options.map(
        (option: Option) => new Option(option)
      );

      this.topicsList = jsonObj.topicsList.map(
        (topic: Topic) => new Topic(topic)
      );
    }
  }
}
