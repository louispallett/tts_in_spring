package com.example.tts_in_spring.score;

import com.example.tts_in_spring.exception.ResourceNotFoundException;
import com.example.tts_in_spring.match.Match;
import com.example.tts_in_spring.participant.Participant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScoreFinder {
    private final ScoreRepository scoreRepository;

    public Score getScoreOrThrow(Long id) {
        return scoreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Score " + id + " not found"));
    }

    public Optional<Score> getScoreByMatch(Match match) {
        return scoreRepository.findByMatchId(match.getId());
    }

    public boolean isHost(Score score, Long userId) {
        return score.getMatch()
                .getCategory()
                .getTournament()
                .getHost()
                .getId()
                .equals(userId);
    }

    public boolean isParticipant(Score score, Long userId) {
        if (score.getMatch().getCategory().isDoubles()) {
            return score.getMatch().getParticipants().stream()
                    .map(Participant::getTeam)
                    .filter(Objects::nonNull)
                    .anyMatch(team -> team.getPlayers().stream()
                            .anyMatch(player -> player.getUser().getId().equals(userId)));
        } else {
            return score.getMatch().getParticipants().stream()
                    .anyMatch(p -> p.getPlayer().getUser().getId().equals(userId));
        }
    }
}
