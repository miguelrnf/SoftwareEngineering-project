package pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.SuggestionService
import spock.lang.Specification

@DataJpaTest
class TestTest extends Specification{
    @Autowired
    SuggestionService suggestionService

    def "test test test"() {
        expect:
        false
    }

    @TestConfiguration
    static class SugestionServiceTestContextConfiguration {
        @Bean
        SuggestionService suggestionService() {
            return new SuggestionService()
        }
    }
}
