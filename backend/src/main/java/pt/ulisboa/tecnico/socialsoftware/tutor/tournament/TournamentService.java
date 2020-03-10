package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;
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
import java.util.ArrayList;
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
        Tournament tournament = new Tournament(tournamentDto, user);
        courseExecution.addTournament(tournament);
        tournament.setCourseExecution(courseExecution);

        entityManager.persist(tournament);

        return new TournamentDto(tournament);
    }


    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> listTournaments(int executionId){
        List<TournamentDto> result;
        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));

        //TODO add date filters when added to project
        List<Tournament> temp = courseExecution.getTournaments().stream()
                .filter(t -> t.getStatus() == Tournament.TournamentStatus.CREATED).collect(Collectors.toList());


        result = temp.stream().map(TournamentDto::new).collect(Collectors.toList());

        if (result.isEmpty()){
            throw new TutorException(TOURNAMENT_LIST_EMPTY);
        }


        return result;
    }


}
