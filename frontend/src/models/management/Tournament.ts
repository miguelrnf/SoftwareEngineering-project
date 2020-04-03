import Assessment from '@/models/management/Assessment';
import User from '@/models/user/User';

export class Tournament {
  id!: number;
  number!: number;
  title!: string;
  creationDate!: string | undefined;
  availableDate!: string | undefined;
  conclusionDate!: string | undefined;
  numberOfQuestions!: number;
  status!: string;
  assessment!: Assessment;
  owner!: User;
  enrolledStudents: User[] = [];

  constructor(jsonObj?: Tournament) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.number = jsonObj.number;
      this.title = jsonObj.title;
      this.numberOfQuestions = jsonObj.numberOfQuestions;
      this.creationDate = jsonObj.creationDate;
      this.availableDate = jsonObj.availableDate;
      this.conclusionDate = jsonObj.conclusionDate;
      this.status = jsonObj.status;

      if (jsonObj.assessment)
        this.assessment = new Assessment(jsonObj.assessment);

      if (jsonObj.owner) this.owner = new User(jsonObj.owner);

      if (jsonObj.enrolledStudents) {
        this.enrolledStudents = jsonObj.enrolledStudents.map(
          (student: User) => new User(student)
        );
      }
    }
  }
}
