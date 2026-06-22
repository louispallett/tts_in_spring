package com.example.tts_in_spring.tournament;

import com.example.tts_in_spring.category.dto.CategoryRequest;
import com.example.tts_in_spring.category.CategoryService;
import com.example.tts_in_spring.tournament.dto.*;
import com.example.tts_in_spring.user.User;
import com.example.tts_in_spring.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TournamentService {
    private final TournamentRepository tournamentRepository;
    private final UserService userService;
    private final TournamentMapper tournamentMapper;
    private final CategoryService categoryService;

    public Tournament getTournamentOrThrow(Long id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tournament not found"));
    }

    private void assertHost(Tournament tournament, Long userId) {
        if (!tournament.getHost().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

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
        Tournament tournament = getTournamentOrThrow(id);

        if (userId.equals(tournament.getHost().getId())) return tournamentMapper.toResponse(tournament);

        boolean isPlayer = tournament.getCategories().stream()
                .flatMap(category -> category.getPlayers().stream())
                        .anyMatch(player -> player.getUser().getId().equals(userId));

        if (isPlayer) return tournamentMapper.toResponse(tournament);

        throw(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @Transactional
    public TournamentResponseLite createTournament(TournamentRequest request, Long userId) {
        User user = userService.getUserOrThrow(userId);

        Tournament newTournament = tournamentMapper.toEntity(request);
        newTournament.setStage("SIGN_UP");
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
        Tournament existingTournament = getTournamentOrThrow(id);
        assertHost(existingTournament, userId);

        tournamentMapper.updateNameEntity(request, existingTournament);

        Tournament savedTournament = tournamentRepository.save(existingTournament);
        return tournamentMapper.toResponseLite(savedTournament);
    }

     public TournamentResponseLite updateStage(Long id, TournamentStageUpdateRequest request, Long userId) {
         Tournament existingTournament = getTournamentOrThrow(id);
         assertHost(existingTournament, userId);

         tournamentMapper.updateStageEntity(request, existingTournament);

         Tournament savedTournament = tournamentRepository.save(existingTournament);
         return tournamentMapper.toResponseLite(savedTournament);
     }

    public TournamentResponseLite updateShowMobile(Long id, TournamentShowMobileUpdateRequest request, Long userId) {
        Tournament existingTournament = getTournamentOrThrow(id);
        assertHost(existingTournament, userId);

        tournamentMapper.updateShowMobileEntity(request, existingTournament);

        Tournament savedTournament = tournamentRepository.save(existingTournament);
        return tournamentMapper.toResponseLite(savedTournament);
    }

    public Tournament checkCode(String code) {
        return tournamentRepository.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid code"));
    }
}
