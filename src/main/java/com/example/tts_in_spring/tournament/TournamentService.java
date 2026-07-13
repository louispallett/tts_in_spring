package com.example.tts_in_spring.tournament;

import com.example.tts_in_spring.category.Type;
import com.example.tts_in_spring.category.dto.CategoryRequest;
import com.example.tts_in_spring.category.CategoryService;
import com.example.tts_in_spring.exception.ForbiddenException;
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


    @Transactional
    public TournamentResponseLite createTournament(TournamentRequest request, Long userId) {
        User user = userFinder.getUserOrThrow(userId);

        Tournament newTournament = tournamentMapper.toEntity(request);
        newTournament.setHost(user);
        newTournament.setCode(generateCode(newTournament.getName()));

        Tournament savedTournament = tournamentRepository.save(newTournament);

        if (request.men_singles()) categoryService.createCategory(
                new CategoryRequest(Type.MEN_SINGLES, savedTournament.getId()), userId
        );

        if (request.men_doubles()) categoryService.createCategory(
                new CategoryRequest(Type.MEN_DOUBLES, savedTournament.getId()), userId
        );

        if (request.women_singles()) categoryService.createCategory(
                new CategoryRequest(Type.WOMEN_SINGLES, savedTournament.getId()), userId
        );

        if (request.women_doubles()) categoryService.createCategory(
                new CategoryRequest(Type.WOMEN_DOUBLES, savedTournament.getId()), userId
        );

        if (request.mix_doubles()) categoryService.createCategory(
                new CategoryRequest(Type.MIXED_DOUBLES, savedTournament.getId()), userId
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
