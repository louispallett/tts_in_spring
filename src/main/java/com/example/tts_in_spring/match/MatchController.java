package com.example.tts_in_spring.match;

import com.example.tts_in_spring.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/match")
public class MatchController {
    private final MatchService matchService;

    @GetMapping
    // TODO: Uncomment for prod
    //@PreAuthorize("hasRole('ADMIN')")
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

    @PatchMapping("/{id}/submit-score")
    public ResponseEntity<MatchResponseLite> submitScore(
            @PathVariable Long id,
            @Valid @RequestBody MatchSubmitScoreRequest request,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(matchService.submitScore(id, request, user.userId()));
    }

    @PatchMapping("/{id}/update-deadline")
    public ResponseEntity<MatchResponseLite> updateDeadline(
            @PathVariable Long id,
            @Valid @RequestBody MatchUpdateDeadlineRequest request,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(matchService.updateDeadline(id, request, user.userId()));
    }
}
