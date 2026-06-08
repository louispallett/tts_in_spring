package com.example.tts_in_spring.controller;

import com.example.tts_in_spring.dto.CategoryResponse;
import com.example.tts_in_spring.dto.ParticipantResponse;
import com.example.tts_in_spring.dto.PlayerResponse;
import com.example.tts_in_spring.dto.TeamResponse;
import com.example.tts_in_spring.model.Team;
import com.example.tts_in_spring.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/team")
public class TeamController {
    @Autowired
    TeamRepository teamRepository;

    private TeamResponse mapToResponse(Team team) {
        TeamResponse teamResponse = new TeamResponse(team);
        teamResponse.category = new CategoryResponse(team.getCategory());

        teamResponse.players = Optional.ofNullable(team.getPlayers())
                .orElse(List.of())
                .stream()
                .map(PlayerResponse::new)
                .toList();

        teamResponse.participants = Optional.ofNullable(team.getParticipants())
                .orElse(List.of())
                .stream()
                .map(ParticipantResponse::new)
                .toList();

        return teamResponse;
    }

    @GetMapping
    public ResponseEntity<List<TeamResponse>> getAllTeams() {
        List<TeamResponse> teams = teamRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return ResponseEntity.ok(teams);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> getTeam(@PathVariable Long id) {
        return teamRepository.findById(id)
                .map(team -> ResponseEntity.ok(mapToResponse(team)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTeam(@RequestBody Team incomingTeam) {
        Team savedTeam = teamRepository.save(incomingTeam);

        return teamRepository.findById(savedTeam.getId())
                .map(team -> ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(team)))
                .orElse(ResponseEntity.notFound().build());
    }
}
