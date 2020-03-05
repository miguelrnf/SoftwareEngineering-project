package pt.ulisboa.tecnico.socialsoftware.tutor.tourament.service
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import spock.lang.Specification

class CreateTournamentServiceSpockTest extends Specification {

    def TournamentService

    def setup(){
        TournamentService = new TournamentService()
    }

    def "student creates a tournament"() {
       //the tournament is created
        expect: false
    }

    def "create a tournament with blank"() {
        //an exception is thrown
        expect: false
    }

    def "create a tournament with empty"() {
        //an exception is thrown
        expect: false
    }

    def "teacher creates a tournament"() {
        //an exception is thrown
        expect: false
    }

    def "admin creates a tournament"() {
        //an exception is thrown
        expect: false
    }
}
