package com.example.tts_in_spring.match;

import com.example.tts_in_spring.match.dto.*;
import com.example.tts_in_spring.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/match")
public class MatchController {
    private final MatchService matchService;
    private final MatchGenerationService matchGenerationService;
    private final MatchScoreSubmissionService matchScoreSubmissionService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MatchResponse>> getAllMatches() {
        return ResponseEntity.ok(matchService.getAllMatches());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchResponse> getMatch(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(matchService.getMatchById(id, user.userId()));
    }

    @PostMapping("/{categoryId}/generate")
    public ResponseEntity<List<MatchResponse>> generateMatches(
            @PathVariable Long categoryId,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(matchGenerationService.generateMatchesParent(categoryId, user.userId()));
    }

    @PatchMapping("/{categoryId}/submit-deadlines-by-round")
    public ResponseEntity<List<MatchResponseLite>> submitDeadlinesByRound(
            @PathVariable Long categoryId,
            @Valid @RequestBody MatchUpdateDeadlinesByRoundRequest request,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(matchService.submitDeadlinesByRound(categoryId, request, user.userId()));
    }

    @PostMapping("/{id}/submit-score")
    public ResponseEntity<MatchResponse> submitScore(
            @PathVariable Long id,
            @Valid @RequestBody SubmitScoreRequest request,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(matchScoreSubmissionService.submitScore(id, request, user.userId()));
    }

    @PatchMapping("/{id}/update-deadline")
    public ResponseEntity<MatchResponseLite> updateDeadline(
            @PathVariable Long id,
            @Valid @RequestBody MatchUpdateDeadlineRequest request,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(matchService.updateDeadline(id, request, user.userId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        matchService.delete(id, principal.userId());
        return ResponseEntity.noContent().build();
    }
}
