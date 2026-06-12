package com.example.tts_in_spring.service;

import com.example.tts_in_spring.dto.player.PlayerRequest;
import com.example.tts_in_spring.dto.player.PlayerResponse;
import com.example.tts_in_spring.dto.player.PlayerResponseLite;
import com.example.tts_in_spring.mapper.PlayerMapper;
import com.example.tts_in_spring.model.Player;
import com.example.tts_in_spring.model.User;
import com.example.tts_in_spring.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerMapper playerMapper;

    public List<PlayerResponse> getAllPlayers() {
        return playerRepository.findAll()
                .stream()
                .map(playerMapper::toResponse)
                .toList();
    }

    public PlayerResponse getPlayerById(Long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found"));
        return playerMapper.toResponse(player);
    }

    public PlayerResponseLite createPlayer(PlayerRequest playerRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        Player validatedPlayer = new Player();

        validatedPlayer.setMale(playerRequest.isMale());
        validatedPlayer.setSeeded(false);
        validatedPlayer.setRank(0);
        validatedPlayer.setUser(user);
        validatedPlayer.setCategory(playerRequest.getCategory());

        Player savedPlayer = playerRepository.save(validatedPlayer);
        return playerMapper.toResponseLite(savedPlayer);
    }
}
