import Assessment from '@/models/management/Assessment';
import { Student } from '@/models/management/Student';
import { ISOtoString } from '@/services/ConvertDateService';
import StatementQuiz from '@/models/statement/StatementQuiz';
import SolvedQuiz from '@/models/statement/SolvedQuiz';
import User from '@/models/user/User';

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
  owner!: User;
  enrolledStudents: Student[] = [];
  quiz!: StatementQuiz;
  completed!: boolean;
  solved!: SolvedQuiz;
  type!: string;

  constructor(jsonObj?: Tournament) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.number = jsonObj.number;
      this.title = jsonObj.title;
      this.numberOfQuestions = jsonObj.numberOfQuestions;
      this.status = jsonObj.status;
      this.completed = jsonObj.completed;
      this.type = jsonObj.type;

      if (jsonObj.creationDate)
        this.creationDate = ISOtoString(jsonObj.creationDate);
      if (jsonObj.availableDate)
        this.availableDate = ISOtoString(jsonObj.availableDate);
      if (jsonObj.conclusionDate)
        this.conclusionDate = ISOtoString(jsonObj.conclusionDate);

      if (jsonObj.assessmentDto)
        this.assessmentDto = new Assessment(jsonObj.assessmentDto);

      if (jsonObj.owner) this.owner = new User(jsonObj.owner);

      if (jsonObj.enrolledStudents) {
        this.enrolledStudents = jsonObj.enrolledStudents.map(
          (student: Student) => new Student(student)
        );
      }

      if (jsonObj.quiz) this.quiz = new StatementQuiz(jsonObj.quiz);

      if (jsonObj.solved) this.solved = new SolvedQuiz(jsonObj.solved);
    }
  }
}
