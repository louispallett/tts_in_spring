package com.example.tts_in_spring.team;

import com.example.tts_in_spring.category.CategoryFinder;
import com.example.tts_in_spring.category.dto.CategoryResponseLite;
import com.example.tts_in_spring.category.CategoryTestBuilder;
import com.example.tts_in_spring.player.Player;
import com.example.tts_in_spring.player.PlayerTestBuilder;
import com.example.tts_in_spring.team.dto.TeamRequest;
import com.example.tts_in_spring.team.dto.TeamResponse;
import com.example.tts_in_spring.team.dto.TeamResponseLite;
import com.example.tts_in_spring.user.User;
import com.example.tts_in_spring.user.UserTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class TeamServiceTest {
    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamMapper teamMapper;

    @Mock
    private TeamFinder teamFinder;

    @Mock
    private CategoryFinder categoryFinder;

    @InjectMocks
    private TeamService teamService;

    private TeamResponse buildTeamResponse() {
        return new TeamResponse(
                10000L,
                new CategoryResponseLite(100L, "Mens Singles", false, false),
                null,
                null
        );
    }

    private TeamRequest buildTeamRequest() {
        return new TeamRequest(CategoryTestBuilder.aCategory().build().getId());
    }

    @Test
    void getAllTeams_returnsMappedList() {
        Team team = TeamTestBuilder.aTeam().build();
        TeamResponse response = buildTeamResponse();

        when(teamRepository.findAll()).thenReturn(List.of(team));
        when(teamMapper.toResponse(team)).thenReturn(response);

        List<TeamResponse> result = teamService.getAllTeams();

        assertThat(result).containsExactly(response);
    }

    @Test
    void getAllTeams_whenEmpty_returnsEmptyList() {
        when(teamRepository.findAll()).thenReturn(List.of());

        assertThat(teamService.getAllTeams()).isEmpty();
    }

    @Test
    void getTeamById_withHost_returnsMappedResponse() {
        Team team = TeamTestBuilder.aTeam().build();
        TeamResponse response = buildTeamResponse();

        when(teamFinder.getTeamOrThrow(team.getId())).thenReturn(team);
        when(teamMapper.toResponse(team)).thenReturn(response);

        assertThat(teamService.getTeamById(team.getId(), team.getCategory().getTournament().getHost().getId())).isEqualTo(response);
    }

    @Test
    void getTeamById_whenPlayer_returnsMappedResponse() {
        Team team = TeamTestBuilder.aTeam().build();
        User user = UserTestBuilder.aUser().withId(2L).build();
        Player player = PlayerTestBuilder.aPlayer().withUser(user).build();
        team.setPlayers(List.of(player, PlayerTestBuilder.aPlayer().build()));

        TeamResponse response = buildTeamResponse();

        when(teamFinder.getTeamOrThrow(team.getId())).thenReturn(team);
        when(teamMapper.toResponse(team)).thenReturn(response);

        assertThat(teamService.getTeamById(team.getId(), 2L)).isEqualTo(response);
    }

    @Test
    void getTeamById_whenNotAuthorized_returns403() {
        Team team = TeamTestBuilder.aTeam().build();

        when(teamFinder.getTeamOrThrow(team.getId())).thenReturn(team);

        assertThatThrownBy(() -> teamService.getTeamById(team.getId(), 3L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.FORBIDDEN)
                );
    }

    @Test
    void createTeam_whenHost_savesAndReturnsMappedLite() {
        TeamRequest request = buildTeamRequest();

        Team saved = TeamTestBuilder.aTeam().build();
        TeamResponseLite lite = new TeamResponseLite(10000L);

        when(categoryFinder.getCategoryOrThrow(request.categoryId())).thenReturn(saved.getCategory());
        when(teamMapper.toEntity(request)).thenReturn(saved);
        when(teamRepository.save(any(Team.class))).thenReturn(saved);
        when(teamMapper.toResponseLite(saved)).thenReturn(lite);

        assertThat(teamService.createTeam(request, saved.getCategory().getTournament().getHost().getId())).isEqualTo(lite);
    }

    @Test
    void createTeam_whenNotHost_throws403() {
        TeamRequest request = buildTeamRequest();

        Team team = TeamTestBuilder.aTeam().build();

        when(categoryFinder.getCategoryOrThrow(request.categoryId())).thenReturn(team.getCategory());

        assertThatThrownBy(() -> teamService.createTeam(request, 3L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.FORBIDDEN)
                );

        verify(teamRepository, never()).save(any());
        verifyNoInteractions(teamMapper);
    }
}
