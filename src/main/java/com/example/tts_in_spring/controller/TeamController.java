package com.example.tts_in_spring.controller;

import com.example.tts_in_spring.dto.team.TeamResponse;
import com.example.tts_in_spring.mapper.TeamMapper;
import com.example.tts_in_spring.model.Team;
import com.example.tts_in_spring.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/team")
public class TeamController {
    @Autowired
    TeamRepository teamRepository;

    @Autowired
    TeamMapper teamMapper;

    @GetMapping
    // TODO: Uncomment for prod
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TeamResponse>> getAllTeams() {
        List<TeamResponse> teams = teamRepository.findAll()
                .stream()
                .map(t -> teamMapper.toResponse(t))
                .toList();

        return ResponseEntity.ok(teams);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> getTeam(@PathVariable Long id) {
        return teamRepository.findById(id)
                .map(team -> ResponseEntity.ok(teamMapper.toResponse(team)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTeam(@RequestBody Team incomingTeam) {
        Team savedTeam = teamRepository.save(incomingTeam);

        return teamRepository.findById(savedTeam.getId())
                .map(t -> ResponseEntity.status(HttpStatus.CREATED).body(teamMapper.toResponseLite(t)))
                .orElse(ResponseEntity.notFound().build());
    }
}
