package com.example.tts_in_spring.controller;

import com.example.tts_in_spring.model.Tournament;
import com.example.tts_in_spring.model.User;
import com.example.tts_in_spring.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournament")
public class TournamentController {
    @Autowired
    private TournamentRepository tournamentRepository;

    @GetMapping("/get-all")
    public ResponseEntity<List<Tournament>> getAllTournaments() {
        List<Tournament> tournaments = tournamentRepository.findAll();
        return ResponseEntity.ok(tournaments);
    }

    @GetMapping("/get")
    public ResponseEntity<Tournament> getTournament(@RequestParam Long id) {
        return tournamentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTournament(@RequestBody Tournament incomingTournament) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        incomingTournament.setCode(incomingTournament.getName());
        incomingTournament.setHost(user);
        Tournament savedTournament = tournamentRepository.save(incomingTournament);
        return ResponseEntity.ok(savedTournament);
    }
}