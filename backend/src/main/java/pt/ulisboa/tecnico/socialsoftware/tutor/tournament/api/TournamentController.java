package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;

@RestController
public class TournamentController {

    @Autowired
    private TournamentService tournamentservice;

    TournamentController(TournamentService tournamentService) {
        this.tournamentservice = tournamentService;
    }

    @PostMapping("/executions/{executionId}/tournaments")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public TournamentDto createTournament(@Valid @RequestBody TournamentDto tournamentDto, @PathVariable Integer executionId) {
        formatDates(tournamentDto);
        return tournamentservice.createTournament(executionId, tournamentDto);
    }

    private void formatDates(TournamentDto tournament) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        if (tournament.getAvailableDate() != null && !tournament.getAvailableDate().matches("(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2})")){
            tournament.setAvailableDate(LocalDateTime.parse(tournament.getAvailableDate().replaceAll(".$", ""), DateTimeFormatter.ISO_DATE_TIME).format(formatter));
        }

        if (tournament.getConclusionDate() !=null && !tournament.getConclusionDate().matches("(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2})"))
            tournament.setConclusionDate(LocalDateTime.parse(tournament.getConclusionDate().replaceAll(".$", ""), DateTimeFormatter.ISO_DATE_TIME).format(formatter));
    }

    @PutMapping("/tournament/{tournamentId}/opened/enroll")
    @PreAuthorize("(hasRole('ROLE_STUDENT') and hasPermission(#tournamentId, 'TOURNAMENT.ACCESS'))")
    public TournamentDto enrollStudent(Principal principal, @PathVariable int tournamentId){
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return tournamentservice.enrollStudent(user.getUsername(), tournamentId);
    }

    @GetMapping("/executions/{executionId}/tournaments/open")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<TournamentDto> getOpenTournaments(@PathVariable Integer executionId){
        return tournamentservice.listTournaments(executionId);
    }

    @GetMapping("/executions/{executionId}/tournaments")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<TournamentDto> getTournaments(@PathVariable Integer executionId){
        return tournamentservice.getTournaments(executionId);
    }

    @GetMapping("/executions/{executionId}/tournaments/{tournamentId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public TournamentDto getTournament(@PathVariable Integer tournamentId, @PathVariable Integer executionId) {
        return this.tournamentservice.findById(tournamentId, executionId);
    }

    @PutMapping("/tournament/{tournamentId}/opened/unenroll")
    @PreAuthorize("(hasRole('ROLE_STUDENT') and hasPermission(#tournamentId, 'TOURNAMENT.ACCESS'))")
    public TournamentDto unenrollStudent(Principal principal, @PathVariable int tournamentId){
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null)
            throw new TutorException(AUTHENTICATION_ERROR);

        return this.tournamentservice.unrollStudent(user.getUsername(), tournamentId);
    }
}

