package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.api;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.security.Principal;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;

@RestController
public class TournamentController {
    private TournamentService tournamentservice;

    TournamentController(TournamentService tournamentService) {
        this.tournamentservice = tournamentService;
    }

    @PutMapping("/tournament/{tournamentId}/opened/enroll")
    @PreAuthorize("(hasRole('ROLE_STUDENT') and hasPermission(#tournamentId, 'TOURNAMENT.ACCESS'))")
    public TournamentDto enrollStudent(Principal principal, @PathVariable int tournamentId){
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }
        return this.tournamentservice.enrollStudent(user.getUsername(), tournamentId);
    }
}