package com.example.tts_in_spring.controller;

import com.example.tts_in_spring.dto.tournament.TournamentRequest;
import com.example.tts_in_spring.dto.tournament.TournamentResponse;
import com.example.tts_in_spring.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournament")
public class TournamentController {
    @Autowired
    private TournamentService tournamentService;

    @GetMapping
    // TODO: Uncomment for prod
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TournamentResponse>> getAllTournaments() {
        return ResponseEntity.ok(tournamentService.getAllTournaments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TournamentResponse> getTournament(@PathVariable Long id) {
        return ResponseEntity.ok(tournamentService.getTournamentById(id));
    }

    // FIXME: Should return code
    @PostMapping("/create")
    public ResponseEntity<?> createTournament(@RequestBody TournamentRequest tournamentRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tournamentService.createTournament(tournamentRequest));
    }
}