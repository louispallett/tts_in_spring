package com.example.tts_in_spring.controller;

import com.example.tts_in_spring.dto.match.MatchResponse;
import com.example.tts_in_spring.dto.participant.ParticipantResponse;
import com.example.tts_in_spring.mapper.MatchMapper;
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

    @Autowired
    MatchMapper matchMapper;

    @GetMapping
    // TODO: Uncomment for prod
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MatchResponse>> getAllMatches() {
        List<MatchResponse> matches = matchRepository.findAll()
                .stream()
                .map(m -> matchMapper.toResponse(m))
                .toList();

        return ResponseEntity.ok(matches);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchResponse> getMatch(@PathVariable Long id) {
        return matchRepository.findById(id)
                .map(m-> ResponseEntity.ok(matchMapper.toResponse(m)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<?> createMatch(@RequestBody Match incomingMatch) {
        Match savedMatch = matchRepository.save(incomingMatch);

        return matchRepository.findById(savedMatch.getId())
                .map(m -> ResponseEntity.status(HttpStatus.CREATED).body(matchMapper.toResponseLite(m)))
                .orElse(ResponseEntity.notFound().build());
    }
}
