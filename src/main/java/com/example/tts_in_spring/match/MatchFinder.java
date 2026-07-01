package com.example.tts_in_spring.match;

import com.example.tts_in_spring.exception.ResourceNotFoundException;
import com.example.tts_in_spring.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchFinder {
    private final MatchRepository matchRepository;

    public Match getMatchOrThrow(Long id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match " + id + " not found"));
    }

    public boolean isHost(Match match, Long userId) {
        return match.getCategory()
                .getTournament()
                .getHost()
                .getId()
                .equals(userId);
    }

    public boolean isParticipant(Match match, Long userId) {
        return match.getParticipants().stream()
                .anyMatch(p -> p.getPlayer().getUser().getId().equals(userId));
    }

    public void assertHost(Match match, Long userId) {
        if (!match.getCategory().getTournament().getHost().getId().equals(userId)) {
            throw new ForbiddenException(
                    "Not host of tournament " +
                            match.getCategory().getTournament().getName()
                            + " ("
                            + match.getCategory().getTournament().getId()
                            + ")"
            );
        }
    }
}
