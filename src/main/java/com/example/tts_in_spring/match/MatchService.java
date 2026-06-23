package com.example.tts_in_spring.match;

import com.example.tts_in_spring.category.Category;
import com.example.tts_in_spring.category.CategoryService;
import com.example.tts_in_spring.match.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchService {
    private final MatchRepository matchRepository;
    private final MatchMapper matchMapper;
    private final CategoryService categoryService;

    public Match getMatchOrThrow(Long id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not found"));
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

    @Transactional(readOnly = true)
    public List<MatchResponse> getAllMatches() {
        return matchRepository.findAll()
                .stream()
                .map(matchMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MatchResponse getMatchById(Long id, Long userId) {
        Match match = getMatchOrThrow(id);

        if (isHost(match, userId) || isParticipant(match, userId)) {
            return matchMapper.toResponse(match);
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    @Transactional
    public MatchResponse createMatch(MatchRequest request, Long userId) {
        Category category = categoryService.getCategoryOrThrow(request.categoryId());
        Match nextMatch = request.nextMatchId() == null ? null : getMatchOrThrow(request.nextMatchId());

        if (category.getTournament().getHost().getId().equals(userId)) {
            Match match = matchMapper.toEntity(request);
            match.setState("SCHEDULED");
            match.setCategory(category);
            match.setNextMatch(nextMatch);

            Match savedMatch = matchRepository.save(match);
            return matchMapper.toResponse(savedMatch);
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    @Transactional
    public MatchResponseLite submitScore(Long id, MatchSubmitScoreRequest request, Long userId) {
        Match match = getMatchOrThrow(id);

        if (isHost(match, userId) || isParticipant(match, userId)) {
            matchMapper.submitScoreEntity(request, match);

            Match savedMatch = matchRepository.save(match);
            return matchMapper.toResponseLite(savedMatch);
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    @Transactional
    public MatchResponseLite updateDeadline(Long id, MatchUpdateDeadlineRequest request, Long userId) {
        Match match = getMatchOrThrow(id);

        if (isHost(match, userId)) {
            matchMapper.updateDeadlineEntity(request, match);

            Match savedMatch = matchRepository.save(match);
            return matchMapper.toResponseLite(savedMatch);
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Match match = getMatchOrThrow(id);

        if (!isHost(match, userId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        matchRepository.delete(match);
    }
}
