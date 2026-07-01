package com.example.tts_in_spring.team;

import com.example.tts_in_spring.category.Category;
import com.example.tts_in_spring.category.CategoryFinder;
import com.example.tts_in_spring.exception.ForbiddenException;
import com.example.tts_in_spring.team.dto.TeamRequest;
import com.example.tts_in_spring.team.dto.TeamResponse;
import com.example.tts_in_spring.team.dto.TeamResponseLite;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;
    private final CategoryFinder categoryFinder;
    private final TeamFinder teamFinder;

    @Transactional(readOnly = true)
    public List<TeamResponse> getAllTeams() {
        return teamRepository.findAll()
                .stream()
                .map(teamMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TeamResponse getTeamById(Long id, Long userId) {
        Team team = teamFinder.getTeamOrThrow(id);

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

        throw new ForbiddenException();
    }

    @Transactional
    public TeamResponseLite createTeam(TeamRequest teamRequest, Long userId) {
        Category category = categoryFinder.getCategoryOrThrow(teamRequest.categoryId());

        if (category.getTournament().getHost().getId().equals(userId)) {
            Team team = teamMapper.toEntity(teamRequest);
            team.setCategory(category);

            Team savedTeam = teamRepository.save(team);
            return teamMapper.toResponseLite(savedTeam);
        }

        throw new ForbiddenException();
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Team team = teamFinder.getTeamOrThrow(id);
        teamFinder.assertHost(team, userId);

        teamRepository.delete(team);
    }
}
