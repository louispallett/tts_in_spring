package com.example.tts_in_spring.match;

import com.example.tts_in_spring.category.Category;
import com.example.tts_in_spring.category.CategoryFinder;
import com.example.tts_in_spring.exception.ForbiddenException;
import com.example.tts_in_spring.exception.GenericBadRequestException;
import com.example.tts_in_spring.exception.ResourceNotFoundException;
import com.example.tts_in_spring.match.dto.*;
import com.example.tts_in_spring.participant.Participant;
import com.example.tts_in_spring.player.Player;
import com.example.tts_in_spring.tournament.Stage;
import com.example.tts_in_spring.user.User;
import com.example.tts_in_spring.user.UserFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchService {
    private final MatchRepository matchRepository;
    private final MatchMapper matchMapper;
    private final MatchFinder matchFinder;
    private final CategoryFinder categoryFinder;
    private final UserFinder userFinder;

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
        if (matchFinder.isHost(match, userId)) {
            return matchMapper.toResponse(match);
        }

        if (!match.getCategory().getTournament().getStage().equals(Stage.DRAW) && matchFinder.isParticipant(match, userId)) {
            return matchMapper.toResponse(match);
        }

        throw new ForbiddenException("Not host of tournament or participant in match");
    }

    public List<MatchResponse> getUserMatchesByCategory(Long tournamentId, Long userId) {
        User user = userFinder.getUserOrThrow(userId);
        List<Player> players = user.getPlayers().stream().filter(p -> p.getCategory().getTournament().getId().equals(tournamentId)).toList();
        List<Match> matches = new ArrayList<>();

        for (Player player : players) {
            if (player.getTeam() == null) {
                List<Participant> participants = player.getParticipants();
                for (Participant participant : participants) {
                    matches.add(participant.getMatch());
                }
            } else {
                List<Participant> participants = player.getTeam().getParticipants();
                for (Participant participant : participants) {
                    matches.add(participant.getMatch());
                }
            }
        }

        return matches.stream().map(matchMapper::toResponse).toList();
    }

    @Transactional
    public List<MatchResponseLite> submitDeadlinesByRound(
            Long categoryId,
            MatchUpdateDeadlinesByRoundRequest request,
            Long userId
    ) {
        Category category = categoryFinder.getCategoryOrThrow(categoryId);
        categoryFinder.assertHost(category, userId);

        if (category.getMatches().isEmpty())
            throw new ResourceNotFoundException("No matches in this category");

        Map<String, DeadlineByRoundRequest> requestByRound = request.rounds()
                .stream()
                .collect(Collectors.toMap(
                        DeadlineByRoundRequest::tournamentRoundText,
                        Function.identity()
                ));

        List<MatchResponseLite> updatedMatches = new ArrayList<>();

        for (Match match : category.getMatches()) {
            DeadlineByRoundRequest roundRequest = requestByRound.get(match.getTournamentRoundText());

            if (roundRequest == null)
                throw new GenericBadRequestException("No deadline supplied for round " + match.getTournamentRoundText());

            updatedMatches.add(updateDeadline(
                    match.getId(),
                    new MatchUpdateDeadlineRequest(roundRequest.deadline()),
                    userId
            ));
        }

        return updatedMatches;
    }

    @Transactional
    public MatchResponseLite updateDeadline(
            Long id,
            MatchUpdateDeadlineRequest request,
            Long userId
    ) {
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
