package com.example.tts_in_spring.team;

import com.example.tts_in_spring.category.Category;
import com.example.tts_in_spring.category.CategoryResponseLite;
import com.example.tts_in_spring.category.CategoryTestBuilder;
import com.example.tts_in_spring.player.Player;
import com.example.tts_in_spring.player.PlayerTestBuilder;
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
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class TeamServiceTest {
    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamMapper teamMapper;

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
        TeamRequest r = new TeamRequest();
        r.setCategory(CategoryTestBuilder.aCategory().build());

        return r;
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

        when(teamRepository.findById(team.getId())).thenReturn(Optional.of(team));
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

        when(teamRepository.findById(team.getId())).thenReturn(Optional.of(team));
        when(teamMapper.toResponse(team)).thenReturn(response);

        assertThat(teamService.getTeamById(team.getId(), 2L)).isEqualTo(response);
    }

    @Test
    void getTeamById_whenNotAuthorized_returns403() {
        Team team = TeamTestBuilder.aTeam().build();

        when(teamRepository.findById(team.getId())).thenReturn(Optional.of(team));

        assertThatThrownBy(() -> teamService.getTeamById(team.getId(), 3L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.FORBIDDEN)
                );
    }

    @Test
    void getTeamById_whenNotFound_returns404() {
        when(teamRepository.findById(99999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> teamService.getTeamById(99999L, 1L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.NOT_FOUND)
                );
    }

    @Test
    void createTeam_whenHost_savesAndReturnsMappedLite() {
        TeamRequest request = buildTeamRequest();

        Team saved = TeamTestBuilder.aTeam().build();
        TeamResponseLite lite = new TeamResponseLite(10000L);

        when(teamMapper.toEntity(request)).thenReturn(saved);
        when(teamRepository.save(any(Team.class))).thenReturn(saved);
        when(teamMapper.toResponseLite(saved)).thenReturn(lite);

        assertThat(teamService.createTeam(request, saved.getCategory().getTournament().getHost().getId())).isEqualTo(lite);
    }

    @Test
    void createTeam_whenNotHost_throws403() {
        TeamRequest request = buildTeamRequest();

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
