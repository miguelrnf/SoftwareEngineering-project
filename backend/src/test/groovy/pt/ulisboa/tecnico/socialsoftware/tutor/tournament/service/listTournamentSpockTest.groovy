package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import spock.lang.Specification

@DataJpaTest
class listTournamentSpockTest extends Specification{
    @Autowired
    TournamentService tournamentService

    def setup() {

    }

    def "show available tournaments"(){
        //available tournaments exist and are listed
        expects:false
    }

    def "no available tournaments"(){
        //no available tournaments exits throw exception
        expects:false
    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}
