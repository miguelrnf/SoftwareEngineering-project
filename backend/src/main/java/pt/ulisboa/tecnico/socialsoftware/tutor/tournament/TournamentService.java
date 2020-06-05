package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.AssessmentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.AssessmentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.SolvedQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementOptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class TournamentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private OptionRepository optionRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto createTournament(int executionId, TournamentDto tournamentDto){

        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));

        if(tournamentDto.getTitle() == null || tournamentDto.getTitle().isBlank())
            throw new TutorException(TOURNAMENT_NOT_CONSISTENT,  "Title");

        if(tournamentDto.getOwner() == null || tournamentDto.getOwner().getUsername() == null)
            throw new TutorException(TOURNAMENT_NOT_CONSISTENT, "Owner");

        if(tournamentDto.getNumberOfQuestions() == null || tournamentDto.getNumberOfQuestions() < 1)
            throw new TutorException(NOT_ENOUGH_QUESTIONS_TOURNAMENT);

        User user = findUsername(tournamentDto.getOwner().getUsername());

        if(user.getRole() == User.Role.ADMIN){
            throw new TutorException(TOURNAMENT_PERMISSION);
        }

        Assessment assessment = checkAssessment(tournamentDto.getAssessmentDto(), courseExecution);

        Tournament tournament = new Tournament(tournamentDto, user, assessment);

        List<Question> availableQuestions = questionRepository.findAvailableQuestions(courseExecution.getCourse().getId());

        availableQuestions = filterByAssessment(availableQuestions, tournament);

        if (availableQuestions.size() < tournament.getNumberOfQuestions()) {
            throw new TutorException(NOT_ENOUGH_QUESTIONS);
        }

        assignTournamentToExecution(tournament, courseExecution);

        if (DateHandler.isValidDateFormat(tournamentDto.getAvailableDate()))
            tournament.setAvailableDate(DateHandler.toLocalDateTime(tournamentDto.getAvailableDate()));
        if (DateHandler.isValidDateFormat(tournamentDto.getConclusionDate()))
            tournament.setConclusionDate(DateHandler.toLocalDateTime(tournamentDto.getConclusionDate()));
        tournament.setCreationDate(DateHandler.now());

        entityManager.persist(tournament);

        return new TournamentDto(tournament);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto editTournament(String username, TournamentDto tournamentDto) {
        Tournament tournament = tournamentRepository.findById(tournamentDto.getId()).orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentDto.getId()));
        User u = findUsername(username);
        if(tournament.getOwner() != u || tournament.getStatus() != Tournament.TournamentStatus.CREATED || !tournament.getEnrolledStudents().isEmpty() ){
            throw new TutorException(TOURNAMENT_UNABLE_EDIT);

        }
        tournament.setAssessment(assessmentRepository.findById(tournamentDto.getAssessmentDto().getId()).orElseThrow(() -> new TutorException(ASSESSMENT_NOT_FOUND)));
        tournament.setTitle(tournamentDto.getTitle());
        tournament.setNumberOfQuestions(tournamentDto.getNumberOfQuestions());
        tournament.setAvailableDate(DateHandler.toLocalDateTime(tournamentDto.getAvailableDate()));
        tournament.setConclusionDate(DateHandler.toLocalDateTime(tournamentDto.getConclusionDate()));
        tournament.setType(Tournament.TournamentType.valueOf(tournamentDto.getType()));

        return new TournamentDto(tournament);
    }



    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> listTournaments(int courseExecutionId) {
        return tournamentRepository.findAll().stream()
                .filter(tournament -> this.checkStatus(tournament).equals(Tournament.TournamentStatus.CREATED) && tournament
                        .getCourseExecution().getId().equals(courseExecutionId))
                .map(TournamentDto::new).sorted(Comparator.comparing(TournamentDto::getTitle))
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> listAllTournaments(int courseExecutionId) {
        List<TournamentDto> result = new ArrayList<>();

        tournamentRepository.findAll().stream().filter(tournament -> tournament.getCourseExecution().getId().equals(courseExecutionId))
                .forEach(tournament -> {
                    checkStatus(tournament);
                    TournamentDto tournamentDto = new TournamentDto(tournament);
                    if (tournament.getQuiz() != null)
                        tournamentDto.getQuiz().setId(tournament.getQuiz().getId());
                    result.add(tournamentDto);
                });

        return result.stream().sorted(Comparator.comparing(TournamentDto::getTitle))
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> listOpenedTournaments(int courseExecutionId) {
        return tournamentRepository.findAll().stream()
                .filter(tournament -> this.checkStatus(tournament).equals(Tournament.TournamentStatus.OPEN) && tournament
                        .getCourseExecution().getId().equals(courseExecutionId))
                .map(TournamentDto::new).sorted(Comparator.comparing(TournamentDto::getTitle))
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> getOwnTournaments(String username, int courseExecutionId) {
        User owner = userRepository.findByUsername(username);
        return tournamentRepository.findAll().stream()
                .filter(tournament -> tournament.getOwner().equals(owner) && tournament
                        .getCourseExecution().getId().equals(courseExecutionId) && checkStatus(tournament) != null)
                .map(TournamentDto::new).sorted(Comparator.comparing(TournamentDto::getTitle))
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> getEnrolledTournaments(String username, int executionId) {
        User student = userRepository.findByUsername(username);
        List<Tournament> temp = tournamentRepository.findAll().stream()
                .filter(tournament -> tournament.getEnrolledStudents().contains(student) && tournament
                        .getCourseExecution().getId().equals(executionId))
                .sorted(Comparator.comparing(Tournament::getTitle))
                .collect(Collectors.toList());

        if(temp.isEmpty())
            return new ArrayList<>();

        return setQuizAnswers(temp, student, executionId);
    }

    private List<TournamentDto> setQuizAnswers(List<Tournament> tournaments, User user, int executionId) {
        List<TournamentDto> tournamentDtos = new ArrayList<>();

        Set<Integer> studentQuizIds = user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.getQuiz().getCourseExecution().getId() == executionId)
                .filter(quizAnswer -> quizAnswer.getQuiz().getType() == Quiz.QuizType.TOURNAMENT)
                .map(QuizAnswer::getQuiz)
                .map(Quiz::getId)
                .collect(Collectors.toSet());

        tournaments.forEach(tournament ->  {
                    checkStatus(tournament);
                    TournamentDto tournamentDto = new TournamentDto(tournament);
                    if (checkStatus(tournament) == Tournament.TournamentStatus.OPEN && !studentQuizIds.contains(tournament.getQuiz().getId())){
                            QuizAnswer quizAnswer = new QuizAnswer(user, tournament.getQuiz());
                            quizAnswerRepository.save(quizAnswer);
                            tournamentDto.setQuiz(new StatementQuizDto(quizAnswer));
                    }


                    if(tournament.getQuiz() != null) {
                        List<QuizAnswer> answerList = user.getQuizAnswers().stream()
                                .filter(quizAnswer -> quizAnswer.getQuiz().getId().equals(tournament.getQuiz().getId()))
                                .collect(Collectors.toList());
                        if (!answerList.isEmpty()) {
                            QuizAnswer answer = answerList.get(0);

                            if ((checkStatus(tournament) == Tournament.TournamentStatus.OPEN || checkStatus(tournament) == Tournament.TournamentStatus.CLOSED) && tournamentDto.getQuiz().getId() == null)
                                tournamentDto.setQuiz(new StatementQuizDto(answer));

                            tournamentDto.setCompleted(answer.isCompleted());

                            if (tournamentDto.isCompleted() && checkStatus(tournament) == Tournament.TournamentStatus.CLOSED) {
                                List<SolvedQuizDto> temp = user.getQuizAnswers().stream()
                                        .filter(quizAnswer -> quizAnswer.getQuiz().getId().equals(tournament.getQuiz().getId()))
                                        .map(SolvedQuizDto::new).collect(Collectors.toList());

                                if (temp.size() != 1)
                                    throw new TutorException(MULTIPLE_QUIZ_ANSWERS);

                                tournamentDto.setSolved(temp.get(0));
                            }
                        }
                    }

                    tournamentDtos.add(tournamentDto);
                });
        return tournamentDtos;
    }

    private Assessment checkAssessment(AssessmentDto assessmentDto, CourseExecution courseExecution){

        if( assessmentDto == null || (assessmentDto.getId() == null && assessmentDto.getTitle().equals("")))
            throw new TutorException(TOURNAMENT_NOT_CONSISTENT, "AssessmentDto");

        int assessmentId = assessmentDto.getId();

        List<Assessment> assessmentL = assessmentRepository.findByExecutionCourseId(courseExecution.getId()).stream().filter(a -> a.getId() == assessmentId).collect(Collectors.toList());

        if (assessmentL.isEmpty())
            throw new TutorException(ASSESSMENT_NOT_FOUND, assessmentId);

        Assessment assessment = assessmentL.get(0);

        if(!courseExecution.getAssessments().contains(assessment))
            throw new TutorException(ASSESSMENT_NOT_FOUND, assessmentId);

        if(assessment.getTopicConjunctions().isEmpty())
            throw new TutorException(TOPIC_CONJUNCTION_NOT_FOUND);

        checkAssessmentStatus(assessment.getStatus().name());

        return assessment;
    }

    public void checkCanRemove(Tournament tournament) {
        if( this.checkStatus(tournament) == Tournament.TournamentStatus.OPEN)
            throw new TutorException(TOURNAMENT_UNABLE_REMOVE, "Tournament is open");

        if( checkStatus(tournament) == Tournament.TournamentStatus.CREATED && !tournament.getEnrolledStudents().isEmpty())
            throw new TutorException(TOURNAMENT_UNABLE_REMOVE, "Tournament has enrolled students");
    }

    private void assignTournamentToExecution(Tournament t, CourseExecution courseExecution){
        courseExecution.addTournament(t);
        t.setCourseExecution(courseExecution);
    }

    private User findUsername(String username){
        User user = userRepository.findByUsername(username);

        if(user == null)
            throw new TutorException(USERNAME_NOT_FOUND, username);

        return user;
    }

    private void checkAssessmentStatus(String status){
        if(!status.equals(Assessment.Status.AVAILABLE.name()))
            throw new TutorException(TOURNAMENT_NOT_CONSISTENT, "Assessement Status");
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto enrollStudent(String username, int tournamentId) {
        User user = findUsername(username);

        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));

        if(user.getRole() != User.Role.STUDENT)
            throw new TutorException(TOURNAMENT_PERMISSION_ENROLL);

        if(this.checkStatus(tournament) != Tournament.TournamentStatus.CREATED)
            throw new TutorException(TOURNAMENT_NOT_AVAILABLE);

        if(tournament.getEnrolledStudents().contains(user) || user.getTournaments().contains(tournament)){
            throw new TutorException(USER_ALREADY_ENROLLED, user.getUsername());
        }

        if(!user.getCourseExecutions().contains(tournament.getCourseExecution())){
            throw new TutorException(TOURNAMENT_NOT_AVAILABLE);
        }


        tournament.enrollStudent(user);
        user.addTournament(tournament);

        return new TournamentDto(tournament);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto unrollStudent(String username, int tournamentId){
        User user = findUsername(username);
        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));

        if(this.checkStatus(tournament) != Tournament.TournamentStatus.CREATED)
            throw new TutorException(TOURNAMENT_NOT_AVAILABLE);

        if(!tournament.getEnrolledStudents().contains(user))
            throw new TutorException(UNABLE_TO_UNROLL, user.getUsername());

        tournament.getEnrolledStudents().remove(user);
        user.getTournaments().remove(tournament);

        return new TournamentDto(tournament);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CourseDto findTournamentCourseExecution(int tournamentId) {
        return this.tournamentRepository.findById(tournamentId)
                .map(Tournament::getCourseExecution)
                .map(CourseDto::new)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto findById(int tournamentId, int executionId, String username) {
        User user = findUsername(username);
        List<Tournament> tournaments = tournamentRepository.findById(tournamentId).stream().filter(t -> t.getCourseExecution().getId()
                .equals(executionId)).collect(Collectors.toList());

        if(tournaments.isEmpty())
            throw new TutorException(TOURNAMENT_NOT_FOUND, tournamentId);

        return setQuizAnswers(tournaments, user, executionId).get(0);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> getTournaments() {
        return tournamentRepository.findAll().stream().map(TournamentDto::new).sorted(Comparator
                .comparing(TournamentDto::getTitle)).collect(Collectors.toList());
    }

    public Tournament.TournamentStatus checkStatus(Tournament tournament){
        if(tournament.getStatus() == Tournament.TournamentStatus.CANCELED)
            return Tournament.TournamentStatus.CANCELED;
        if(DateHandler.now().isBefore(tournament.getAvailableDate()))
            tournament.setStatus(Tournament.TournamentStatus.CREATED);
        else if(DateHandler.now().isBefore(tournament.getConclusionDate())){
            tournament.setStatus(Tournament.TournamentStatus.OPEN);
            if(tournament.getQuiz() == null) {
                generateTournamentQuiz(tournament.getId());
            }

        }
        else
            tournament.setStatus(Tournament.TournamentStatus.CLOSED);

        return tournament.getStatus();
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void removeTournament(Integer tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
        this.checkCanRemove(tournament);
        tournament.remove();

        tournamentRepository.delete(tournament);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto cancelTournament(int tournamentId, String username) {
       User user = findUsername(username);
       Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));

       if(tournament.getOwner() != user)
           throw new TutorException(TOURNAMENT_PERMISSION_CANCEL);

       if(this.checkStatus(tournament) != Tournament.TournamentStatus.CREATED)
           throw new TutorException(TOURNAMENT_NOT_AVAILABLE);

       tournament.setStatus(Tournament.TournamentStatus.CANCELED);
        this.checkStatus(tournament);

       return new TournamentDto(tournament);
    }

    public List<Question> filterByAssessment(List<Question> availableQuestions, Tournament tournament) {
        Assessment assessment = assessmentRepository.findById(tournament.getAssessment().getId())
                .orElseThrow(() -> new TutorException(ASSESSMENT_NOT_FOUND,tournament.getAssessment().getId()));

        return availableQuestions.stream().filter(question -> question.belongsToAssessment(assessment)).collect(Collectors.toList());
    }

    public void generateTournamentQuiz(int tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));

        if(tournament.getEnrolledStudents().size() <= 1) {
            tournament.setStatus(Tournament.TournamentStatus.CANCELED);
            return;
        }

        int executionId = tournament.getCourseExecution().getId();
        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));

        Quiz quiz = new Quiz();
        quiz.setKey(quizService.getMaxQuizKey() + 1);

        List<Question> availableQuestions = questionRepository.findAvailableQuestions(courseExecution.getCourse().getId());

        availableQuestions = filterByAssessment(availableQuestions, tournament);

        availableQuestions = tournament.getOwner().filterQuestionsByStudentModel(tournament.getNumberOfQuestions(), availableQuestions);
        quiz.setCourseExecution(courseExecution);
        courseExecution.addQuiz(quiz);

        quizRepository.save(quiz);

        this.generateQuiz(availableQuestions, quiz, tournament);

        new QuizDto(quiz, true);
    }

    private void generateQuiz(List<Question> questions, Quiz quiz, Tournament tournament) {
        tournament.setQuiz(quiz);
        IntStream.range(0,questions.size())
                .forEach(index -> {
                    QuizQuestion temp = new QuizQuestion(tournament.getQuiz(), questions.get(index), index);
                    quizQuestionRepository.save(temp);

                });

        tournament.getQuiz().setType("TOURNAMENT");
        tournament.getQuiz().setOneWay(true);
        tournament.getQuiz().setAvailableDate(tournament.getAvailableDate());
        tournament.getQuiz().setConclusionDate(tournament.getConclusionDate());
        tournament.getQuiz().setResultsDate(tournament.getConclusionDate());
        tournament.getQuiz().setCreationDate(DateHandler.now());
        tournament.getQuiz().setTitle(tournament.getTitle());
        tournament.getQuiz().setTournament(tournament);
    }

    private Question findQuestion(Integer questionId){
        return questionRepository.findById(questionId).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StatementQuestionDto removeTwoOptions(StatementQuestionDto statementQuestionDto, Integer quizId){
        List<StatementOptionDto> statementOptionDtos = statementQuestionDto.getOptions();
        List<Option> options = new ArrayList<>();
        int size;
        int i = 0;
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        if(quiz.getTournament() == null){
            throw new TutorException(TOURNAMENT_NOT_FOUND);
        }

        if(quiz.getTournament().getType() != Tournament.TournamentType.STANDARD){
            throw new TutorException(TOURNAMENT_INVALID_TYPE);
        }

        statementOptionDtos.forEach(statementOptionDto -> {
            Option o = optionRepository.findById(statementOptionDto.getOptionId()).orElseThrow(()-> new TutorException(OPTION_NOT_FOUND, statementOptionDto.getOptionId()));
            options.add(o);
        });

        size = options.size();
        if(size != 1) {
            while (options.size() > size / 2) {
                if (!options.get(i).getCorrect())
                    options.remove(options.get(i));
                i++;
            }
        }

        statementOptionDtos = options.stream().map(StatementOptionDto::new).collect(Collectors.toList());

        statementQuestionDto.setOptions(statementOptionDtos);

        return statementQuestionDto;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StatementQuestionDto rigthAnswer(StatementQuestionDto statementQuestionDto, Integer quizId){
        List<StatementOptionDto> statementOptionDtos = statementQuestionDto.getOptions();
        List<Option> options = new ArrayList<>();

        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        if(quiz.getTournament() == null){
            throw new TutorException(TOURNAMENT_NOT_FOUND);
        }

        if(quiz.getTournament().getType() != Tournament.TournamentType.STANDARD){
            throw new TutorException(TOURNAMENT_INVALID_TYPE);
        }

        statementOptionDtos.forEach(statementOptionDto -> {
            Option o = optionRepository.findById(statementOptionDto.getOptionId()).orElseThrow(()-> new TutorException(OPTION_NOT_FOUND, statementOptionDto.getOptionId()));
            options.add(o);
        });

        options.removeIf(o -> !o.getCorrect());

        statementOptionDtos = options.stream().map(StatementOptionDto::new).collect(Collectors.toList());

        statementQuestionDto.setOptions(statementOptionDtos);

        return statementQuestionDto;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String getHint(StatementQuestionDto statementQuestionDto, Integer quizId){
        List<Question> q = new ArrayList<>();

        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        if(quiz.getTournament() == null){
            throw new TutorException(TOURNAMENT_NOT_FOUND);
        }

        if(quiz.getTournament().getType() != Tournament.TournamentType.STANDARD){
            throw new TutorException(TOURNAMENT_INVALID_TYPE);
        }

        quiz.getQuizQuestions().forEach(quizQuestion -> {
            if (quizQuestion.getQuestion().getContent().equals(statementQuestionDto.getContent()))
                q.add(quizQuestion.getQuestion());
                            });

        return q.get(0).getHint();
    }

}
