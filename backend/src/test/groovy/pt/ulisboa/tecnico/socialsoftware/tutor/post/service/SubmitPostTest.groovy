package pt.ulisboa.tecnico.socialsoftware.tutor.post.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.post.PostService
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostDto
import pt.ulisboa.tecnico.socialsoftware.tutor.post.dto.PostQuestionDto
import spock.lang.Specification

@DataJpaTest
class SubmitPostTest extends Specification {
    public static final String VALID_QUESTION = 'This is a valid question'
    public static final String VALID_STUDENT_QUESTION = 'I am asking a valid question'
    public static final String EMPTY_QUESTION = ''
    public static final int VALID_QID = 1
    public static final int INVALID_QID = -1
    public static final int VALID_UID = 1
    public static final int INVALID_UID = -1
    public static final String TOO_MANY_CHARS =
                    '5EdnCpIJFNNr0enpzluxNDqldKmHf6TZvTeLpj6laJPTYaZeI3DYv9KGVXtykpTq0hjXtS75Y3VhBlHlPPI3E1HlmHNI5pH' +
                    '5QYoF24hA7Dd8z6nxA8NStjuugQmKMuZYKV5jugeFtcqt2yoT4LzVMtAvtB7jGMQ8ua4Pxm1QifflguBuJDNmXdtNkpwX3l' +
                    'smTxBwLMoIvXebgzIDG20yloMadbiY8RT7IcMYSRkAFZJqT4xS6Zr9MXLA0ceLetDLYTvWkXokBo4hGcq4cWhXTF6EpK9JV' +
                    'wzx7qH0ftW3nakG1dYonRLFvsvAHDby5eomorAjt35goeFCKiiqGd69NIZhVJm7LIqArqndg52o0cUGIm064b0TbxN9naBy' +
                    '3bFhiDjNh22m8XPgbVh4OHloTKVW0xELfMPDWXjeVsXQrEudyKuUrJKzhRBGoxkXHgNcnASnGTfSm4VQGVsQbuWGiOQ7RLn' +
                    'bs1xN568BiF1vQ3MTHvhS922likFcpOEnTqpG41UOiSYJuGXnybo1Pr5fNirhtBhf2lQTKlX6Np0EsshuDjGqBqjWJg3npy' +
                    'vwdgl6bAjG7wt0RaDf6g22iIv5dys9KCd6mZB5mCh1W1naEAaUZkS0d52fwhciRVmAUqlQxmfdl650O14FntY93IWILmChH' +
                    'wa7vsaXqFJbMM5c4hN2M2mCjVbQYimYHGty6JaJGJ6vBAjkFqRRTC98u5xLBijcUgIfswDdO2JatYs5feyUEEvlB4WO3QcF' +
                    'jYzZdZKjgpNfNXc86zBD3cgUZpk30wSsxN1MgVutK335D4KmqyMcZ5vD4fBoZtJPgAcjOFLSJucrZVr28ln8wL404Slm3EA' +
                    'VBiTuQJWUYlaMJ33nJh6UGeZx1Hwj0fThLq2UtWN9KsBRKnvPl54Ixprv4bisZ6CLYoIh4bhNNZGzVTYmchtvYfVahe153l' +
                    '9vusIYIQt7jSaqSrzlzdS94a1qa3rKiGqvFpgBsbPvzQ6eDpA6WgSTrUgJTuNr5ns1UNG2Y0KqGqvFpgBsbPvzQ6eDpA6Wg' +
                    '5EdnCpIJFNNr0enpzluxNDqldKmHf6TZvTeLpj6laJPTYaZeI3DYv9KGVXtykpTq0hjXtS75Y3VhBlHlPPI3E1HlmHNI5pH' +
                    '5QYoF24hA7Dd8z6nxA8NStjuugQmKMuZYKV5jugeFtcqt2yoT4LzVMtAvtB7jGMQ8ua4Pxm1QifflguBuJDNmXdtNkpwX3l' +
                    'smTxBwLMoIvXebgzIDG20yloMadbiY8RT7IcMYSRkAFZJqT4xS6Zr9MXLA0ceLetDLYTvWkXokBo4hGcq4cWhXTF6EpK9JV' +
                    'wzx7qH0ftW3nakG1dYonRLFvsvAHDby5eomorAjt35goeFCKiiqGd69NIZhVJm7LIqArqndg52o0cUGIm064b0TbxN9naBy' +
                    '3bFhiDjNh22m8XPgbVh4OHloTKVW0xELfMPDWXjeVsXQrEudyKuUrJKzhRBGoxkXHgNcnASnGTfSm4VQGVsQbuWGiOQ7RLn' +
                    'bs1xN568BiF1vQ3MTHvhS922likFcpOEnTqpG41UOiSYJuGXnybo1Pr5fNirhtBhf2lQTKlX6Np0EsshuDjGqBqjWJg3npy' +
                    'vwdgl6bAjG7wt0RaDf6g22iIv5dys9KCd6mZB5mCh1W1naEAaUZkS0d52fwhciRVmAUqlQxmfdl650O14FntY93IWILmChH' +
                    'wa7vsaXqFJbMM5c4hN2M2mCjVbQYimYHGty6JaJGJ6vBAjkFqRRTC98u5xLBijcUgIfswDdO2JatYs5feyUEEvlB4WO3QcF' +
                    'jYzZdZKjgpNfNXc86zBD3cgUZpk30wSsxN1MgVutK335D4KmqyMcZ5vD4fBoZtJPgAcjOFLSJucrZVr28ln8wL404Slm3EA' +
                    'VBiTuQJWUYlaMJ33nJh6UGeZx1Hwj0fThLq2UtWN9KsBRKnvPl54Ixprv4bisZ6CLYoIh4bhNNZGzVTYmchtvYfVahe153l' +
                    '9vusIYIQt7jSaqSrzlzdS94a1qa3rKiSTrx_'

    def postService

    def setup() {
        postService = new PostService()
    }

    def "submit a valid post"() {
        // the post and the question is created and submitted
        given: "a valid postQuestionDto"
        def postQuestionDto = new PostQuestionDto()
        postQuestionDto.setQid(VALID_QID)
        postQuestionDto.setUid(VALID_UID)
        postQuestionDto.setQuestion(VALID_QUESTION)
        postQuestionDto.setStudentQuestion(VALID_STUDENT_QUESTION)

        when:
        def result = postService.submitPost(postQuestionDto)

        then: "the returned data is correct"
        result.question.qid == VALID_QID
        result.question.uid == VALID_UID
        result.question.question == VALID_QUESTION
        result.question.studentQuestion == VALID_STUDENT_QUESTION
        result.postStatus == true
    }

    def "submit a post without a question"() {
        // the post is created without a question and submitted
        given: "a null postQuestionDto"
        def postQuestionDto = null

        when:
        postService.submitPost(postQuestionDto)

        then:
        thrown(TutorException)
    }

    def "submit a post without having answered the question"() {
        // the post is created without having the question answered and submitted
        given: "a valid postQuestionDto"
        def postQuestionDto = new PostQuestionDto()
        postQuestionDto.setQid(VALID_QID)
        postQuestionDto.setUid(VALID_UID)
        postQuestionDto.setQuestion(VALID_QUESTION)
        postQuestionDto.setStudentQuestion(VALID_STUDENT_QUESTION)

        when:
        postService.submitPost(postQuestionDto)

        then:
        thrown(TutorException)
    }

    def "submit an empty post"() {
        // the empty post is created and submitted
        given: "an empty postQuestionDto"
        def postQuestionDto = new PostQuestionDto()
        postQuestionDto.setQid(VALID_QID)
        postQuestionDto.setUid(VALID_UID)
        postQuestionDto.setQuestion(EMPTY_QUESTION)
        postQuestionDto.setStudentQuestion(EMPTY_QUESTION)

        when:
        postService.submitPost(postQuestionDto)

        then:
        thrown(TutorException)
    }

    def "submit a post with an invalid question"() {
        // the post is created with an invalid question and submitted
        given: "an invalid postQuestionDto"
        def postQuestionDto = new PostQuestionDto()
        postQuestionDto.setQid(INVALID_QID)
        postQuestionDto.setUid(VALID_UID)
        postQuestionDto.setQuestion(VALID_QUESTION)
        postQuestionDto.setStudentQuestion(VALID_STUDENT_QUESTION)

        when:
        postService.submitPost(postQuestionDto)

        then:
        thrown(TutorException)
    }

    def "submit a post with the students question having too many chars"() {
        // the post is created with an invalid number of characters in the students question field and submitted
        given: "an invalid postQuestionDto"
        def postQuestionDto = new PostQuestionDto()
        postQuestionDto.setQid(VALID_QID)
        postQuestionDto.setUid(VALID_UID)
        postQuestionDto.setQuestion(VALID_QUESTION)
        postQuestionDto.setStudentQuestion(TOO_MANY_CHARS)

        when:
        postService.submitPost(postQuestionDto)

        then:
        thrown(TutorException)
    }

    def "submit a post with the students question being empty"() {
        // the post is created with the students question being empty
        given: "an invalid postQuestionDto"
        def postQuestionDto = new PostQuestionDto()
        postQuestionDto.setQid(VALID_QID)
        postQuestionDto.setUid(VALID_UID)
        postQuestionDto.setQuestion(VALID_QUESTION)
        postQuestionDto.setStudentQuestion(EMPTY_QUESTION)

        when:
        postService.submitPost(postQuestionDto)

        then:
        thrown(TutorException)
    }

    def "submit a post with the question being empty"() {
        // the post is created with the question being empty
        given: "an invalid postQuestionDto"
        def postQuestionDto = new PostQuestionDto()
        postQuestionDto.setQid(VALID_QID)
        postQuestionDto.setUid(VALID_UID)
        postQuestionDto.setQuestion(EMPTY_QUESTION)
        postQuestionDto.setStudentQuestion(VALID_QUESTION)

        when:
        postService.submitPost(postQuestionDto)

        then:
        thrown(TutorException)
    }

    def "submit a post with an invalid user id"() {
        // the post is created by a non-existing user and submitted
        given: "an invalid postQuestionDto"
        def postQuestionDto = new PostQuestionDto()
        postQuestionDto.setQid(VALID_QID)
        postQuestionDto.setUid(INVALID_UID)
        postQuestionDto.setQuestion(VALID_QUESTION)
        postQuestionDto.setStudentQuestion(VALID_QUESTION)

        when:
        postService.submitPost(postQuestionDto)

        then:
        thrown(TutorException)
    }

    def "submit a post by a non-student"() {
        // the post is created by a user that does not have the role student and submitted
        given: "an invalid postQuestionDto"
        def postQuestionDto = new PostQuestionDto()
        postQuestionDto.setQid(VALID_QID)
        postQuestionDto.setUid(VALID_UID)
        postQuestionDto.setQuestion(VALID_QUESTION)
        postQuestionDto.setStudentQuestion(VALID_QUESTION)

        when:
        postService.submitPost(postQuestionDto)

        then:
        thrown(TutorException)
    }
}
