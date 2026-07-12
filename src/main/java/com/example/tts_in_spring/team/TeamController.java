package com.example.tts_in_spring.team;

import com.example.tts_in_spring.player.dto.PlayerResponse;
import com.example.tts_in_spring.security.UserPrincipal;
import com.example.tts_in_spring.team.dto.TeamRequest;
import com.example.tts_in_spring.team.dto.TeamResponse;
import com.example.tts_in_spring.team.dto.TeamsRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/team")
public class TeamController {
    private final TeamService teamService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TeamResponse>> getAllTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> getTeam(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(teamService.getTeamById(id, user.userId()));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTeam(
            @Valid @RequestBody TeamRequest teamRequest,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.createTeam(teamRequest, user.userId()));
    }

    @PostMapping("/{categoryId}/generate")
    public ResponseEntity<List<List<PlayerResponse>>> generateTeams(
            @PathVariable Long categoryId,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(teamService.generateTeams(categoryId, user.userId()));
    }

    @PostMapping("/{categoryId}/save-all")
    public ResponseEntity<List<TeamResponse>> saveAllTeams(
            @PathVariable Long categoryId,
            @Valid @RequestBody TeamsRequest teams,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.saveTeams(categoryId, teams, user.userId()));
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        teamService.delete(id, principal.userId());
        return ResponseEntity.noContent().build();
    }
}
