import RemoteServices from '@/services/RemoteServices';
import { Tournament } from '@/models/management/Tournament';
import Assessment from '@/models/management/Assessment';

export default class TournamentManager {
  assessmentDto: Assessment = new Assessment();
  assessmentId: number | null = null;
  numberOfQuestions: string = '5';
  title: string = '';
  availableDate!: string;
  conclusionDate!: string;
  tournament: Tournament | null = null;

  private static _tournament: TournamentManager = new TournamentManager();

  static get getInstance(): TournamentManager {
    return this._tournament;
  }

  async getNewTournament() {
    this.assessmentDto.id = this.assessmentId;
    let params = {
      title: this.title,
      availableDate: this.availableDate,
      conclusionDate: this.conclusionDate,
      assessmentDto: this.assessmentDto,
      numberOfQuestions: +this.numberOfQuestions
    };
    this.tournament = await RemoteServices.createNewTournament(params);
  }

  reset() {
    this.tournament = null;
  }

  isEmpty(): boolean {
    return this.tournament == null;
  }
}
