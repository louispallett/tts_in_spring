package com.example.tts_in_spring.tournament;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TournamentFinder {
    private final TournamentRepository tournamentRepository;

    public Tournament getTournamentOrThrow(Long id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Tournament not found"
                ));
    }

    public void assertHost(Tournament tournament, Long userId) {
        if (!tournament.getHost().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}