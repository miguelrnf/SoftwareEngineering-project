package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import spock.lang.Specification



@DataJpaTest
class UserOptionsSpockTest extends Specification{

    @Autowired
    TournamentService tournamentService


    def setup(){

    }

    def "valid of options"(){
        //valid options and tournament is created
        expect:false
}

    def "invalid of options"(){
        //valid options and tournament is created
        expect:false
    }

}
