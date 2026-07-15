package com.example.tts_in_spring.tournament;

import com.example.tts_in_spring.category.Category;
import com.example.tts_in_spring.category.Type;
import com.example.tts_in_spring.exception.IllegalStageException;
import com.example.tts_in_spring.match.Match;
import com.example.tts_in_spring.match.State;
import com.example.tts_in_spring.notification.NotificationService;
import com.example.tts_in_spring.player.Player;
import com.example.tts_in_spring.player.PlayerService;
import com.example.tts_in_spring.tournament.dto.TournamentResponseLite;
import com.example.tts_in_spring.tournament.dto.ValidateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TournamentStageService {
    private final TournamentRepository tournamentRepository;
    private final TournamentMapper tournamentMapper;
    private final TournamentFinder tournamentFinder;
    private final PlayerService playerService;
    private final NotificationService notificationService;

    public ValidateResponse validate(Long id, Long userId) {
        Tournament tournament = tournamentFinder.getTournamentOrThrow(id);
        tournamentFinder.assertHost(tournament, userId);

        boolean doublesHaveEightPlayers = true;
        boolean doublesHaveEvenPlayers = true;
        boolean singlesHaveFourPlayers = true;
        boolean mixedHasEqualMaleAndFemale = true;

        for (Category category : tournament.getCategories()) {
            if (category.isDoubles()) {
                if (category.getPlayers().size() < 8) {
                    doublesHaveEightPlayers = false;
                }
                if (category.getPlayers().size() % 2 != 0) {
                    doublesHaveEvenPlayers = false;
                }
            } else {
                if (category.getPlayers().size() < 4) {
                    singlesHaveFourPlayers = false;
                }
            }
            if (category.getName().equals(Type.MIXED_DOUBLES)) {
                Long males = category.getPlayers().stream().filter(Player::isMale).count();
                Long females = category.getPlayers().size() - males;

                if (!males.equals(females)) {
                    mixedHasEqualMaleAndFemale = false;
                }
            }
        }

        return new ValidateResponse(
                doublesHaveEightPlayers,
                doublesHaveEvenPlayers,
                singlesHaveFourPlayers,
                mixedHasEqualMaleAndFemale
        );
    }

    private void validateNextTransition(Tournament tournament) {
        switch (tournament.getStage()) {
            case REGISTRATION -> {
                ValidateResponse expected = new ValidateResponse(true, true, true, true);
                ValidateResponse response = validate(tournament.getId(), tournament.getHost().getId());

                if (!response.equals(expected)) {
                    throw new IllegalStageException("Tournament invalid for stage DRAW");
                }
            }
            case DRAW -> {
                for (Category category : tournament.getCategories()) {
                    if (category.getMatches().isEmpty()) {
                        throw new IllegalStageException("Tournament invalid for stage PLAY");
                    }
                }
            }
            case PLAY -> {
                for (Category category : tournament.getCategories()) {
                    List<Match> unfinishedMatches = category.getMatches().stream().filter(m -> !m.getState().equals(State.SCORE_DONE)).toList();
                    if (!unfinishedMatches.isEmpty()) {
                        throw new IllegalStageException("Tournament invalid for stage FINISHED");
                    }
                }
            }
        }
    }

    private void validatePreviousTransition(Tournament tournament) {
        switch (tournament.getStage()) {
            case DRAW -> {
                for (Category category : tournament.getCategories()) {
                    if (!category.getTeams().isEmpty() || !category.getMatches().isEmpty()) {
                        throw new IllegalStageException("Tournament invalid for stage REGISTRATION");
                    }
                }
            }
            case PLAY -> {
                for (Category category : tournament.getCategories()) {
                    List<Match> finishedMatches = category.getMatches().stream().filter(m -> m.getState().equals(State.SCORE_DONE)).toList();
                    if (!finishedMatches.isEmpty()) {
                        throw new IllegalStageException("Tournament invalid for stage DRAW");
                    }
                }
            }
        }
    }


    @Transactional
    public TournamentResponseLite nextStage(Long id, Long userId) {
        Tournament tournament = tournamentFinder.getTournamentOrThrow(id);
        tournamentFinder.assertHost(tournament, userId);

        Stage current = tournament.getStage();

        Stage next = switch (current) {
            case REGISTRATION -> Stage.DRAW;
            case DRAW -> Stage.PLAY;
            case PLAY -> Stage.FINISHED;
            case FINISHED -> throw new IllegalStageException("Tournament is already at final stage");
        };

        validateNextTransition(tournament);

        if (next == Stage.FINISHED) {
            playerService.wipeMobiles(tournament.getCategories());
        }

        tournament.setStage(next);

        notificationService.handleNotificationForStage(tournament);

        return tournamentMapper.toResponseLite(tournamentRepository.save(tournament));
    }

    @Transactional
    public TournamentResponseLite previousStage(Long id, Long userId) {
        Tournament tournament = tournamentFinder.getTournamentOrThrow(id);
        tournamentFinder.assertHost(tournament, userId);

        Stage current = tournament.getStage();

        Stage prev = switch (current) {
            case REGISTRATION -> throw new IllegalStageException("Tournament already at first stage");
            case DRAW -> Stage.REGISTRATION;
            case PLAY -> Stage.DRAW;
            case FINISHED -> Stage.PLAY;
        };

        validatePreviousTransition(tournament);

        tournament.setStage(prev);
        return tournamentMapper.toResponseLite(tournamentRepository.save(tournament));
    }
}
