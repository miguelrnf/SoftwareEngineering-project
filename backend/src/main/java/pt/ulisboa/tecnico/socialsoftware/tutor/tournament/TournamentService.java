package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.AssessmentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.AssessmentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.StatementService;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementCreationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private QuizRepository quizRepository;

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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        if(tournamentDto.getOwner() == null || tournamentDto.getOwner().getUsername() == null)
            throw new TutorException(TOURNAMENT_NOT_CONSISTENT, "Owner");

        if(tournamentDto.getNumberOfQuestions() == null || tournamentDto.getNumberOfQuestions() < 1)
            throw new TutorException(NOT_ENOUGH_QUESTIONS_TOURNAMENT);

        User user = findUsername(tournamentDto.getOwner().getUsername());


        if(user.getRole() != User.Role.STUDENT)
            throw new TutorException(TOURNAMENT_PERMISSION);

        Assessment assessment = checkAssessment(tournamentDto.getAssessmentDto(), courseExecution);

        Tournament tournament = new Tournament(tournamentDto, user, assessment);

        assignTournamentToExecution(tournament, courseExecution);

        setValidCreationDate(tournament, tournamentDto.getCreationDate(), formatter);
        setValidAvailableDate(tournament, tournamentDto.getAvailableDate(), formatter);
        setValidConclusionDate(tournament, tournamentDto.getConclusionDate(), formatter);

        entityManager.persist(tournament);



        return new TournamentDto(tournament);
    }


    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> listTournaments(int courseExecutionId) {
        List<TournamentDto> temp = tournamentRepository.findAll().stream()
                .filter(tournament -> tournament.checkStatus().equals(Tournament.TournamentStatus.CREATED) && tournament
                        .getCourseExecution().getId().equals(courseExecutionId))
                .map(TournamentDto::new).sorted(Comparator.comparing(TournamentDto::getTitle))
                .collect(Collectors.toList());
        if(temp.isEmpty())
            throw new TutorException(TOURNAMENT_LIST_EMPTY);

        return temp;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> getOwnTournaments(String username, int courseExecutionId) {
        User owner = userRepository.findByUsername(username);
        List<TournamentDto> temp = tournamentRepository.findAll().stream()
                .filter(tournament -> tournament.getOwner().equals(owner) && tournament
                        .getCourseExecution().getId().equals(courseExecutionId))
                .map(TournamentDto::new).sorted(Comparator.comparing(TournamentDto::getTitle))
                .collect(Collectors.toList());
        if(temp.isEmpty())
            throw new TutorException(TOURNAMENT_LIST_EMPTY);

        return temp;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> getEnrolledTournaments(String username, int executionId) {
        User student = userRepository.findByUsername(username);
        List<TournamentDto> temp = tournamentRepository.findAll().stream()
                .filter(tournament -> tournament.getEnrolledStudents().contains(student) && tournament
                        .getCourseExecution().getId().equals(executionId))
                .map(TournamentDto::new).sorted(Comparator.comparing(TournamentDto::getTitle))
                .collect(Collectors.toList());

        if(temp.isEmpty())
            throw new TutorException(TOURNAMENT_LIST_EMPTY);

        return temp;
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

    private void assignTournamentToExecution(Tournament t, CourseExecution courseExecution){
        courseExecution.addTournament(t);
        t.setCourseExecution(courseExecution);
    }


    private void setValidCreationDate(Tournament tournament, String creationDate, DateTimeFormatter formatter){
        if (creationDate == null)
            tournament.setCreationDate(LocalDateTime.now());
        else
            tournament.setCreationDate(LocalDateTime.parse(creationDate, formatter));
    }

    private void setValidConclusionDate(Tournament tournament, String date, DateTimeFormatter formatter){
        if(date == null || date.equals("") || LocalDateTime.parse(date, formatter).isBefore(LocalDateTime.now()))
            throw new TutorException(TOURNAMENT_NOT_CONSISTENT, "Conclusion Date");

        tournament.setConclusionDate(LocalDateTime.parse(date, formatter));
    }

    private void setValidAvailableDate(Tournament tournament, String date, DateTimeFormatter formatter){
        if(date == null || date.equals("") || !LocalDateTime.parse(date, formatter).isAfter(LocalDateTime.now()))
            throw new TutorException(TOURNAMENT_NOT_CONSISTENT, "Available Date");

        tournament.setAvailableDate(LocalDateTime.parse(date, formatter));
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

        if(tournament.checkStatus() != Tournament.TournamentStatus.CREATED)
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

        if(tournament.checkStatus() != Tournament.TournamentStatus.CREATED)
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
    public TournamentDto findById(int tournamentId, int executionId) {
        List<TournamentDto> tournament = tournamentRepository.findById(tournamentId).stream().filter(t -> t.getCourseExecution().getId()
                .equals(executionId)).map(TournamentDto::new).collect(Collectors.toList());

        if(tournament.isEmpty())
            throw new TutorException(TOURNAMENT_NOT_FOUND, tournamentId);

        return tournament.get(0);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> getTournaments() {
        return tournamentRepository.findAll().stream().map(TournamentDto::new).sorted(Comparator
                .comparing(TournamentDto::getTitle)).collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void removeTournament(Integer tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));

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

       if(tournament.checkStatus() == Tournament.TournamentStatus.OPEN)
           throw new TutorException(TOURNAMENT_INVALID_STATUS, "open");

       tournament.setStatus(Tournament.TournamentStatus.CANCELED);
       tournament.checkStatus();

       return new TournamentDto(tournament);
    }

    public List<Question> filterByAssessment(List<Question> availableQuestions, Tournament tournament) {
        Assessment assessment = assessmentRepository.findById(Integer.valueOf(tournament.getAssessment().getId()))
                .orElseThrow(() -> new TutorException(ASSESSMENT_NOT_FOUND,tournament.getAssessment().getId()));

        return availableQuestions.stream().filter(question -> question.belongsToAssessment(assessment)).collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StatementQuizDto generateTournamentQuiz(String username, int tournamentId) {
        User user = userRepository.findByUsername(username);
        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
        int executionId = tournament.getCourseExecution().getId();
        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));

        Quiz quiz = new Quiz();
        quiz.setKey(quizService.getMaxQuizKey() + 1);
        quiz.setType(Quiz.QuizType.GENERATED); //TODO MUDAT ESTE TYPE??
        quiz.setCreationDate(LocalDateTime.now());
        quiz.setAvailableDate(tournament.getAvailableDate());
        quiz.setConclusionDate(tournament.getConclusionDate());

        List<Question> availableQuestions = questionRepository.findAvailableQuestions(courseExecution.getCourse().getId());

        if(tournament.getAssessment().getId() != null) {
            availableQuestions = filterByAssessment(availableQuestions, tournament);
        }
        // TODO else use default assessment

        if (availableQuestions.size() < tournament.getNumberOfQuestions()) {
            throw new TutorException(NOT_ENOUGH_QUESTIONS);
        }

        availableQuestions = user.filterQuestionsByStudentModel(tournament.getNumberOfQuestions(), availableQuestions);

        quiz.generate(availableQuestions);

        QuizAnswer quizAnswer = new QuizAnswer(user, quiz);

        quiz.setCourseExecution(courseExecution);
        courseExecution.addQuiz(quiz);

        quizRepository.save(quiz);
        quizAnswerRepository.save(quizAnswer);

        tournament.setQuiz(quiz);

        return new StatementQuizDto(quizAnswer);
    }
}
