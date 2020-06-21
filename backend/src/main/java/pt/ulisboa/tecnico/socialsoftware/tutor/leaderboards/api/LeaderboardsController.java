package pt.ulisboa.tecnico.socialsoftware.tutor.leaderboards.api;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.leaderboards.LeaderboardsService;
import pt.ulisboa.tecnico.socialsoftware.tutor.leaderboards.dto.LeaderboardsDto;

@RestController
public class LeaderboardsController {

    private final LeaderboardsService leaderboardsService;

    public LeaderboardsController(LeaderboardsService leaderboardsService) {
        this.leaderboardsService = leaderboardsService;
    }

    @GetMapping("executions/{executionId}/leaderboards")
    @PreAuthorize("hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public LeaderboardsDto getLeaderboards(@PathVariable int executionId) {
        return leaderboardsService.getCurrentLeaderboards(executionId);
    }
}
