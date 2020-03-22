package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.AssessmentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.AssessmentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @PersistenceContext
    EntityManager entityManager;

    public Integer getMaxTournamentKey() {
        Integer maxTournamentKey = tournamentRepository.getMaxTournamentKey();
        return maxTournamentKey != null ? maxTournamentKey : 0;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto createTournament(int executionId, TournamentDto tournamentDto){

        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        if (tournamentDto.getKey() == null) {
            int maxTournamentNumber = getMaxTournamentKey();
            tournamentDto.setKey(maxTournamentNumber + 1);
        }

        if(tournamentDto.getOwner() == null || tournamentDto.getOwner().getUsername() == null)
            throw new TutorException(TOURNAMENT_NOT_CONSISTENT, "Owner");

        if(tournamentDto.getNumberOfQuestions() == null || tournamentDto.getNumberOfQuestions() < 1)
            throw new TutorException(NOT_ENOUGH_QUESTIONS_TOURNAMENT);

        User user = findUsername(tournamentDto.getOwner().getUsername());


        if(user.getRole() != User.Role.STUDENT)
            throw new TutorException(TOURNAMENT_PERMISSION);

        if(tournamentDto.getTitle() == null || tournamentDto.getTitle().isBlank())
            throw new TutorException(TOURNAMENT_NOT_CONSISTENT,  "Title");

        Assessment assessment = checkAssessment(tournamentDto.getAssessmentDto(), courseExecution);

        Tournament tournament = new Tournament(tournamentDto, user, assessment);

        assignTournamentToExecution(tournament, courseExecution);

        setValidCreationDate(tournament, tournamentDto.getCreationDate(), formatter);
        setValidAvailableDate(tournament, tournamentDto.getAvailableDate(), formatter);
        setValidConclusionDate(tournament, tournamentDto.getConclusionDate(), formatter);

        entityManager.persist(tournament);

        return new TournamentDto(tournament);
    }

    private Assessment checkAssessment(AssessmentDto assessmentDto, CourseExecution courseExecution){

        if(assessmentDto == null)
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
        if(date == null)
            throw new TutorException(TOURNAMENT_NOT_CONSISTENT, "Date");

        tournament.setConclusionDate(LocalDateTime.parse(date, formatter));
    }

    private void setValidAvailableDate(Tournament tournament, String date, DateTimeFormatter formatter){
        if(date == null)
            throw new TutorException(TOURNAMENT_NOT_CONSISTENT, "Date");

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

    public void enrollStudent(int courseExecutionId, String username, int tournamentId) {
        User user = findUsername(username);

        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));

        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, courseExecutionId));

        if(user.getRole() != User.Role.STUDENT)
            throw new TutorException(TOURNAMENT_PERMISSION_ENROLL);

        if(tournament.getStatus() != Tournament.TournamentStatus.CREATED || tournament.getAvailableDate().isAfter(LocalDateTime.now()))
            throw new TutorException(TOURNAMENT_NOT_AVAILABLE);

        if(courseExecution != tournament.getCourseExecution())
            throw new TutorException(TOURNAMENT_NOT_AVAILABLE);

         tournament.enrollStudent(user);
         user.addTournament(tournament);
    }

    public void unrollStudent(String username, int tournamentId){
        User user = findUsername(username);
        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));

        if(tournament.getStatus() != Tournament.TournamentStatus.CREATED)
            throw new TutorException(TOURNAMENT_NOT_AVAILABLE);

        if(!tournament.getEnrolledStudents().contains(user))
            throw new TutorException(UNABLE_TO_UNROLL, user.getUsername());

        tournament.getEnrolledStudents().remove(user);
        user.getTournaments().remove(tournament);

    }

}
