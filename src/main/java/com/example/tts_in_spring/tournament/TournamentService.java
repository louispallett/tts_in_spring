package com.example.tts_in_spring.tournament;

import com.example.tts_in_spring.category.Category;
import com.example.tts_in_spring.category.dto.CategoryRequest;
import com.example.tts_in_spring.category.CategoryService;
import com.example.tts_in_spring.exception.ForbiddenException;
import com.example.tts_in_spring.exception.IllegalStageException;
import com.example.tts_in_spring.match.Match;
import com.example.tts_in_spring.player.Player;
import com.example.tts_in_spring.tournament.dto.*;
import com.example.tts_in_spring.user.User;
import com.example.tts_in_spring.user.UserFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TournamentService {
    private final TournamentRepository tournamentRepository;
    private final UserFinder userFinder;
    private final TournamentMapper tournamentMapper;
    private final CategoryService categoryService;
    private final TournamentFinder tournamentFinder;


    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz0123456789";

    private static String generateRandomString() {
        int len = 12;
        StringBuilder sb = new StringBuilder(len);

        for (int i = 0; i < len; i++) {
            sb.append(ALPHABET.charAt(SECURE_RANDOM.nextInt(ALPHABET.length())));
        }

        return sb.toString();
    }

    public String generateCode(String name) {
        String code;
        String[] words = name.split("\\s+");

        String firstWord = words[0];

        if (firstWord.length() < 8) {
            code = firstWord + "_" + generateRandomString();
        } else {
            String truncatedWord = firstWord.substring(0, 8);
            code = truncatedWord + "_" + generateRandomString();
        }

        return code;
    }

    @Transactional(readOnly = true)
    public List<TournamentResponse> getAllTournaments() {
        return tournamentRepository.findAll()
                .stream()
                .map(tournamentMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TournamentResponse getTournamentById(Long id, Long userId) {
        Tournament tournament = tournamentFinder.getTournamentOrThrow(id);

        if (userId.equals(tournament.getHost().getId())) return tournamentMapper.toResponse(tournament);

        boolean isPlayer = tournament.getCategories().stream()
                .flatMap(category -> category.getPlayers().stream())
                        .anyMatch(player -> player.getUser().getId().equals(userId));

        if (isPlayer) return tournamentMapper.toResponse(tournament);

        throw new ForbiddenException("You are not a host or player of this tournament");
    }

    @Transactional(readOnly = true)
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
            if (category.getName().equals("Mixed Doubles")) {
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

    @Transactional
    public TournamentResponseLite createTournament(TournamentRequest request, Long userId) {
        User user = userFinder.getUserOrThrow(userId);

        Tournament newTournament = tournamentMapper.toEntity(request);
        newTournament.setHost(user);
        newTournament.setCode(generateCode(newTournament.getName()));

        Tournament savedTournament = tournamentRepository.save(newTournament);

        if (request.men_singles()) categoryService.createCategory(
                new CategoryRequest("Men's Singles", savedTournament.getId()), userId
        );

        if (request.men_doubles()) categoryService.createCategory(
                new CategoryRequest("Men's Doubles", savedTournament.getId()), userId
        );

        if (request.women_singles()) categoryService.createCategory(
                new CategoryRequest("Women's Singles", savedTournament.getId()), userId
        );

        if (request.women_doubles()) categoryService.createCategory(
                new CategoryRequest("Women's Doubles", savedTournament.getId()), userId
        );

        if (request.mix_doubles()) categoryService.createCategory(
                new CategoryRequest("Mixed Doubles", savedTournament.getId()), userId
        );

        return tournamentMapper.toResponseLite(savedTournament);
    }

    @Transactional
    public TournamentResponseLite updateName(Long id, TournamentNameUpdateRequest request, Long userId) {
        Tournament existingTournament = tournamentFinder.getTournamentOrThrow(id);
        tournamentFinder.assertHost(existingTournament, userId);

        tournamentMapper.updateNameEntity(request, existingTournament);

        Tournament savedTournament = tournamentRepository.save(existingTournament);
        return tournamentMapper.toResponseLite(savedTournament);
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
                    List<Match> unfinishedMatches = category.getMatches().stream().filter(m -> !m.getState().equals("FINISHED")).toList();
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
                    List<Match> finishedMatches = category.getMatches().stream().filter(m -> m.getState().equals("FINISHED")).toList();
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

        tournament.setStage(next);
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

    @Transactional
    public TournamentResponseLite updateShowMobile(Long id, TournamentShowMobileUpdateRequest request, Long userId) {
        Tournament existingTournament = tournamentFinder.getTournamentOrThrow(id);
        tournamentFinder.assertHost(existingTournament, userId);

        tournamentMapper.updateShowMobileEntity(request, existingTournament);

        Tournament savedTournament = tournamentRepository.save(existingTournament);
        return tournamentMapper.toResponseLite(savedTournament);
    }

    public TournamentResponseLite checkCode(TournamentCheckCodeRequest request) {
        Tournament tournament = tournamentFinder.getTournamentByCodeOrThrow(request.code());

        return tournamentMapper.toResponseLite(tournament);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Tournament tournament = tournamentFinder.getTournamentOrThrow(id);
        tournamentFinder.assertHost(tournament, userId);

        tournamentRepository.delete(tournament);
    }
}
