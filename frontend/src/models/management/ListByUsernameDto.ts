import Suggestion from '@/models/management/Suggestion';

export default class ListByUsernameDto {
  _suggslist!: Suggestion[];
  numberofsuggs!: number;
  numberofapprovedsuugs!: number;

  constructor(jsonObj?: ListByUsernameDto) {
    if (jsonObj) {
      this.numberofsuggs = jsonObj.numberofsuggs;
      this.numberofapprovedsuugs = jsonObj.numberofapprovedsuugs;

      if (jsonObj._suggslist != null)
        this._suggslist = jsonObj._suggslist.map(
          (suggestion: Suggestion) => new Suggestion(suggestion)
        );
    }
  }
}
