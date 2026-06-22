package com.example.tts_in_spring.participant;

import com.example.tts_in_spring.participant.dto.*;
import com.example.tts_in_spring.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/participant")
public class ParticipantController {
    private final ParticipantService participantService;

    @GetMapping
    // TODO: Uncomment for prod
    //@PreAuthorize("hasRole('ADMIN')")
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
            @Valid @RequestBody ParticipantUpdateResultTextRequest request,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(participantService.updateResultText(id, request, user.userId()));
    }

    @PatchMapping("/{id}/update-is-winner")
    public ResponseEntity<ParticipantResponseLite> updateIsWinner(
            @PathVariable Long id,
            @Valid @RequestBody ParticipantUpdateWinnerRequest request,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(participantService.updateIsWinner(id, request, user.userId()));
    }

    @PatchMapping("/{id}/update-status")
    public ResponseEntity<ParticipantResponseLite> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ParticipantUpdateStatusRequest request,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(participantService.updateStatus(id, request, user.userId()));
    }
}
