package com.example.tts_in_spring.team;

import com.example.tts_in_spring.category.Category;
import com.example.tts_in_spring.category.CategoryFinder;
import com.example.tts_in_spring.category.Type;
import com.example.tts_in_spring.category.dto.CategoryResponseLite;
import com.example.tts_in_spring.category.CategoryTestBuilder;
import com.example.tts_in_spring.exception.ForbiddenException;
import com.example.tts_in_spring.player.Player;
import com.example.tts_in_spring.player.PlayerMapper;
import com.example.tts_in_spring.player.PlayerTestBuilder;
import com.example.tts_in_spring.player.dto.PlayerResponse;
import com.example.tts_in_spring.team.dto.TeamRequest;
import com.example.tts_in_spring.team.dto.TeamResponse;
import com.example.tts_in_spring.team.dto.TeamResponseLite;
import com.example.tts_in_spring.user.User;
import com.example.tts_in_spring.user.UserTestBuilder;
import com.example.tts_in_spring.user.dto.UserResponseLite;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private PlayerMapper playerMapper;

    @Mock
    private CategoryFinder categoryFinder;

    @InjectMocks
    private TeamService teamService;

    private TeamResponse buildTeamResponse() {
        return new TeamResponse(
                10000L,
                new CategoryResponseLite(100L, "Mens Singles", false),
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
                .isInstanceOf(ForbiddenException.class);
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
                .isInstanceOf(ForbiddenException.class);

        verify(teamRepository, never()).save(any());
        verifyNoInteractions(teamMapper);
    }

    private void playerResponseInvocation() {
        when(playerMapper.toResponse(any(Player.class)))
                .thenAnswer(invocation -> {
                    Player player = invocation.getArgument(0);
                    return new PlayerResponse(
                            player.getId(),
                            "Player",
                            player.isMale(),
                            player.isSeeded(),
                            player.getRank(),
                            player.getMobCode(),
                            player.getMobile(),
                            new UserResponseLite(
                                    player.getUser().getId(),
                                    player.getUser().getFirstName(),
                                    player.getUser().getLastName()
                            ),
                            null,
                            new CategoryResponseLite(
                                    player.getCategory().getId(),
                                    player.getCategory().getName().getDisplayName(),
                                    player.getCategory().isDoubles()
                            ),
                            null
                    );
                });
    }

    @Test
    void generateTeams_forDoubles_returnsExpected() {
        Category category = CategoryTestBuilder.aCategory().build();
        category.setDoubles(true);

        for (long i = 1; i <= 16; i++) {
            Player player = PlayerTestBuilder.aPlayer().withId(i).build();
            player.setSeeded(true);
            category.getPlayers().add(player);
        }
        for (long i = 17; i <= 32; i++) {
            Player player = PlayerTestBuilder.aPlayer().withId(i).build();
            category.getPlayers().add(player);
        }

        when(categoryFinder.getCategoryOrThrow(category.getId())).thenReturn(category);
        playerResponseInvocation();

        List<List<PlayerResponse>> teams = teamService.generateTeams(
                category.getId(),
                category.getTournament().getHost().getId()
        );

        assertThat(teams.size()).isEqualTo(16);
        for (List<PlayerResponse> team : teams) {
            assertThat(team.size()).isEqualTo(2);
            List<PlayerResponse> seeded = team.stream().filter(PlayerResponse::seeded).toList();
            List<PlayerResponse> nonSeeded = team.stream().filter(p -> !p.seeded()).toList();
            assertThat(seeded.size()).isEqualTo(1);
            assertThat(nonSeeded.size()).isEqualTo(1);
        }
    }

    @Test
    void generateTeams_forMixed_returnsExpected() {
        Category category = CategoryTestBuilder.aCategory().build();
        category.setName(Type.MIXED_DOUBLES);
        category.setDoubles(true);

        for (long i = 1; i <= 8; i++) {
            Player player = PlayerTestBuilder.aPlayer().withId(i).build();
            player.setSeeded(true);
            category.getPlayers().add(player);
        }
        for (long i = 9; i <= 16; i++) {
            Player player = PlayerTestBuilder.aPlayer().withId(i).build();
            category.getPlayers().add(player);
        }
        for (long i = 17; i <= 24; i++) {
            Player player = PlayerTestBuilder.aPlayer().withId(i).build();
            player.setSeeded(true);
            player.setMale(false);
            category.getPlayers().add(player);
        }
        for (long i = 25; i <= 32; i++) {
            Player player = PlayerTestBuilder.aPlayer().withId(i).build();
            player.setMale(false);
            category.getPlayers().add(player);
        }

        when(categoryFinder.getCategoryOrThrow(category.getId())).thenReturn(category);
        playerResponseInvocation();

        List<List<PlayerResponse>> teams = teamService.generateTeams(
                category.getId(),
                category.getTournament().getHost().getId()
        );

        assertThat(teams.size()).isEqualTo(16);
        for (List<PlayerResponse> team : teams) {
            assertThat(team.size()).isEqualTo(2);
            List<PlayerResponse> seeded = team.stream().filter(PlayerResponse::seeded).toList();
            List<PlayerResponse> nonSeeded = team.stream().filter(p -> !p.seeded()).toList();
            List<PlayerResponse> male = team.stream().filter(PlayerResponse::male).toList();
            List<PlayerResponse> female = team.stream().filter(p -> !p.male()).toList();
            assertThat(seeded.size()).isEqualTo(1);
            assertThat(nonSeeded.size()).isEqualTo(1);
            System.out.println(male.size());
            assertThat(male.size()).isEqualTo(1);
            assertThat(female.size()).isEqualTo(1);
        }
    }
}
