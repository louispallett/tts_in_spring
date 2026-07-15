package com.example.tts_in_spring.player;

import com.example.tts_in_spring.category.Category;
import com.example.tts_in_spring.category.CategoryFinder;
import com.example.tts_in_spring.exception.ConflictException;
import com.example.tts_in_spring.exception.ForbiddenException;
import com.example.tts_in_spring.player.dto.*;
import com.example.tts_in_spring.user.User;
import com.example.tts_in_spring.user.UserFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;
    private final CategoryFinder categoryFinder;
    private final UserFinder userFinder;
    private final PlayerFinder playerFinder;


    @Transactional(readOnly = true)
    public List<PlayerResponse> getAllPlayers() {
        return playerRepository.findAll()
                .stream()
                .map(playerMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PlayerResponse getPlayerById(Long id, Long userId) {
        Player player = playerFinder.getPlayerOrThrow(id);

        if (
                player.getUser().getId().equals(userId) ||
                        player.getCategory().getTournament().getHost().getId().equals(userId)
        ) {
            return playerMapper.toResponse(player);
        }

        throw new ForbiddenException();
    }

    @Transactional
    public PlayerResponseLite createPlayer(PlayerRequest playerRequest, Long userId) {
        User user = userFinder.getUserOrThrow(userId);
        Category category = categoryFinder.getCategoryOrThrow(playerRequest.categoryId());

        if (!category.getPlayers().stream().filter(p -> p.getUser().getId().equals(userId)).toList().isEmpty())
            throw new ConflictException(
                    "User " + user.getFirstName()
                            + " " + user.getLastName() + " is already part of category "
                            + category.getName() + " (" + category.getId() + ")"
            );

        Player player = playerMapper.toEntity(playerRequest);
        player.setUser(user);
        player.setCategory(category);
        player.setRank(0);
        player.setSeeded(false);

        Player savedPlayer = playerRepository.save(player);
        return playerMapper.toResponseLite(savedPlayer);
    }

    @Transactional
    public List<PlayerResponseLite> joinTournament(JoinTournamentRequest request, Long userId) {
        List<PlayerResponseLite> players = new ArrayList<>();
        for (Long categoryId : request.categories()) {
            PlayerRequest playerRequest = new PlayerRequest(
                    request.male(),
                    request.mobCode(),
                    request.mobile(),
                    categoryId
            );
            players.add(createPlayer(playerRequest, userId));
        }

        return players;
    }

    @Transactional
    public List<PlayerResponse> updateMobile(
            Long tournamentId,
            PlayerUpdateMobileRequest request,
            Long userId
    ) {
        User user = userFinder.getUserOrThrow(userId);

        List<Player> players = user.getPlayers().stream().filter(
                p -> p.getCategory().getTournament().getId().equals(tournamentId)
        ).toList();

        for (Player player : players) {
            playerMapper.updateMobileEntity(request, player);
        }

        return players.stream().map(playerMapper::toResponse).toList();
    }

    @Transactional
    public PlayerResponseLite updateRank(Long id, PlayerUpdateRankRequest request, Long userId) {
        Player player = playerFinder.getPlayerOrThrow(id);
        playerFinder.assertHost(player, userId);

        playerMapper.updateRankEntity(request, player);

        Player savedPlayer = playerRepository.save(player);
        return playerMapper.toResponseLite(savedPlayer);
    }

    @Transactional
    public PlayerResponseLite updateSeeded(Long id, PlayerUpdateSeededRequest request, Long userId) {
        Player player = playerFinder.getPlayerOrThrow(id);
        playerFinder.assertHost(player, userId);

        playerMapper.updateSeededEntity(request, player);

        Player savedPlayer = playerRepository.save(player);
        return playerMapper.toResponseLite(savedPlayer);
    }

    @Transactional
    public void addTeam(Long id, AddTeamRequest request) {
        Player player = playerFinder.getPlayerOrThrow(id);

        playerMapper.updateTeamEntity(request, player);
    }

    @Transactional
    public void wipeMobiles(List<Category> categories) {
        PlayerUpdateMobileRequest request = new PlayerUpdateMobileRequest("", "");
        for (Category category : categories) {
            for (Player player : category.getPlayers()) {
                playerMapper.updateMobileEntity(request, player);
            }
        }
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Player player = playerFinder.getPlayerOrThrow(id);
        playerFinder.assertHost(player, userId);

        playerRepository.delete(player);
    }
}
