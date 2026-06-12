package com.example.tts_in_spring.controller;

import com.example.tts_in_spring.dto.participant.ParticipantResponse;
import com.example.tts_in_spring.mapper.ParticipantMapper;
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

    @Autowired
    ParticipantMapper participantMapper;

    @GetMapping
    // TODO: Uncomment for prod
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ParticipantResponse>> getAllParticipants() {
        List<ParticipantResponse> participants = participantRepository.findAll()
                .stream()
                .map(p -> participantMapper.toResponse(p))
                .toList();

        return ResponseEntity.ok(participants);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParticipantResponse> getParticipant(Long id) {
        return participantRepository.findById(id)
                .map(p -> ResponseEntity.ok(participantMapper.toResponse(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<?> createParticipant(@RequestBody Participant incomingParticipant) {
        Participant savedParticipant = participantRepository.save(incomingParticipant);

        return participantRepository.findById(savedParticipant.getId())
                .map(p -> ResponseEntity.status(HttpStatus.CREATED).body(participantMapper.toResponseLite(p)))
                .orElse(ResponseEntity.notFound().build());
    }
}
