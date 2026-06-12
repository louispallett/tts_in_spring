package com.example.tts_in_spring.controller;

import com.example.tts_in_spring.dto.match.MatchRequest;
import com.example.tts_in_spring.dto.match.MatchResponse;
import com.example.tts_in_spring.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/match")
public class MatchController {
    @Autowired
    private MatchService matchService;

    @GetMapping
    // TODO: Uncomment for prod
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MatchResponse>> getAllMatches() {
        return ResponseEntity.ok(matchService.getAllMatches());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchResponse> getMatch(@PathVariable Long id) {
        return ResponseEntity.ok(matchService.getMatchById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createMatch(@RequestBody MatchRequest matchRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(matchService.createMatch(matchRequest));
    }
}
