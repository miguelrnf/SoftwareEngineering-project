package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import spock.lang.Specification

@DataJpaTest
class enrollStudentsSpockTest extends Specification{

    def setup(){

    }

    def "Enroll a student that is in the same courseExecution as the owner"() {
        //add a valid student to a tornament
        expected:false
    }

    def "Enroll a student that isn't in the same courseExecution as the owner"() {
        //add an invalid student to a tornament
        expected:false
    }
}
