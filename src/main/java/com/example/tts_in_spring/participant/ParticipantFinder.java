package com.example.tts_in_spring.participant;

import com.example.tts_in_spring.exception.ConflictException;
import com.example.tts_in_spring.exception.ResourceNotFoundException;
import com.example.tts_in_spring.exception.ForbiddenException;
import com.example.tts_in_spring.match.Match;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public void assertParticipants(List<Participant> participants, Match match) {
        Set<Long> matchParticipantIds = match.getParticipants().stream()
                .map(Participant::getId)
                .collect(Collectors.toSet());

        boolean hasInvalidPlayer = participants.stream()
                .map(Participant::getId)
                .anyMatch(id -> !matchParticipantIds.contains(id));

        if (hasInvalidPlayer)
            throw new ConflictException("Participant in request is not part of this match");
    }
}
