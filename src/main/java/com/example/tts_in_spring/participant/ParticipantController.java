package com.example.tts_in_spring.participant;

import com.example.tts_in_spring.participant.dto.*;
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
@RequestMapping("/api/participant")
public class ParticipantController {
    private final ParticipantService participantService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ParticipantResponse>> getAllParticipants() {
        return ResponseEntity.ok(participantService.getAllParticipants());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParticipantResponse> getParticipant(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(participantService.getParticipantById(id, user.userId()));
    }

    @PatchMapping("/{id}/update-result-text")
    public ResponseEntity<ParticipantResponseLite> updateResultText(
            @PathVariable Long id,
            @Valid @RequestBody UpdateResultTextRequest request,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(participantService.updateResultText(id, request, user.userId()));
    }

    @PatchMapping("/{id}/update-is-winner")
    public ResponseEntity<ParticipantResponseLite> updateWinner(
            @PathVariable Long id,
            @Valid @RequestBody ParticipantUpdateWinnerRequest request,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(participantService.updateWinner(id, request, user.userId()));
    }

    @PatchMapping("/{id}/update-status")
    public ResponseEntity<ParticipantResponseLite> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ParticipantUpdateStatusRequest request,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(participantService.updateStatus(id, request, user.userId()));
    }

    @PostMapping("/{id}/replace")
    public ResponseEntity<ParticipantResponseLite> replace(
            @PathVariable Long id,
            @Valid @RequestBody ParticipantChangeMatchRequest request,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(participantService.changeParticipantMatch(id, request, user.userId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        participantService.delete(id, principal.userId());
        return ResponseEntity.noContent().build();
    }
}
