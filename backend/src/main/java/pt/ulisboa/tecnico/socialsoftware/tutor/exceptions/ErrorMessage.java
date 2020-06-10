package pt.ulisboa.tecnico.socialsoftware.tutor.exceptions;

public enum ErrorMessage {

    INVALID_ACADEMIC_TERM_FOR_COURSE_EXECUTION("Invalid academic term for course execution"),
    INVALID_ACRONYM_FOR_COURSE_EXECUTION("Invalid acronym for course execution"),
    INVALID_CONTENT_FOR_OPTION("Invalid content for option"),
    INVALID_CONTENT_FOR_QUESTION("Invalid content for question"),
    INVALID_NAME_FOR_COURSE("Invalid name for course"),
    INVALID_NAME_FOR_TOPIC("Invalid name for topic"),
    INVALID_SEQUENCE_FOR_OPTION("Invalid sequence for option"),
    INVALID_SEQUENCE_FOR_QUESTION_ANSWER("Invalid sequence for question answer"),
    INVALID_TITLE_FOR_ASSESSMENT("Invalid title for assessment"),
    INVALID_TITLE_FOR_QUESTION("Invalid title for question"),
    INVALID_URL_FOR_IMAGE("Invalid url for image"),
    INVALID_TYPE_FOR_COURSE("Invalid type for course"),
    INVALID_TYPE_FOR_COURSE_EXECUTION("Invalid type for course execution"),
    INVALID_AVAILABLE_DATE_FOR_QUIZ("Invalid available date for quiz"),
    INVALID_CONCLUSION_DATE_FOR_QUIZ("Invalid conclusion date for quiz"),
    INVALID_RESULTS_DATE_FOR_QUIZ("Invalid results date for quiz"),
    INVALID_TITLE_FOR_QUIZ("Invalid title for quiz"),
    INVALID_TYPE_FOR_QUIZ("Invalid type for quiz"),
    INVALID_QUESTION_SEQUENCE_FOR_QUIZ("Invalid question sequence for quiz"),
    MULTIPLE_QUIZ_ANSWERS("There are multiple answers for the same quiz"),

    ASSESSMENT_NOT_FOUND("Assessment not found with id %d"),
    COURSE_EXECUTION_NOT_FOUND("Course execution not found with id %d"),
    OPTION_NOT_FOUND("Option not found with id %d"),
    QUESTION_ANSWER_NOT_FOUND("Question answer not found with id %d"),
    QUESTION_NOT_FOUND("Question not found with id %d"),
    QUIZ_ANSWER_NOT_FOUND("Quiz answer not found with id %d"),
    QUIZ_NOT_FOUND("Quiz not found with id %d"),
    QUIZ_QUESTION_NOT_FOUND("Quiz question not found with id %d"),
    TOPIC_CONJUNCTION_NOT_FOUND("Topic Conjunction not found with id %d"),

    TOPIC_NOT_FOUND("Topic not found with id %d"),
    USER_NOT_FOUND("User not found with id %d"),
    COURSE_NOT_FOUND("Course not found with name %s"),

    CANNOT_DELETE_COURSE_EXECUTION("The course execution cannot be deleted %s"),
    USERNAME_NOT_FOUND("Username %s not found"),
    USERID_NOT_FOUND("User not found"),
    COURSE_NAME_IS_EMPTY("The course name is empty"),
    COURSE_TYPE_NOT_DEFINED("The course type is not defined"),
    COURSE_EXECUTION_ACRONYM_IS_EMPTY("The course execution acronym is empty"),
    COURSE_EXECUTION_ACADEMIC_TERM_IS_EMPTY("The course execution academic term is empty"),
    SUGGESTION_NOT_FOUND("Suggestion %s not found"),

    SUGGESTION_ALREADY_APP("The Suggestion was Already Approved"),
    SUGGESTION_ALREADY_REJ("The Suggestion was Already Rejected"),


    QUIZ_USER_MISMATCH("Quiz %s is not assigned to student %s"),
    QUIZ_MISMATCH("Quiz Answer Quiz %d does not match Quiz Question Quiz %d"),
    QUESTION_OPTION_MISMATCH("Question %d does not have option %d"),
    COURSE_EXECUTION_MISMATCH("Course Execution %d does not have quiz %d"),
    QUIZ_ALREADY_STARTED("Quiz was already started"),

    DUPLICATE_TOPIC("Duplicate topic: %s"),
    DUPLICATE_USER("Duplicate user: %s"),
    DUPLICATE_COURSE_EXECUTION("Duplicate course execution: %s"),

    USERS_IMPORT_ERROR("Error importing users: %s"),
    QUESTIONS_IMPORT_ERROR("Error importing questions: %s"),
    TOPICS_IMPORT_ERROR("Error importing topics: %s"),
    ANSWERS_IMPORT_ERROR("Error importing answers: %s"),
    QUIZZES_IMPORT_ERROR("Error importing quizzes: %s"),

    QUESTION_MISSING_DATA("Question Missind Data"),
    QUESTION_MULTIPLE_CORRECT_OPTIONS ("Can't have multiple correct options"),

    QUESTION_IS_USED_IN_QUIZ("Question is used in quiz %s"),
    USER_NOT_ENROLLED("%s - Not enrolled in any available course"),
    QUIZ_NO_LONGER_AVAILABLE("This quiz is no longer available"),
    QUIZ_NOT_YET_AVAILABLE("This quiz is not yet available"),

    TOURNAMENT_NOT_CONSISTENT("Field %s of tournament is not consistent"),
    TOURNAMENT_PERMISSION("Only student's can create tournaments"),
    TOURNAMENT_LIST_EMPTY("No tournaments found"),
    TOURNAMENT_PERMISSION_ENROLL("Only student's can enroll in tournaments"),
    TOURNAMENT_NOT_FOUND("Tournament not found with id %s"),
    TOURNAMENT_NOT_AVAILABLE("Tournament not available"),
    TOURNAMENT_UNABLE_EDIT("Unable to edit tournament, field: %s"),
    TOURNAMENT_UNABLE_REMOVE("Unable to remove, reason: %s"),
    TOURNAMENT_PERMISSION_CANCEL("Only owner can cancel the tournament"),
    TOURNAMENT_INVALID_STATUS("Tournament is %s"),

    USER_ALREADY_ENROLLED("User %s already enrolled in tournament"),
    UNABLE_TO_UNROLL("Student %s isn't enrolled in the tournament"),
    NOT_ENOUGH_QUESTIONS_TOURNAMENT("Not enough questions to create a tournament"),

    NO_CORRECT_OPTION("Question does not have a correct option"),
    NOT_ENOUGH_QUESTIONS("Not enough questions to create a quiz"),
    ONE_CORRECT_OPTION_NEEDED("Questions need to have 1 and only 1 correct option"),
    CANNOT_CHANGE_ANSWERED_QUESTION("Can not change answered question"),
    QUIZ_HAS_ANSWERS("Quiz already has answers"),
    QUIZ_ALREADY_COMPLETED("Quiz already completed"),
    QUIZ_QUESTION_HAS_ANSWERS("Quiz question has answers"),
    FENIX_ERROR("Fenix Error"),
    AUTHENTICATION_ERROR("Authentication Error"),
    FENIX_CONFIGURATION_ERROR("Incorrect server configuration files for fenix"),
    EMPTY_SUGGESTIONS_LIST ("You don't have any suggestion"),
    SUGGESTION_NOT_APPROVED("Suggestion is not approved"),

    ACCESS_DENIED("You do not have permission to view this resource"),
    CANNOT_OPEN_FILE("Cannot open file"),

    USER_HAS_WRONG_ROLE("User has wrong role"),
    SUGGESTION_TOO_LONG("Suggestion is too long"),
    SUGGESTION_EMPTY("Empty suggestion"),
    EMPTY_TOPICS("No topics"),
    JUSTIFICATION_EMPTY("No justufication"),


    //new errors for posts
    USER_HAS_NOT_ANSWERED("You have not answered the given question"),
    NO_STUDENT_QUESTION("You cannot submit a post without a writing a question"),
    STUDENT_QUESTION_TOO_LONG("Your question is too long"),
    INVALID_POST("The specified post does not exist"),
    NOT_YOUR_POST("You do not have permission to change somebody else's post"),
    NO_APPROVED_SUGGESTIONS("No approved suggestions"),
    NOT_SUGGESTION_CREATOR("User not the one who created the suggestion"),
    NO_TOPICS("No topics"),
    NO_ANSWER("No answer was given by a teacher"),
    INVALID_ANSWER_BLANK("The answer written is invalid because it is empty"),
    DIFFERENT_QUESTION("You cannot redirect when it's a different question"),
    INVALID_ANSWER_TOO_LONG("The answer given is too long"),
    ERROR_WHILE_REDIRECTING("There was an error while redirecting the answer"),
    INVALID_CREATION_DATE("The creation date is invalid"),
    INVALID_COMMENT("The comment you submitted is invalid"),
    INVALID_COMMENT_SEARCH("No results available"),
    ALREADY_UPVOTED("You have already upvoted this post"),
    ALREADY_DOWNVOTED("You have already downvoted this post"),
    COMMENT_NO_PARENT("The parent does not exist");


    public final String label;

    ErrorMessage(String label) {
        this.label = label;
    }
}