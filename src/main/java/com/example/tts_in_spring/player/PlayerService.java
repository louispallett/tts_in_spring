package com.example.tts_in_spring.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerMapper playerMapper;

    private Player getPlayerOrThrow(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found"));
    }

    private void assertHost(Player player, Long userId) {
        if (!player.getCategory().getTournament().getHost().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @Transactional(readOnly = true)
    public List<PlayerResponse> getAllPlayers() {
        return playerRepository.findAll()
                .stream()
                .map(playerMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PlayerResponse getPlayerById(Long id, Long userId) {
        Player player = getPlayerOrThrow(id);

        if (
                player.getUser().getId().equals(userId) ||
                        player.getCategory().getTournament().getHost().getId().equals(userId)
        ) {
            return playerMapper.toResponse(player);
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    @Transactional
    public PlayerResponseLite createPlayer(PlayerRequest playerRequest, Long userId) {
        if (playerRequest.getCategory().getTournament().getHost().getId().equals(userId)) {
            Player player = playerMapper.toEntity(playerRequest);
            player.setRank(0);
            player.setSeeded(false);

            Player savedPlayer = playerRepository.save(player);
            return playerMapper.toResponseLite(savedPlayer);
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    @Transactional
    public PlayerResponseLite updateRank(Long id, PlayerUpdateRankRequest request, Long userId) {
        Player player = getPlayerOrThrow(id);
        assertHost(player, userId);

        playerMapper.updateRankEntity(request, player);

        Player savedPlayer = playerRepository.save(player);
        return playerMapper.toResponseLite(savedPlayer);
    }

    @Transactional
    public PlayerResponseLite updateSeeded(Long id, PlayerUpdateSeededRequest request, Long userId) {
        Player player = getPlayerOrThrow(id);
        assertHost(player, userId);

        playerMapper.updateSeededEntity(request, player);

        Player savedPlayer = playerRepository.save(player);
        return playerMapper.toResponseLite(savedPlayer);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Player player = getPlayerOrThrow(id);
        assertHost(player, userId);

        playerRepository.delete(player);
    }
}
