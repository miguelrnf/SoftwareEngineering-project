package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class TournamentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

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

        if (tournamentDto.getKey() == null) {
            tournamentDto.setKey(getMaxTournamentKey() + 1);
        }
        if(tournamentDto.getOwner() == null || tournamentDto.getOwner().getUsername() == null){
            throw new TutorException(TOURNAMENT_NOT_CONSISTENT, "Owner");
        }

        User user = findUsername(tournamentDto.getOwner().getUsername());

        if(user.getRole() != User.Role.STUDENT){
            throw new TutorException(TOURNAMENT_PERMISSION);
        }

        if(tournamentDto.getTitle() == null || tournamentDto.getTitle().isBlank()){
            throw new TutorException(TOURNAMENT_NOT_CONSISTENT,  "Title");
        }
        Tournament tournament = new Tournament(tournamentDto, user);
        courseExecution.addTournament(tournament);
        tournament.setCourseExecution(courseExecution);



        entityManager.persist(tournament);

        return new TournamentDto(tournament);
    }

    public void enrollStudent(int courseExecutionId, String username, int tournamentId) {
        User user = findUsername(username);

        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));

        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, courseExecutionId));

        if(user.getRole() != User.Role.STUDENT){
            throw new TutorException(TOURNAMENT_PERMISSION_ENROLL);
        }

        if(tournament.getStatus() != Tournament.TournamentStatus.CREATED){
            throw new TutorException(TOURNAMENT_NOT_AVAILABLE);
        }

         if(courseExecution != tournament.getCourseExecution()){
             throw new TutorException(TOURNAMENT_NOT_AVAILABLE);
         }

         tournament.enrollStudent(user);
         user.addTournament(tournament);
    }

    public User findUsername(String username){
        User user = userRepository.findByUsername(username);

        if(user == null ){
            throw new TutorException(USERNAME_NOT_FOUND, username);
        }
        return user;
    }

    public void unrollStudent(String username, int tournamentId){
        User user = findUsername(username);
        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));

        if(tournament.getStatus() != Tournament.TournamentStatus.CREATED){
            throw new TutorException(TOURNAMENT_NOT_AVAILABLE);
        }

        if(!tournament.getEnrolledStudents().contains(user)){
            throw new TutorException(UNABLE_TO_UNROLL, user.getUsername());
        }

        tournament.getEnrolledStudents().remove(user);
        user.getTournaments().remove(tournament);

    }

}
