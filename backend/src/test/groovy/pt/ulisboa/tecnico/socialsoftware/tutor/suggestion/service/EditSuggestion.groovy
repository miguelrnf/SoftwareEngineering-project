package pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.SuggestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.domain.Suggestion
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.dto.SuggestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestion.repository.SuggestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DataJpaTest
class EditSuggestion extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String SUGGESTION_CONTENT = 'suggestion content'
    public static final String OPTION_CONTENT = "optionId content"
    public static final String URL = 'URL'
    public static final int VALID_KEY = 1
    public static final int INVALID_KEY = -1
    public static final int VALID_ID = 1
    public static final int INVALID_ID = -1
    public static final String VALID_NAME = "Ruben Freitas"
    public static final String VALID_USERNAME = "RubenFreitas99"
    public static final String VALID_USERNAME_TEACHER = "RuiPeter"
    public static final String VALID_NAME2 = "Goncalo Freitas"
    public static final String VALID_USERNAME2 = "GoncaloQueridoFreitas"
    public static final String VALID_NAME_TOPIC = "Spock"
    public static final String EMPTY_SUGGESTION = ""
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
    @Autowired
    SuggestionService suggestionService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    SuggestionRepository suggestionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    TopicRepository topicRepository

    @Shared
    def VALID_U

    @Shared
    def INVALID_U_UID

    @Shared
    def INVALID_U_UNAME

    @Shared
    def INVALID_U_ROLE

    @Shared
    def VALID_TOPIC

    @Shared
    def VALID_TOPIC_LIST

    @Shared
    def INVALID_TOPIC_LIST

    @Shared
    def VALID_SUGGESTION

    @Shared
    def FORMATTER

    @Shared
    def INVALID_SUGGESTION_E

    @Shared
    def INVALID_SUGGESTION_F

    @Shared
    def INVALID_SUGGESTION_IU

    def course
    def courseExecution

    def setupSpec() {
        given: "a user with an invalid uid"
        INVALID_U_UID = new User()
        INVALID_U_UID.setId(INVALID_ID)
        INVALID_U_UID.setRole(User.Role.STUDENT)
        INVALID_U_UID.setUsername(VALID_USERNAME)

        and: ""
        FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        and: "a user with the role teacher"
        INVALID_U_ROLE = new User()
        INVALID_U_ROLE.setId(VALID_ID)
        INVALID_U_ROLE.setRole(User.Role.TEACHER)
        INVALID_U_ROLE.setUsername(VALID_USERNAME_TEACHER)

        and: "a user with an invalid username"
        INVALID_U_UNAME = new User()
        INVALID_U_UNAME.setId(VALID_ID)
        INVALID_U_UNAME.setRole(User.Role.STUDENT)

        and: "a valid topic"
        VALID_TOPIC = new Topic()
        //VALID_TOPIC.setCourse(COURSE_NAME)
        VALID_TOPIC.setId(VALID_ID)
        VALID_TOPIC.setName(VALID_NAME_TOPIC)

        and: "a valid list of topics"
        VALID_TOPIC_LIST = new HashSet<Topic>();
        VALID_TOPIC_LIST.add(VALID_TOPIC)

        and: "a invalid list of topics"
        INVALID_TOPIC_LIST = new HashSet<Topic>();

        and: "a valid user - STUDENT "
        VALID_U = new User()
        VALID_U.setId(VALID_ID)
        VALID_U.setRole(User.Role.STUDENT)
        VALID_U.setUsername(VALID_USERNAME)
        VALID_U.setName(VALID_NAME)

        and: "valid suggestion"
        VALID_SUGGESTION = new Suggestion()
        VALID_SUGGESTION.set_student(VALID_U)
        VALID_SUGGESTION.set_questionStr(SUGGESTION_CONTENT)
        VALID_SUGGESTION.set_topicsList(VALID_TOPIC_LIST)

        and: "empty suggestion"
        INVALID_SUGGESTION_E = new Suggestion()
        INVALID_SUGGESTION_E.set_student(VALID_U)
        INVALID_SUGGESTION_E.set_questionStr(EMPTY_SUGGESTION)

        and: "too much char suggestion"
        INVALID_SUGGESTION_F = new Suggestion()
        INVALID_SUGGESTION_F.set_student(VALID_U)
        INVALID_SUGGESTION_F.set_questionStr(TOO_MANY_CHARS)

        and: "invalid user"
        INVALID_SUGGESTION_IU = new User()
        INVALID_SUGGESTION_IU.setId(VALID_ID)
        INVALID_SUGGESTION_IU.setRole(User.Role.STUDENT)
        INVALID_SUGGESTION_IU.setUsername(VALID_USERNAME2)
        INVALID_SUGGESTION_IU.setName(VALID_NAME2)

    }

    def setup() {

        given:
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        and: "a user with the role teacher"
        def userT = new User(VALID_NAME, VALID_USERNAME_TEACHER, 2, User.Role.TEACHER)

        and: "a user with the role student"
        def userS = new User(VALID_NAME, VALID_USERNAME, 1, User.Role.STUDENT)

        and: "a user with the role student that didn't create that suggestion"
        def userS2 = new User(VALID_NAME2, VALID_USERNAME2, 3, User.Role.STUDENT)

        and:
        def topic = new Topic();
        topic.setName(VALID_NAME_TOPIC)
        topic.setCourse(course)

        and: "valid suggestion"
        def suggestion = new Suggestion()
        suggestion.set_student(userS)
        suggestion.set_questionStr(SUGGESTION_CONTENT)
        suggestion.setKey(VALID_KEY)
        suggestion.setCreationDate(LocalDateTime.now())


        then: "add to repository"
        println(suggestion.dump())
        courseRepository.save(course)
        courseExecutionRepository.save(courseExecution)
        userRepository.save(userS)
        userRepository.save(userS2)
        userRepository.save(userT)
        topicRepository.save(topic)
        suggestionRepository.save(suggestion)

    }

    @Unroll
    def "valid suggestion"() {


        when:
        def sug = new SuggestionDto()
        sug.set_questionStr(s as String)
        sug.set_student(new UserDto(u as User))
        sug.set_id(1)
        sug.setCreationDate(LocalDateTime.now().format(FORMATTER))

        then:
        def result = suggestionService.editSuggestion(sug)
        result.get_questionStr() == sug.get_questionStr()
        result.get_topicsList() == sug.get_topicsList()
        result.get_student() == sug.get_student()
        result.getCreationDate() == sug.getCreationDate();



        where:
        s                  | l                | u
        SUGGESTION_CONTENT | VALID_SUGGESTION | VALID_U
    }

    @Unroll
    def "invalid users"() {
        when:
        def sug = new SuggestionDto()
        sug.set_questionStr(s as String)
        sug.set_student(new UserDto(VALID_U as User))
        sug.setCreationDate(LocalDateTime.now().format(FORMATTER))
        List<TopicDto> topicsDto = new ArrayList<>();
        for (t in l){
            topicsDto.add(new TopicDto(t));
        }

        sug.set_topicsList(topicsDto)
        sug = suggestionService.createSuggestion(courseExecution.getId(), sug)
        sug.set_student(new UserDto(u as User))


        suggestionService.editSuggestion(sug)


        then:
        def result = thrown(TutorException)
        result.message == expected

        where:
        s                  | l                | u                     | expected
        SUGGESTION_CONTENT | VALID_TOPIC_LIST | INVALID_SUGGESTION_IU | ErrorMessage.ACCESS_DENIED.label
//        SUGGESTION_CONTENT | VALID_TOPIC_LIST | INVALID_U_ROLE        | ErrorMessage.USER_HAS_WRONG_ROLE.label
    }


/*    @Unroll
    def "edit a suggestion with invalid fields"() {
        println(topicRepository.findAll().dump())

        when:
        def sug = new SuggestionDto()
        sug.set_questionStr(s as String)
        sug.set_student(new UserDto(u as User))
        sug.set_id(1)
        sug.setCreationDate(LocalDateTime.now().format(FORMATTER))
        List<TopicDto> topicsDto = new ArrayList<>();
        for (t in VALID_TOPIC_LIST){
            topicsDto.add(new TopicDto(t));
        }

        sug.set_topicsList(topicsDto)
        suggestionService.createSuggestion(courseExecution.getId(), sug)

        suggestionService.editSuggestion(sug)


        then:
        def result = suggestionService.editSuggestion(sug)
        result.get_questionStr() == sug.get_questionStr()
        result.get_topicsList() == sug.get_topicsList()
        result.get_student().getUsername() == sug.get_student().getUsername()
        result.getCreationDate() == sug.getCreationDate();

        where:
        s                | u       || expected
        TOO_MANY_CHARS   | VALID_U || ErrorMessage.SUGGESTION_TOO_LONG.label
        EMPTY_SUGGESTION | VALID_U || ErrorMessage.SUGGESTION_EMPTY.label


    }*/

    @TestConfiguration
    static class SuggestionServiceImplTestContextConfiguration {

        @Bean
        SuggestionService suggestionService() {
            return new SuggestionService()
        }
    }
}