package com.example.tts_in_spring.service;

/* PlayerServiceTest
- A Player can only be created once a category has been created.
- A Player is created when a user signs up to a tournament.
- It also relies on User, since every player is linked to a user at @ManyToOne

Checks:
> All requests <
- Must be authenticated.

> getById() <
Authorized:
- The user of the player
- The host of the category of the tournament

> createPlayer() <
Authorized:
- The user (this is simply done by setting the player.setUser() to currentUser).

> updatePlayerRank() <
> updateSeeded() <
Authorized:
- Host of tournament, changed directly

> updateTeam() <
Authorized:
- Host of tournament, changed as part of team creation.

> deletePlayer <
Authorized:
- Must be host (this is done when removing a player from a category
- Must be user of player (in the specific case of deleting account
*/

import com.example.tts_in_spring.builder.CategoryTestBuilder;
import com.example.tts_in_spring.builder.PlayerTestBuilder;
import com.example.tts_in_spring.builder.TournamentTestBuilder;
import com.example.tts_in_spring.builder.UserTestBuilder;
import com.example.tts_in_spring.dto.category.CategoryResponseLite;
import com.example.tts_in_spring.dto.player.PlayerRequest;
import com.example.tts_in_spring.dto.player.PlayerResponse;
import com.example.tts_in_spring.dto.player.PlayerResponseLite;
import com.example.tts_in_spring.dto.user.UserResponseLite;
import com.example.tts_in_spring.mapper.PlayerMapper;
import com.example.tts_in_spring.model.Category;
import com.example.tts_in_spring.model.Player;
import com.example.tts_in_spring.model.Tournament;
import com.example.tts_in_spring.model.User;
import com.example.tts_in_spring.repository.PlayerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {
    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private PlayerMapper playerMapper;

    @Mock
    private PlayerService playerService;

    @InjectMocks
    private CategoryService categoryService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void mockAuthenticatedUser(User user) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    private PlayerResponse buildPlayerResponse() {
        return new PlayerResponse(
                1000L,
                false,
                false,
                0,
                new UserResponseLite(2L, "John", "Doe"),
                null,
                new CategoryResponseLite(100L, "Mens Singles", false, false),
                null
        );
    }

    private PlayerRequest buildPlayerRequest(User user, Category category) {
        PlayerRequest r = new PlayerRequest();
        r.setMale(true);
        r.setSeeded(false);
        r.setRank(0);
        r.setUser(user);
        r.setCategory(category);

        return r;
    }

    @Test
    void getAllPlayers_returnsMappedList() {
        Player player = PlayerTestBuilder.aPlayer().build();
        PlayerResponse response = buildPlayerResponse();

        when(playerRepository.findAll()).thenReturn(List.of(player));
        when(playerMapper.toResponse(player)).thenReturn(response);

        List<PlayerResponse> result = playerService.getAllPlayers();

        assertThat(result).containsExactly(response);
    }

    @Test
    void getPlayerById_withPlayer_returnsMappedResponse() {
        User currentUser = UserTestBuilder.aUser().withId(2L).build();
        mockAuthenticatedUser(currentUser);

        Player player = PlayerTestBuilder.aPlayer().withUser(currentUser).build();
        PlayerResponse response = buildPlayerResponse();

        when(playerRepository.findById(1000L)).thenReturn(Optional.of(player));
        when(playerMapper.toResponse(player)).thenReturn(response);

        Object result = playerService.getPlayerById(1000L);

        assertThat(result)
                .isInstanceOf(PlayerResponse.class)
                .isEqualTo(response);
    }

    @Test
    void getPlayerById_withHost_returnsMappedResponse() {
        User host = UserTestBuilder.aUser().build();
        mockAuthenticatedUser(host);
        Tournament tournament = TournamentTestBuilder.aTournament().withHost(host).build();
        Category category = CategoryTestBuilder.aCategory().withTournament(tournament).build();

        Player player = PlayerTestBuilder.aPlayer().withCategory(category).build();
        PlayerResponse response = buildPlayerResponse();

        when(playerRepository.findById(1000L)).thenReturn(Optional.of(player));
        when(playerMapper.toResponse(player)).thenReturn(response);

        Object result = playerService.getPlayerById(1000L);

        assertThat(result)
                .isInstanceOf(PlayerResponse.class)
                .isEqualTo(response);
    }

    @Test
    void getPlayerById_whenNotFound_throws404() {
        mockAuthenticatedUser(UserTestBuilder.aUser().build());

        when(playerRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> playerService.getPlayerById(9999L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.NOT_FOUND)
                );
    }

    @Test
    void getPlayerById_whenNotAuthorised_throws403() {
        User outsider = UserTestBuilder.aUser().withId(3L).build();
        mockAuthenticatedUser(outsider);

        User user = UserTestBuilder.aUser().withId(2L).build();
        Player player = PlayerTestBuilder.aPlayer().withUser(user).build();

        when(playerRepository.findById(1000L)).thenReturn(Optional.of(player));

        assertThatThrownBy(() -> playerService.getPlayerById(1000L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.FORBIDDEN)
                );
    }

    @Test
    void createPlayer_savesAndReturnsMappedLite() {
        User user = UserTestBuilder.aUser().build();
        mockAuthenticatedUser(user);
        Category category = CategoryTestBuilder.aCategory().build();
        PlayerRequest request = buildPlayerRequest(user, category);

        Player saved = new Player();
        PlayerResponseLite lite = new PlayerResponseLite(
                1000L,
                true,
                false,
                0
        );

        when(playerRepository.save(any(Player.class))).thenReturn(saved);
        when(playerMapper.toResponseLite(saved)).thenReturn(lite);

        assertThat(playerService.createPlayer(request)).isEqualTo(lite);
        verify(playerRepository).save(any(Player.class));
    }

    // Update rank from 0 to 1
    @Test
    void updatePlayerRank_whenHost_savesAndReturnsMappedLite() {
        User host = UserTestBuilder.aUser().build();
        mockAuthenticatedUser(host);

        Tournament tournament = TournamentTestBuilder.aTournament().withHost(host).build();
        Category category = CategoryTestBuilder.aCategory().withTournament(tournament).build();
        Player player = PlayerTestBuilder.aPlayer().withCategory(category).build();

        PlayerRequest request = new PlayerRequest();
        request.setRank(1);

        Player updatedPlayer = PlayerTestBuilder.aPlayer().withCategory(category).build();
        updatedPlayer.setRank(1);
        PlayerResponseLite lite = new PlayerResponseLite(
                1000L,
                true,
                false,
                1
        );

        when(playerRepository.findById(1000L)).thenReturn(Optional.of(player));
        when(playerRepository.save(any(Player.class))).thenReturn(updatedPlayer);
        when(playerMapper.toResponseLite(updatedPlayer)).thenReturn(lite);

        Object result = playerService.updateRank(1000L, request);

        assertThat(result).isEqualTo(lite);
        assertThat(result.rank()).isEqualTo(1);
        verify(playerRepository).save(any(Player.class));
    }

    @Test
    void updatePlayerRank_whenNotHost_throws403() {
        User user = UserTestBuilder.aUser().withId(2L).build();
        mockAuthenticatedUser(user);

        User host = UserTestBuilder.aUser().build();
        Tournament tournament = TournamentTestBuilder.aTournament().withHost(host).build();
        Category category = CategoryTestBuilder.aCategory().withTournament(tournament).build();
        Player player = PlayerTestBuilder.aPlayer().withCategory(category).build();
        player.setRank(0);

        PlayerRequest request = new PlayerRequest();
        request.setRank(1);

        Player updatedPlayer = PlayerTestBuilder.aPlayer().withCategory(category).build();
        updatedPlayer.setRank(1);
        PlayerResponseLite lite = new PlayerResponseLite(
                1000L,
                true,
                false,
                1
        );

        when(playerRepository.findById(1000L)).thenReturn(Optional.of(player));
        when(playerRepository.save(any(Player.class))).thenReturn(updatedPlayer);
        when(playerMapper.toResponseLite(updatedPlayer)).thenReturn(lite);

        assertThatThrownBy(() -> playerService.updateRank(1000L, request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.FORBIDDEN)
                );
    }

    @Test
    void updatePlayerSeeded_whenHost_savesAndReturnsMappedLite() {
        User host = UserTestBuilder.aUser().build();
        mockAuthenticatedUser(host);

        Tournament tournament = TournamentTestBuilder.aTournament().withHost(host).build();
        Category category = CategoryTestBuilder.aCategory().withTournament(tournament).build();
        Player player = PlayerTestBuilder.aPlayer().withCategory(category).build();
        player.setSeeded(true);

        PlayerRequest request = new PlayerRequest();
        request.setSeeded(true);

        Player updatedPlayer = PlayerTestBuilder.aPlayer().withCategory(category).build();
        updatedPlayer.setSeeded(true);
        PlayerResponseLite lite = new PlayerResponseLite(
                1000L,
                true,
                true,
                1
        );

        when(playerRepository.findById(1000L)).thenReturn(Optional.of(player));
        when(playerRepository.save(any(Player.class))).thenReturn(updatedPlayer);
        when(playerMapper.toResponseLite(updatedPlayer)).thenReturn(lite);

        Object result = playerService.updatePlayerSeeded(1000L, request);

        assertThat(result).isEqualTo(lite);
        assertThat(result.seeded()).isEqualTo(1);
        verify(playerRepository).save(any(Player.class));
    }

    @Test
    void updatePlayerSeeded_whenNotHost_throws403() {
        User user = UserTestBuilder.aUser().withId(2L).build();
        mockAuthenticatedUser(user);

        User host = UserTestBuilder.aUser().build();
        Tournament tournament = TournamentTestBuilder.aTournament().withHost(host).build();
        Category category = CategoryTestBuilder.aCategory().withTournament(tournament).build();
        Player player = PlayerTestBuilder.aPlayer().withCategory(category).build();
        player.setSeeded(true);

        PlayerRequest request = new PlayerRequest();
        request.setSeeded(true);

        Player updatedPlayer = PlayerTestBuilder.aPlayer().withCategory(category).build();
        updatedPlayer.setSeeded(true);
        PlayerResponseLite lite = new PlayerResponseLite(
                1000L,
                true,
                true,
                1
        );

        when(playerRepository.findById(1000L)).thenReturn(Optional.of(player));
        when(playerRepository.save(any(Player.class))).thenReturn(updatedPlayer);
        when(playerMapper.toResponseLite(updatedPlayer)).thenReturn(lite);

        assertThatThrownBy(() -> playerService.updatePlayerSeeded(1000L, request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.FORBIDDEN)
                );
    }

    @Test
    void deletePlayer_whenHost_deletesPlayer() {
        User host = UserTestBuilder.aUser().build();
        mockAuthenticatedUser(host);

        Tournament tournament = TournamentTestBuilder.aTournament().withHost(host).build();
        Category category = CategoryTestBuilder.aCategory().withTournament(tournament).build();
        Player player = PlayerTestBuilder.aPlayer().withCategory(category).build();

        when(playerRepository.findById(1000L)).thenReturn(Optional.of(player));

        playerService.deletePlayer(1000L);

        verify(playerRepository).delete(player);
    }

    @Test
    void deletePlayer_whenNotAuthorized_throws403() {
        User user = UserTestBuilder.aUser().withId(2L).build();
        mockAuthenticatedUser(user);

        User host = UserTestBuilder.aUser().build();
        Tournament tournament = TournamentTestBuilder.aTournament().withHost(host).build();
        Category category = CategoryTestBuilder.aCategory().withTournament(tournament).build();
        Player player = PlayerTestBuilder.aPlayer().withCategory(category).build();

        when(playerRepository.findById(1000L)).thenReturn(Optional.of(player));

        assertThatThrownBy(() -> playerService.deletePlayer(1000L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.FORBIDDEN)
                );
    }
}
