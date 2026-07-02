package com.example.tts_in_spring.tournament;

import com.example.tts_in_spring.security.UserPrincipal;
import com.example.tts_in_spring.tournament.dto.*;
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
@RequestMapping("/api/tournament")
public class TournamentController {
    private final TournamentService tournamentService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TournamentResponse>> getAllTournaments() {
        return ResponseEntity.ok(tournamentService.getAllTournaments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TournamentResponse> getTournament(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(tournamentService.getTournamentById(id, user.userId()));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTournament(
            @RequestBody TournamentRequest tournamentRequest,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tournamentService.createTournament(tournamentRequest, user.userId()));
    }

    @PatchMapping("/{id}/update-name")
    public ResponseEntity<TournamentResponseLite> updateName(
            @PathVariable Long id,
            @Valid @RequestBody TournamentNameUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(tournamentService.updateName(id, request, principal.userId()));
    }

    @PatchMapping("/{id}/update-stage")
    public ResponseEntity<TournamentResponseLite> updateStage(
            @PathVariable Long id,
            @Valid @RequestBody TournamentStageUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(tournamentService.updateStage(id, request, principal.userId()));
    }

    @PatchMapping("/{id}/update-showMobile")
    public ResponseEntity<TournamentResponseLite> updateShowMobile(
            @PathVariable Long id,
            @Valid @RequestBody TournamentShowMobileUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(tournamentService.updateShowMobile(id, request, principal.userId()));
    }

    @PostMapping("/check-code")
    public ResponseEntity<TournamentResponseLite> checkCode(
            @Valid @RequestBody TournamentCheckCodeRequest request
    ) {
        return ResponseEntity.ok(tournamentService.checkCode(request));
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        tournamentService.delete(id, principal.userId());
        return ResponseEntity.noContent().build();
    }
}