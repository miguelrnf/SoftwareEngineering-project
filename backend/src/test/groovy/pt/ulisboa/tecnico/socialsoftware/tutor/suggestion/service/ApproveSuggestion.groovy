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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
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

@DataJpaTest
class ApproveSuggestion extends Specification{

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
    public static final String VALID_NAME = "Ben Dover"
    public static final String VALID_USERNAME = "BenDover69"
    public static final String VALID_USERNAME_TEACHER = "something"
    public static final String VALID_NAME_TOPIC = "Spock"
    public static final String EMPTY_SUGGESTION = ""
    public static final String VALID_USERNAME_T = "Teacher"
    public static final String VALID_JUSTIFICATION = "approved/rejected"
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
    def VALID_T

    @Shared
    def INVALID_U_UID

    @Shared
    def INVALID_U_UNAME

    @Shared
    def INVALID_U_ROLE

    @Shared
    def VALID_TOPIC

    @Shared
    def VALID_TOPIC_DTO

    @Shared
    def VALID_TOPIC_LIST

    @Shared
    def INVALID_TOPIC_LIST

    @Shared
    def suggestion

    def sug

    def course
    def courseExecution

    def setupSpec() {
        given: "a user with an invalid uid"
        INVALID_U_UID = new User()
        INVALID_U_UID.setId(INVALID_ID)
        INVALID_U_UID.setRole(User.Role.STUDENT)
        INVALID_U_UID.setUsername(VALID_USERNAME)

        and: "a user with the role teacher"
        INVALID_U_ROLE = new User()
        INVALID_U_ROLE.setId(VALID_ID)
        INVALID_U_ROLE.setRole(User.Role.TEACHER)
        INVALID_U_ROLE.setUsername(VALID_USERNAME_TEACHER)

        and: "a user with an invalid username"
        INVALID_U_UNAME = new User()
        INVALID_U_UNAME.setId(VALID_ID)
        INVALID_U_UNAME.setRole(User.Role.STUDENT)


        and: "a valid user - STUDENT "
        VALID_U = new User(VALID_NAME, VALID_USERNAME, 1, User.Role.STUDENT)

        and: "a valid user - teacher "
        VALID_T = new User(VALID_NAME, VALID_USERNAME_TEACHER, 2, User.Role.TEACHER)
    }

    def setup(){
        given:
        course = new Course(COURSE_NAME, Course.Type.TECNICO)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        and: "a user with the role teacher"
        def userT = new User(VALID_NAME, VALID_USERNAME_TEACHER, 2, User.Role.TEACHER)


        and: "a user with the role student"
        def userS = new User(VALID_NAME, VALID_USERNAME, 1, User.Role.STUDENT)

        and: "a valid topicDto"
        VALID_TOPIC_DTO = new TopicDto()
        VALID_TOPIC_DTO.setId(VALID_ID)
        VALID_TOPIC_DTO.setName(VALID_NAME_TOPIC)

        and: "a valid topic"
        VALID_TOPIC = new Topic(course, VALID_TOPIC_DTO)


        and: "a valid list of topics"
        VALID_TOPIC_LIST = new HashSet<Topic>();
        VALID_TOPIC_LIST.add(VALID_TOPIC)

        and: "a invalid list of topics"
        INVALID_TOPIC_LIST = new HashSet<Topic>();

        and: "a suggestion"
        sug = new SuggestionDto()
        sug.set_questionStr(SUGGESTION_CONTENT as String)
        sug.setKey(VALID_KEY)

        List<TopicDto> topicsDto = new ArrayList<>();
        for (t in VALID_TOPIC_LIST){
            topicsDto.add(new TopicDto(t));
        }
        sug.set_topicsList(topicsDto)
        sug.set_id(VALID_ID)
        sug.set_student(new UserDto(userS))

        suggestion = new Suggestion(courseExecution, userS, sug)

        then: "add to repository"
        courseRepository.save(course)
        courseExecutionRepository.save(courseExecution)
        userRepository.save(userS)
        userRepository.save(userT)
        topicRepository.save(VALID_TOPIC)
        suggestionRepository.save(suggestion)
    }


    @Unroll
    def "valid approval"(){
        when:
        def sug = new SuggestionDto()
        def sug2 = new SuggestionDto()

        sug.set_questionStr(SUGGESTION_CONTENT)

        List<TopicDto> topicsDto = new ArrayList<>();
        for (t in VALID_TOPIC_LIST){
            topicsDto.add(new TopicDto(t));
        }

        sug.set_topicsList(topicsDto)

        sug.set_student(new UserDto(VALID_U))
        sug.setTitle("TITLE")

        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        sug.setOptions(options)

        sug = suggestionService.createSuggestion(courseExecution.getId(),sug)

        sug.setStatus(String.valueOf(status))
        sug.set_justification(j)


        def result = suggestionService.approveSuggestion(courseExecution.getId(), sug, new UserDto(VALID_T as User))

        then:
        result.get_justification() == sug.get_justification()
        result.getStatus() == String.valueOf(status)


        where:
        j                   |t          |    status
        EMPTY_SUGGESTION    |VALID_T    |    Suggestion.Status.APPROVED
        VALID_JUSTIFICATION |VALID_T    |    Suggestion.Status.REJECTED

    }

    @Unroll
    def "invalid approval"(){
        when:
        def sug = new SuggestionDto()
        def sug2 = new SuggestionDto()

        sug.set_questionStr(SUGGESTION_CONTENT)

        List<TopicDto> topicsDto = new ArrayList<>();
        for (t in VALID_TOPIC_LIST){
            topicsDto.add(new TopicDto(t));
        }

        sug.set_topicsList(topicsDto)

        sug.set_student(new UserDto(VALID_U))
        sug.setTitle("TITLE")

        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        sug.setOptions(options)

        sug = suggestionService.createSuggestion(courseExecution.getId(),sug)

        sug.setStatus("APPROVED")
        sug.set_justification(j)


        suggestionService.approveSuggestion(courseExecution.getId(), sug, new UserDto(t as User))


        then:
        def result = thrown(TutorException)
        result.message == expected

        where:
        j                   |t               ||expected
        //EMPTY_SUGGESTION    |VALID_T         ||ErrorMessage.JUSTIFICATION_EMPTY.label
        VALID_JUSTIFICATION |VALID_U         ||ErrorMessage.USER_HAS_WRONG_ROLE.label


    }

    @TestConfiguration
    static class SuggestionServiceImplTestContextConfiguration {

        @Bean
        SuggestionService suggestionService() {
            return new SuggestionService()
        }
    }
}
