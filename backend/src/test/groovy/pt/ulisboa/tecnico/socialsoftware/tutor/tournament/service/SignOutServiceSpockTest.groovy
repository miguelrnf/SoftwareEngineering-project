package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import spock.lang.Specification

@DataJpaTest
class SignOutServiceSpockTest extends Specification{

    def setup(){

    }

    def "students unroll from a valid tournament"(){
        expected:false
    }

    def "students unroll from an invalid tournament"(){
        expected:false
    }

    def "not enroll students unroll from a valid tournament"(){
        expected:false
    }
}
