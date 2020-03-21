package pt.ulisboa.tecnico.socialsoftware.tutor.exceptions;

public enum ErrorMessage {
    QUIZ_NOT_FOUND("Quiz not found with id %d"),
    QUIZ_QUESTION_NOT_FOUND("Quiz question not found with id %d"),
    QUIZ_ANSWER_NOT_FOUND("Quiz answer not found with id %d"),
    QUESTION_ANSWER_NOT_FOUND("Question answer not found with id %d"),
    OPTION_NOT_FOUND("Option not found with id %d"),
    QUESTION_NOT_FOUND("Question not found with id %d"),
    USER_NOT_FOUND("User not found with id %d"),
    TOPIC_NOT_FOUND("Topic not found with id %d"),
    ASSESSMENT_NOT_FOUND("Assessment not found with id %d"),
    TOPIC_CONJUNCTION_NOT_FOUND("Topic Conjunction not found with id %d"),
    COURSE_EXECUTION_NOT_FOUND("Course execution not found with name %d"),

    COURSE_NOT_FOUND("Course not found with name %s"),
    COURSE_NAME_IS_EMPTY("The course name is empty"),
    COURSE_TYPE_NOT_DEFINED("The course type is not defined"),
    COURSE_EXECUTION_ACRONYM_IS_EMPTY("The course execution acronym is empty"),
    COURSE_EXECUTION_ACADEMIC_TERM_IS_EMPTY("The course execution academic term is empty"),
    USERNAME_NOT_FOUND("Username %s not found"),

    QUIZ_USER_MISMATCH("Quiz %s is not assigned to student %s"),
    QUIZ_MISMATCH("Quiz Answer Quiz %d does not match Quiz Question Quiz %d"),
    QUESTION_OPTION_MISMATCH("Question %d does not have option %d"),
    COURSE_EXECUTION_MISMATCH("Course Execution %d does not have quiz %d"),
    QUIZ_ALREADY_STARTED("Quiz was already started"),

    CANNOT_DELETE_COURSE_EXECUTION("The course execution cannot be deleted %s"),

    DUPLICATE_TOPIC("Duplicate topic: %s"),
    DUPLICATE_USER("Duplicate user: %s"),
    DUPLICATE_COURSE_EXECUTION("Duplicate course execution: %s"),

    USERS_IMPORT_ERROR("Error importing users: %s"),
    QUESTIONS_IMPORT_ERROR("Error importing questions: %s"),
    TOPICS_IMPORT_ERROR("Error importing topics: %s"),
    ANSWERS_IMPORT_ERROR("Error importing answers: %s"),
    QUIZZES_IMPORT_ERROR("Error importing quizzes: %s"),

    QUESTION_IS_USED_IN_QUIZ("Question is used in quiz %s"),
    QUIZ_NOT_CONSISTENT("Field %s of quiz is not consistent"),
    USER_NOT_ENROLLED("%s - Not enrolled in any available course"),
    QUIZ_NO_LONGER_AVAILABLE("This quiz is no longer available"),
    QUIZ_NOT_YET_AVAILABLE("This quiz is not yet available"),

    TOURNAMENT_NOT_CONSISTENT("Field %s of tournament is not consistent"),
    TOURNAMENT_PERMISSION("Only student's can create tournaments"),
    TOURNAMENT_PERMISSION_ENROLL("Only student's can enroll in tournaments"),
    TOURNAMENT_NOT_FOUND("Tournament not found with id %s"),
    TOURNAMENT_NOT_AVAILABLE("Tournament not available"),
    TOURNAMENT_UNABLE_EDIT("Unable to edit tournament, field: %s"),
    TOURNAMENT_LIST_EMPTY("No tournaments found"),

    UNABLE_TO_UNROLL("Student %s isn't enrolled in the tournament"),
    NOT_ENOUGH_QUESTIONS_TOURNAMENT("Not enough questions to create a tournament"),

    NO_CORRECT_OPTION("Question does not have a correct option"),
    NOT_ENOUGH_QUESTIONS("Not enough questions to create a quiz"),
    QUESTION_MISSING_DATA("Missing information for quiz"),
    QUESTION_MULTIPLE_CORRECT_OPTIONS("Questions can only have 1 correct option"),
    QUESTION_CHANGE_CORRECT_OPTION_HAS_ANSWERS("Can not change correct option of answered question"),
    QUIZ_HAS_ANSWERS("Quiz already has answers"),
    QUIZ_ALREADY_COMPLETED("Quiz already completed"),
    QUIZ_QUESTION_HAS_ANSWERS("Quiz question has answers"),
    FENIX_ERROR("Fenix Error"),
    AUTHENTICATION_ERROR("Authentication Error"),
    FENIX_CONFIGURATION_ERROR("Incorrect server configuration files for fenix"),


    ACCESS_DENIED("You do not have permission to view this resource"),
    CANNOT_OPEN_FILE("Cannot open file"),

    USER_HAS_WRONG_ROLE("User has wrong role"),
    SUGGESTION_TOO_LONG("Suggestion is too long"),
    SUGGESTION_EMPTY("Empty suggestion"),
    EMPTY_TOPICS("No topics"),
    JUSTIFICATION_EMPTY("No justufication"),
    SUGGESTION_NOT_FOUND("Not found"),

    //new errors for posts
    USER_HAS_NOT_ANSWERED("You have not answered the given question"),
    NO_STUDENT_QUESTION("You cannot submit a post without a writing a question"),
    STUDENT_QUESTION_TOO_LONG("Your question is too long"),
    INVALID_POST("The specified post does not exist"),
    NOT_YOUR_POST("You do not have permission to change somebody else's post"),
    NO_APPROVED_SUGGESTIONS("No approved suggestions"),
    NO_ANSWER("No answer was given by a teacher"),
    INVALID_ANSWER_BLANK("The answer written is invalid because it is empty"),
    DIFFERENT_QUESTION("You cannot redirect when it's a different question"),
    INVALID_ANSWER_TOO_LONG("The answer given is too long"),
    ERROR_WHILE_REDIRECTING("There was an error while redirecting the answer"),
    INVALID_CREATION_DATE("The creation date is invalid"),
    INVALID_COMMENT("The comment you submitted is invalid"),
    INVALID_COMMENT_SEARCH("No results available"),
    COMMENT_NO_PARENT("The parent does not exist");

    public final String label;

    ErrorMessage(String label) {
        this.label = label;
    }
}