package pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.user

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import spock.lang.Specification

class ChangeDashboardPrivacyTest extends Specification {
    def user

    def setup() {
        user = new User();
        user.setName('name')
        user.setUsername('username')
        user.setId(1)
        user.setKey(1)
        user.setDashboardPrivate(false)

    }

    def 'valid change dashboard privacy' () {
        when:
        def result = user.isDashboardPrivate
        user.changeDashboardPrivacy()

        then:
        result != user.isDashboardPrivate
    }
}
