package pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.domain.Suggestion;

import java.io.Serializable;
import java.util.List;

public class ListByUsernameDto implements Serializable {

    private List<Suggestion> _suggslist;
    private Integer numberofsuggs = 0;
    private Integer numberofapprovedsuugs = 0;

    public ListByUsernameDto(){
    }

    public void setNumberofsuugs(Integer getnumberofsuggs) {
    }

    public List getListByUsernameDto () {return this._suggslist;}

    public void setListByUsernameDto(List suggs) {this._suggslist = suggs}


    public Integer getNumberofapprovedsuugs() {
        return numberofapprovedsuugs;
    }

    public void setNumberofapprovedsuugs(Integer numberofapprovedsuugs) {
        this.numberofapprovedsuugs = numberofapprovedsuugs;
    }

    public Integer getNumberofsuggs() {
        return numberofsuggs;
    }

    public void setNumberofsuggs(Integer numberofsuggs) {
        this.numberofsuggs = numberofsuggs;
    }
}
