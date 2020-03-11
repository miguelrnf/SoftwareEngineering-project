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
    public TournamentDto createTournament(int executionId, TournamentDto tournamentDto, int assessmentId){
        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");



        System.out.println("=======================");
        System.out.println(courseExecution.getAssessments().toString());
        System.out.println("=======================");

        if (tournamentDto.getKey() == null) {
            tournamentDto.setKey(getMaxTournamentKey() + 1);
        }
        if(tournamentDto.getOwner() == null || tournamentDto.getOwner().getUsername() == null){
            throw new TutorException(TOURNAMENT_NOT_CONSISTENT, "Owner");
        }

        User user = userRepository.findByUsername(tournamentDto.getOwner().getUsername());

        if(user == null){
            throw new TutorException(USERNAME_NOT_FOUND, tournamentDto.getOwner().getUsername());
        }

        if(user.getRole() != User.Role.STUDENT){
            throw new TutorException(TOURNAMENT_PERMISSION);
        }

        if(tournamentDto.getTitle() == null || tournamentDto.getTitle().isBlank()){
            throw new TutorException(TOURNAMENT_NOT_CONSISTENT,  "Title");
        }

        List<Assessment> assessmentL = assessmentRepository.findByExecutionCourseId(executionId).stream().filter(a -> a.getId() == assessmentId).collect(Collectors.toList());

        if (assessmentL.isEmpty()){
            throw new TutorException(ASSESSMENT_NOT_FOUND, assessmentId);
        }

        Assessment assessment = assessmentL.get(0);

        Tournament tournament = new Tournament(tournamentDto, user, assessment);

        if(!courseExecution.getAssessments().contains(assessment)){
            throw new TutorException(ASSESSMENT_NOT_FOUND, assessmentId);
        }

        if(assessment.getTopicConjunctions().isEmpty()){
            throw new TutorException(TOPIC_CONJUNCTION_NOT_FOUND);
        }

        courseExecution.addTournament(tournament);
        tournament.setCourseExecution(courseExecution);

        if(tournamentDto.getConclusionDate() == null || tournamentDto.getAvailableDate() == null){
            throw new TutorException(TOURNAMENT_NOT_CONSISTENT, "Date");
        }

        if (tournamentDto.getCreationDate() == null) {
            tournament.setCreationDate(LocalDateTime.now());
        } else {
            tournament.setCreationDate(LocalDateTime.parse(tournamentDto.getCreationDate(), formatter));
        }

        tournament.setAvailableDate(LocalDateTime.parse(tournamentDto.getAvailableDate(), formatter));
        tournament.setConclusionDate(LocalDateTime.parse(tournamentDto.getConclusionDate(), formatter));


        entityManager.persist(tournament);

        return new TournamentDto(tournament);
    }

}
