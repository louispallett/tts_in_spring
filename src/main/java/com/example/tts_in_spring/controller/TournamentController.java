package com.example.tts_in_spring.controller;

import com.example.tts_in_spring.dto.tournament.TournamentRequest;
import com.example.tts_in_spring.dto.tournament.TournamentResponse;
import com.example.tts_in_spring.mapper.TournamentMapper;
import com.example.tts_in_spring.model.Tournament;
import com.example.tts_in_spring.model.User;
import com.example.tts_in_spring.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @Autowired
    private TournamentMapper tournamentMapper;

    @GetMapping
    // TODO: Uncomment for prod
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TournamentResponse>> getAllTournaments() {
        List<TournamentResponse> tournaments = tournamentRepository.findAll()
                .stream()
                .map(t -> tournamentMapper.toResponse(t))
                .toList();

        return ResponseEntity.ok(tournaments);
    }

    // FIXME: Authenticate only tournament players
    // FIXME: Show code to only host
    // (Note: Do this through dedicated service)
    @GetMapping("/{id}")
    public ResponseEntity<TournamentResponse> getTournament(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        Tournament tournament = tournamentRepository.findById(id).orElseThrow();

        return ResponseEntity.ok(tournamentMapper.toResponse(tournament));
    }

    // FIXME: Should return code
    @PostMapping("/create")
    public ResponseEntity<?> createTournament(@RequestBody TournamentRequest tournamentRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        tournamentRequest.setCode(tournamentRequest.getName());

        Tournament validatedTournament = new Tournament();
        validatedTournament.setName(tournamentRequest.getName());
        validatedTournament.setStage("SIGN_UP");
        validatedTournament.setHost(user);
        validatedTournament.setCode(tournamentRequest.getCode());
        validatedTournament.setShowMobile(tournamentRequest.isShowMobile());

        Tournament savedTournament = tournamentRepository.save(validatedTournament);
        return tournamentRepository.findById(savedTournament.getId())
                .map(t -> ResponseEntity.status(HttpStatus.CREATED).body(tournamentMapper.toResponseHost(t)))
                .orElse(ResponseEntity.notFound().build());
    }
}