package com.example.tts_in_spring.team;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TeamService {
    @Autowired
    TeamRepository teamRepository;

    @Autowired
    TeamMapper teamMapper;

    private Team getTeamOrThrow(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found"));
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> getAllTeams() {
        return teamRepository.findAll()
                .stream()
                .map(teamMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TeamResponse getTeamById(Long id, Long userId) {
        Team team = getTeamOrThrow(id);

        boolean isHost = team.getCategory()
                .getTournament()
                .getHost()
                .getId()
                .equals(userId);

        boolean isTeamMember = team.getPlayers().stream()
                .anyMatch(p -> p.getUser().getId().equals(userId));

        if (isHost || isTeamMember) {
            return teamMapper.toResponse(team);
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    @Transactional
    public TeamResponseLite createTeam(TeamRequest teamRequest, Long userId) {
        if (teamRequest.getCategory().getTournament().getHost().getId().equals(userId)) {
            Team team = teamMapper.toEntity(teamRequest);

            Team savedTeam = teamRepository.save(team);
            return teamMapper.toResponseLite(savedTeam);
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }
}
