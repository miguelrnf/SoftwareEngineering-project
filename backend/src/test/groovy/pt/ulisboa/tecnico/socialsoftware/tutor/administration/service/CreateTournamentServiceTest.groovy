package pt.ulisboa.tecnico.socialsoftware.tutor.administration.service
import pt.ulisboa.tecnico.socialsoftware.tutor.administration.AdministrationService
import spock.lang.Specification

class CreateTournamentServiceTest extends Specification {

    def adminService

    def setup(){
        adminService = new AdministrationService()
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
