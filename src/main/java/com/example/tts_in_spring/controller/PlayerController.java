package com.example.tts_in_spring.controller;

import com.example.tts_in_spring.dto.player.PlayerResponse;
import com.example.tts_in_spring.mapper.PlayerMapper;
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

    @Autowired
    private PlayerMapper playerMapper;

    @GetMapping
    // TODO: Uncomment for prod
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PlayerResponse>> getAllPlayers() {
        List<PlayerResponse> players = playerRepository.findAll()
                .stream()
                .map(p -> playerMapper.toResponse(p))
                .toList();

        return ResponseEntity.ok(players);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerResponse> getPlayer(@PathVariable Long id) {
        return playerRepository.findById(id)
                .map(p -> ResponseEntity.ok(playerMapper.toResponse(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPlayer(@RequestBody Player incomingPlayer) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        incomingPlayer.setUser(user);

        Player savedPlayer = playerRepository.save(incomingPlayer);
        return playerRepository.findById(savedPlayer.getId())
                .map(p -> ResponseEntity.status(HttpStatus.CREATED).body(playerMapper.toResponseLite(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    // @PatchMapping("/players/{id}")
    // public ResponseEntity<PlayerResponse> updatePlayer(@PathVariable Long id, @RequestBody PlayerUpdateDto updatedPlayer) {
    //
    // }
}