import StatementOption from '@/models/statement/StatementOption';
import Image from '@/models/management/Image';
import { _ } from 'vue-underscore';

export default class StatementQuestion {
  quizQuestionId!: number;
  content!: string;
  image: Image | null = null;
  hasHint!: boolean;

  options: StatementOption[] = [];

  constructor(jsonObj?: StatementQuestion) {
    if (jsonObj) {
      this.quizQuestionId = jsonObj.quizQuestionId;
      this.content = jsonObj.content;
      this.image = jsonObj.image;
      this.hasHint = jsonObj.hasHint;

      if (jsonObj.options) {
        this.options = _.shuffle(
          jsonObj.options.map(
            (option: StatementOption) => new StatementOption(option)
          )
        );
      }
    }
  }
}
