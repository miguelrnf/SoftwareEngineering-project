package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
    public TournamentDto createTournament(Principal principal, @Valid @RequestBody TournamentDto tournamentDto, @PathVariable Integer executionId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        formatDates(tournamentDto);
        tournamentDto.setOwner(new UserDto(user));
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

    @GetMapping("/tournaments")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DEMO_ADMIN')")
    public List<TournamentDto> getTournaments(){
        return tournamentservice.getTournaments();
    }

    @GetMapping("/executions/{executionId}/tournaments/{tournamentId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public TournamentDto getTournament(@PathVariable Integer tournamentId, @PathVariable Integer executionId) {
        return this.tournamentservice.findById(tournamentId, executionId);
    }

    @GetMapping("/executions/{executionId}/tournaments/own/{username}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<TournamentDto> getOwnTournament(@PathVariable String username, @PathVariable Integer executionId) {
        return this.tournamentservice.getOwnTournaments(username, executionId);
    }

    @PutMapping("/tournament/{tournamentId}/opened/unenroll")
    @PreAuthorize("(hasRole('ROLE_STUDENT') and hasPermission(#tournamentId, 'TOURNAMENT.ACCESS'))")
    public TournamentDto unenrollStudent(Principal principal, @PathVariable int tournamentId){
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null)
            throw new TutorException(AUTHENTICATION_ERROR);

        return this.tournamentservice.unrollStudent(user.getUsername(), tournamentId);
    }

    @GetMapping("/executions/{executionId}/tournaments/enrolled/{username}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<TournamentDto> getEnrolledTournaments(@PathVariable String username, @PathVariable Integer executionId) {
        return this.tournamentservice.getEnrolledTournaments(username, executionId);
    }

    @DeleteMapping("/tournaments/{tournamentId}/delete")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity deleteTournament(@PathVariable Integer tournamentId) {
        tournamentservice.removeTournament(tournamentId);
        System.out.println("DELETEDDDD" + tournamentId);

        return ResponseEntity.ok().build();
    }
}
