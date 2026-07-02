package com.example.tts_in_spring.player;

import com.example.tts_in_spring.player.dto.*;
import com.example.tts_in_spring.security.UserPrincipal;
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
@RequestMapping("/api/player")
public class PlayerController {
    private final PlayerService playerService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PlayerResponse>> getAllPlayers() {
        return ResponseEntity.ok(playerService.getAllPlayers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerResponse> getPlayer(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(playerService.getPlayerById(id, user.userId()));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPlayer(
            @Valid @RequestBody PlayerRequest playerRequest,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(playerService.createPlayer(playerRequest, user.userId()));
    }

    @PostMapping("/join-tournament")
    public ResponseEntity<?> joinTournament(
            @Valid @RequestBody JoinTournamentRequest request,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(playerService.joinTournament(request, user.userId()));
    }

    @PatchMapping("/{id}/update-rank")
    public ResponseEntity<PlayerResponseLite> updateRank(
            @PathVariable Long id,
            @Valid @RequestBody PlayerUpdateRankRequest request,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(playerService.updateRank(id, request, user.userId()));
    }

    @PatchMapping("/{id}/update-seeded")
    public ResponseEntity<PlayerResponseLite> updateSeeded(
            @PathVariable Long id,
            @Valid @RequestBody PlayerUpdateSeededRequest request,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(playerService.updateSeeded(id, request, user.userId()));
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        playerService.delete(id, user.userId());
        return ResponseEntity.noContent().build();
    }
}