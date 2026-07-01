package com.example.tts_in_spring.tournament;

import com.example.tts_in_spring.exception.ResourceNotFoundException;
import com.example.tts_in_spring.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TournamentFinder {
    private final TournamentRepository tournamentRepository;

    public Tournament getTournamentOrThrow(Long id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament " + id + " not found"));
    }

    public Tournament getTournamentByCodeOrThrow(String code) {
        return tournamentRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Tournament Code"));
    }

    public void assertHost(Tournament tournament, Long userId) {
        if (!tournament.getHost().getId().equals(userId)) {
            throw new ForbiddenException(
                    "Not host of tournament "
                            + tournament.getName()
                            + " ("
                            + tournament.getId()
                            + ")"
            );
        }
    }
}