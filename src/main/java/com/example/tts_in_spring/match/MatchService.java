package com.example.tts_in_spring.match;

import com.example.tts_in_spring.category.CategoryFinder;
import com.example.tts_in_spring.exception.ForbiddenException;
import com.example.tts_in_spring.match.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchService {
    private final MatchRepository matchRepository;
    private final MatchMapper matchMapper;
    private final CategoryFinder categoryFinder;
    private final MatchFinder matchFinder;

    @Transactional(readOnly = true)
    public List<MatchResponse> getAllMatches() {
        return matchRepository.findAll()
                .stream()
                .map(matchMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MatchResponse getMatchById(Long id, Long userId) {
        Match match = matchFinder.getMatchOrThrow(id);

        if (matchFinder.isHost(match, userId) || matchFinder.isParticipant(match, userId)) {
            return matchMapper.toResponse(match);
        }

        throw new ForbiddenException("Not host of tournament or participant in match");
    }

    @Transactional
    public MatchResponseLite updateDeadline(Long id, MatchUpdateDeadlineRequest request, Long userId) {
        Match match = matchFinder.getMatchOrThrow(id);
        matchFinder.assertHost(match, userId);

        matchMapper.updateDeadlineEntity(request, match);
        return matchMapper.toResponseLite(match);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Match match = matchFinder.getMatchOrThrow(id);
        matchFinder.assertHost(match, userId);

        matchRepository.delete(match);
    }
}
