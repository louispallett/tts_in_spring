package com.example.tts_in_spring.score;

import com.example.tts_in_spring.exception.ForbiddenException;
import com.example.tts_in_spring.match.Match;
import com.example.tts_in_spring.score.dto.ScoreResponse;
import com.example.tts_in_spring.user.UserFinder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScoreService {
    private final ScoreRepository scoreRepository;
    private final ScoreFinder scoreFinder;
    private final UserFinder userFinder;
    private final ScoreMapper scoreMapper;

    @Transactional(readOnly = true)
    public List<ScoreResponse> getAllScores() {
        return scoreRepository.findAll()
                .stream()
                .map(scoreMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ScoreResponse getScoreById(Long id, Long userId) {
        Score score = scoreFinder.getScoreOrThrow(id);

        if (scoreFinder.isHost(score, userId) || scoreFinder.isParticipant(score, userId)) {
            return scoreMapper.toResponse(score);
        }

        throw new ForbiddenException("Not host of tournament nor participant in match");
    }

    @Transactional
    public void create(Match match, Long userId) {
        scoreFinder.getScoreByMatch(match).ifPresent(this::delete);

        scoreRepository.save(new Score(match, userFinder.getUserOrThrow(userId)));
    }

    @Transactional
    public void delete(Score score) {
        scoreRepository.delete(score);
    }
}
