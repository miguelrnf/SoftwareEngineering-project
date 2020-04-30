import Assessment from '@/models/management/Assessment';
import { Student } from '@/models/management/Student';
import { ISOtoString } from '@/services/ConvertDateService';

export class Tournament {
  id!: number;
  number!: number;
  title!: string;
  creationDate!: string;
  availableDate!: string;
  conclusionDate!: string;
  numberOfQuestions!: number;
  status!: string;
  assessmentDto: Assessment = new Assessment();
  owner!: Student;
  enrolledStudents: Student[] = [];

  constructor(jsonObj?: Tournament) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.number = jsonObj.number;
      this.title = jsonObj.title;
      this.numberOfQuestions = jsonObj.numberOfQuestions;
      this.status = jsonObj.status;

      if (jsonObj.creationDate)
        this.creationDate = ISOtoString(jsonObj.creationDate);
      if (jsonObj.availableDate)
        this.availableDate = ISOtoString(jsonObj.availableDate);
      if (jsonObj.conclusionDate)
        this.conclusionDate = ISOtoString(jsonObj.conclusionDate);

      if (jsonObj.assessmentDto)
        this.assessmentDto = new Assessment(jsonObj.assessmentDto);

      if (jsonObj.owner) this.owner = new Student(jsonObj.owner);

      if (jsonObj.enrolledStudents) {
        this.enrolledStudents = jsonObj.enrolledStudents.map(
          (student: Student) => new Student(student)
        );
      }
    }
  }
}
