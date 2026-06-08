package com.example.tts_in_spring.controller;

import com.example.tts_in_spring.dto.MatchResponse;
import com.example.tts_in_spring.dto.ParticipantResponse;
import com.example.tts_in_spring.dto.PlayerResponse;
import com.example.tts_in_spring.dto.TeamResponse;
import com.example.tts_in_spring.model.Participant;
import com.example.tts_in_spring.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/participant")
public class ParticipantController {
    @Autowired
    ParticipantRepository participantRepository;

    private ParticipantResponse mapToResponse(Participant participant) {
        ParticipantResponse participantResponse = new ParticipantResponse(participant);

        if (participant.getTeam() == null) {
            participantResponse.player = new PlayerResponse(participant.getPlayer());
        } else {
            participantResponse.team = new TeamResponse(participant.getTeam());
        }

        participantResponse.match = new MatchResponse(participant.getMatch());

        return participantResponse;
    }

    @GetMapping
    public ResponseEntity<List<ParticipantResponse>> getAllParticipants() {
        List<ParticipantResponse> participants = participantRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return ResponseEntity.ok(participants);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParticipantResponse> getParticipant(Long id) {
        return participantRepository.findById(id)
                .map(participant -> ResponseEntity.ok(mapToResponse(participant)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<?> createParticipant(@RequestBody Participant incomingParticipant) {
        Participant savedParticipant = participantRepository.save(incomingParticipant);

        return participantRepository.findById(savedParticipant.getId())
                .map(participant -> ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(participant)))
                .orElse(ResponseEntity.notFound().build());
    }
}
