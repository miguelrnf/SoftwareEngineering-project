import Assessment from '@/models/management/Assessment';
import { Student } from '@/models/management/Student';

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
  owner!: Student;
  enrolledStudents: Student[] = [];

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

      if (jsonObj.owner) this.owner = new Student(jsonObj.owner);

      if (jsonObj.enrolledStudents) {
        this.enrolledStudents = jsonObj.enrolledStudents.map(
          (student: Student) => new Student(student)
        );
      }
    }
  }
}
