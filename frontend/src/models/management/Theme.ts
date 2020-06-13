import Assessment from '@/models/management/Assessment';
import { Student } from '@/models/management/Student';
import { ISOtoString } from '@/services/ConvertDateService';
import StatementQuiz from '@/models/statement/StatementQuiz';
import SolvedQuiz from '@/models/statement/SolvedQuiz';
import User from '@/models/user/User';

export class Theme {
  id!: number;
  name!: string;
  description!: string;
  icon!: string;
  color!: string;
  success!: string;
  secondary!: string;
  accent!: string;
  info!: string;
  warning!: string;
  primary!: string;
  error!: string;
  dark!: boolean;

  constructor(jsonObj?: Theme) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.name = jsonObj.name;
      this.description = jsonObj.description;
      this.icon = jsonObj.icon;
      this.color = jsonObj.color;
      this.success = jsonObj.success;
      this.secondary = jsonObj.secondary;
      this.accent = jsonObj.accent;
      this.info = jsonObj.info;
      this.warning = jsonObj.warning;
      this.primary = jsonObj.primary;
      this.error = jsonObj.error;
      this.dark = jsonObj.dark;
    }
  }
}
