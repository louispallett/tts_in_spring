package com.example.tts_in_spring.service;

import com.example.tts_in_spring.dto.team.TeamRequest;
import com.example.tts_in_spring.dto.team.TeamResponse;
import com.example.tts_in_spring.dto.team.TeamResponseLite;
import com.example.tts_in_spring.mapper.TeamMapper;
import com.example.tts_in_spring.model.Team;
import com.example.tts_in_spring.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TeamService {
    @Autowired
    TeamRepository teamRepository;

    @Autowired
    TeamMapper teamMapper;

    public List<TeamResponse> getAllTeams() {
        return teamRepository.findAll()
                .stream()
                .map(teamMapper::toResponse)
                .toList();
    }

    public TeamResponse getTeamById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found"));
        return teamMapper.toResponse(team);
    }

    public TeamResponseLite createTeam(TeamRequest teamRequest) {
        Team validatedTeam = new Team();

        validatedTeam.setCategory(teamRequest.getCategory());

        Team savedTeam = teamRepository.save(validatedTeam);
        return teamMapper.toResponseLite(savedTeam);
    }
}
