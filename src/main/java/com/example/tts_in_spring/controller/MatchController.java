package com.example.tts_in_spring.controller;

import com.example.tts_in_spring.dto.MatchResponse;
import com.example.tts_in_spring.dto.ParticipantResponse;
import com.example.tts_in_spring.model.Match;
import com.example.tts_in_spring.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/match")
public class MatchController {
    @Autowired
    MatchRepository matchRepository;

    private MatchResponse mapToResponse(Match match) {
        MatchResponse matchResponse = new MatchResponse(match);

        matchResponse.category = match.getCategory();
        if (match.getNextMatch() != null) {
            matchResponse.nextMatch = match.getNextMatch();
        }

        matchResponse.previousMatches = Optional.ofNullable(match.getPreviousMatches())
                .orElse(List.of())
                .stream()
                .map(MatchResponse::new)
                .toList();

        matchResponse.participants = Optional.ofNullable(match.getParticipants())
                .orElse(List.of())
                .stream()
                .map(ParticipantResponse::new)
                .toList();

        return matchResponse;
    }

    @GetMapping
    public ResponseEntity<List<MatchResponse>> getAllMatches() {
        List<MatchResponse> matches = matchRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return ResponseEntity.ok(matches);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchResponse> getMatch(@PathVariable Long id) {
        return matchRepository.findById(id)
                .map(match -> ResponseEntity.ok(mapToResponse(match)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<?> createMatch(@RequestBody Match incomingMatch) {
        Match savedMatch = matchRepository.save(incomingMatch);

        return matchRepository.findById(savedMatch.getId())
                .map(m -> ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(m)))
                .orElse(ResponseEntity.notFound().build());
    }
}
