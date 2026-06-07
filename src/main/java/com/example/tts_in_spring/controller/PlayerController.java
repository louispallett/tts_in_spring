package com.example.tts_in_spring.controller;

import com.example.tts_in_spring.dto.CategoryResponse;
import com.example.tts_in_spring.dto.PlayerResponse;
import com.example.tts_in_spring.dto.UserResponse;
import com.example.tts_in_spring.model.Player;
import com.example.tts_in_spring.model.User;
import com.example.tts_in_spring.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/player")
public class PlayerController {
    @Autowired
    private PlayerRepository playerRepository;

    private PlayerResponse mapToResponse(Player player) {
        PlayerResponse playerResponse = new PlayerResponse(player);
        playerResponse.user = new UserResponse(player.getUser());
        playerResponse.category = new CategoryResponse(player.getCategory());

        return playerResponse;
    }

    @GetMapping
    public ResponseEntity<List<PlayerResponse>> getAllPlayers() {
        List<PlayerResponse> players = playerRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return ResponseEntity.ok(players);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerResponse> getPlayer(@PathVariable Long id) {
        return playerRepository.findById(id)
                .map(p -> ResponseEntity.ok(mapToResponse(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPlayer(@RequestBody Player incomingPlayer) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        incomingPlayer.setUser(user);

        Player savedPlayer = playerRepository.save(incomingPlayer);
        return playerRepository.findById(savedPlayer.getId())
                .map(p -> ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    // @PatchMapping("/players/{id}")
    // public ResponseEntity<PlayerResponse> updatePlayer(@PathVariable Long id, @RequestBody PlayerUpdateDto updatedPlayer) {
    //
    // }
}