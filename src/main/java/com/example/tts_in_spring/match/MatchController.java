package com.example.tts_in_spring.match;

import com.example.tts_in_spring.match.dto.*;
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
@RequestMapping("/api/match")
public class MatchController {
    private final MatchService matchService;
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

    @PostMapping("/create")
    public ResponseEntity<?> createMatch(
            @Valid @RequestBody MatchRequest request,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(matchService.createMatch(request, user.userId()));
    }

    @PostMapping("/{categoryId}/generate")
    public ResponseEntity<List<MatchResponse>> generateMatches(
            @PathVariable Long categoryId,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(matchService.generateMatchesParent(categoryId, user.userId()));
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

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        matchService.delete(id, principal.userId());
        return ResponseEntity.noContent().build();
    }
}
