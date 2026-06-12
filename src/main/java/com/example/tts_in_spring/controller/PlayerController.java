package com.example.tts_in_spring.controller;

import com.example.tts_in_spring.dto.player.PlayerRequest;
import com.example.tts_in_spring.dto.player.PlayerResponse;
import com.example.tts_in_spring.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/player")
public class PlayerController {
    @Autowired
    private PlayerService playerService;

    @GetMapping
    // TODO: Uncomment for prod
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PlayerResponse>> getAllPlayers() {
        return ResponseEntity.ok(playerService.getAllPlayers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerResponse> getPlayer(@PathVariable Long id) {
        return ResponseEntity.ok(playerService.getPlayerById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPlayer(@RequestBody PlayerRequest playerRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(playerService.createPlayer(playerRequest));
    }

    // @PatchMapping("/players/{id}")
    // public ResponseEntity<PlayerResponse> updatePlayer(@PathVariable Long id, @RequestBody PlayerUpdateDto updatedPlayer) {
    //
    // }
}