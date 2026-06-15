package com.example.tts_in_spring.participant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ParticipantService {
    @Autowired
    ParticipantRepository participantRepository;

    @Autowired
    ParticipantMapper participantMapper;

    public List<ParticipantResponse> getAllParticipants() {
        return participantRepository.findAll()
                .stream()
                .map(participantMapper::toResponse)
                .toList();
    }

    public ParticipantResponse getParticipantById(Long id) {
        Participant participant = participantRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Participant not found"));
        return participantMapper.toResponse(participant);
    }

    public ParticipantResponseLite createParticipant(ParticipantRequest participantRequest) {
        Participant validatedParticipant = new Participant();

        validatedParticipant.setResultText("");
        validatedParticipant.setWinner(false);
        validatedParticipant.setStatus("PENDING");
        validatedParticipant.setTeam(participantRequest.getTeam());
        validatedParticipant.setPlayer(participantRequest.getPlayer());
        validatedParticipant.setMatch(participantRequest.getMatch());

        Participant savedParticipant = participantRepository.save(validatedParticipant);
        return participantMapper.toResponseLite(savedParticipant);
    }
}
