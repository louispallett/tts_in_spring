package com.example.tts_in_spring.participant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/participant")
public class ParticipantController {
    @Autowired
    private ParticipantService participantService;

    @GetMapping
    // TODO: Uncomment for prod
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ParticipantResponse>> getAllParticipants() {
        return ResponseEntity.ok(participantService.getAllParticipants());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParticipantResponse> getParticipant(Long id) {
        return ResponseEntity.ok(participantService.getParticipantById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createParticipant(@RequestBody ParticipantRequest participantRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(participantService.createParticipant(participantRequest));
    }
}
