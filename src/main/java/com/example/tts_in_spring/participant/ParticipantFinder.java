package com.example.tts_in_spring.participant;

import com.example.tts_in_spring.exception.ResourceNotFoundException;
import com.example.tts_in_spring.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParticipantFinder {
    private final ParticipantRepository participantRepository;

    public Participant getParticipantOrThrow(Long id) {
        return participantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Participant " + id + " not found"));
    }

    public void assertHost(Participant participant, Long userId) {
        if (!participant.getMatch().getCategory().getTournament().getHost().getId().equals(userId)) {
            throw new ForbiddenException("Not host of category " + participant.getMatch().getCategory().getId().toString());
        }
    }

    public boolean isParticipant(Participant participant, Long userId) {
        boolean isTeam = participant.getTeam() != null;
        if (isTeam) {
            return participant.getTeam().getPlayers().stream()
                    .anyMatch(p -> p.getUser().getId().equals(userId));
        }

        return participant.getPlayer().getUser().getId().equals(userId);
    }

    public boolean isHost(Participant participant, Long userId) {
        return participant.getMatch().getCategory().getTournament().getHost().getId().equals(userId);
    }
}
