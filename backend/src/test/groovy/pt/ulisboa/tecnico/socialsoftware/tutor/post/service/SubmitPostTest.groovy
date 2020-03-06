package pt.ulisboa.tecnico.socialsoftware.tutor.post.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import spock.lang.Specification

@DataJpaTest
class SubmitPostTest extends Specification {

    def postService

    def setup() {
        postService = new PostService();
    }

    def "submit a valid post"() {
        // the post and the question is created and submitted
        expect: false
    }

    def "submit a post without a question"() {
        // the post is created without a question and submitted
        expect: false
    }

    def "submit a post without having answered the question"() {
        // the post is created without having the question answered and submitted
        expect: false
    }

    def "submit an empty post"() {
        // the empty post is created and submitted
        expect: false
    }

    def "submit a post with an invalid question"() {
        // the post is created with an invalid question and submitted
        expect: false
    }

    def "submit a post with the students question having too many chars"() {
        // the post is created with an invalid number of characters in the students question field and submitted
        expect: false
    }

    def "submit a post with the students question being empty"() {
        // the post is created with the students question being empty
        expect: false
    }

    def "submit a post with an invalid user id"() {
        // the post is created by a non-existing user and submitted
        expect: false
    }

    def "submit a post by a non-student"() {
        // the post is created by a user that does not have the role student and submitted
        expect: false
    }
}
