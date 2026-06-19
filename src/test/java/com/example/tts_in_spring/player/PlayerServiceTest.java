package com.example.tts_in_spring.player;

import com.example.tts_in_spring.category.CategoryTestBuilder;
import com.example.tts_in_spring.category.CategoryResponseLite;
import com.example.tts_in_spring.user.UserResponseLite;
import com.example.tts_in_spring.category.Category;
import com.example.tts_in_spring.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
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

    @InjectMocks
    private PlayerService playerService;

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
    void getAllPlayers_whenEmpty_returnsEmptyList() {
        when(playerRepository.findAll()).thenReturn(List.of());

        assertThat(playerService.getAllPlayers()).isEmpty();
    }

    @Test
    void getPlayerById_withPlayer_returnsMappedResponse() {
        Player player = PlayerTestBuilder.aPlayer().build();
        PlayerResponse response = buildPlayerResponse();

        when(playerRepository.findById(player.getId())).thenReturn(Optional.of(player));
        when(playerMapper.toResponse(player)).thenReturn(response);

        assertThat(playerService.getPlayerById(player.getId(), player.getUser().getId())).isEqualTo(response);
    }

    @Test
    void getPlayerById_withHost_returnsMappedResponse() {
        Player player = PlayerTestBuilder.aPlayer().build();
        PlayerResponse response = buildPlayerResponse();

        when(playerRepository.findById(player.getId())).thenReturn(Optional.of(player));
        when(playerMapper.toResponse(player)).thenReturn(response);

        assertThat(playerService.getPlayerById(player.getId(), player.getCategory().getTournament().getHost().getId())).isEqualTo(response);
    }

    @Test
    void getPlayerById_whenNotFound_throws404() {
        when(playerRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> playerService.getPlayerById(9999L, 1L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.NOT_FOUND)
                );
    }

    @Test
    void getPlayerById_whenNotAuthorised_throws403() {
        Player player = PlayerTestBuilder.aPlayer().build();

        when(playerRepository.findById(player.getId())).thenReturn(Optional.of(player));

        assertThatThrownBy(() -> playerService.getPlayerById(player.getId(), 3L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.FORBIDDEN)
                );
    }

    @Test
    void createPlayer_whenHost_savesAndReturnsMappedLite() {
        Category category = CategoryTestBuilder.aCategory().build();
        PlayerRequest request = buildPlayerRequest(category.getTournament().getHost(), category);

        Player saved = PlayerTestBuilder.aPlayer().withCategory(category).build();
        PlayerResponseLite lite = new PlayerResponseLite(
                1000L,
                true,
                false,
                0
        );

        when(playerMapper.toEntity(request)).thenReturn(saved);
        when(playerRepository.save(any(Player.class))).thenReturn(saved);
        when(playerMapper.toResponseLite(saved)).thenReturn(lite);

        assertThat(playerService.createPlayer(request, category.getTournament().getHost().getId())).isEqualTo(lite);
    }

    @Test
    void createPlayer_whenNotHost_throws403() {
        Category category = CategoryTestBuilder.aCategory().build();

        PlayerRequest request = buildPlayerRequest(category.getTournament().getHost(), category);

        assertThatThrownBy(() -> playerService.createPlayer(request, 3L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.FORBIDDEN)
                );

        verify(playerRepository, never()).save(any());
        verifyNoInteractions(playerMapper);
    }

    @Test
    void updatePlayerRank_whenHost_savesAndReturnsMappedLite() {
        Player player = PlayerTestBuilder.aPlayer().build();

        PlayerUpdateRankRequest request = new PlayerUpdateRankRequest();
        request.setRank(1);

        Player updatedPlayer = PlayerTestBuilder.aPlayer().build();
        updatedPlayer.setRank(1);
        PlayerResponseLite lite = new PlayerResponseLite(
                1000L,
                true,
                false,
                1
        );

        when(playerRepository.findById(player.getId())).thenReturn(Optional.of(player));
        when(playerRepository.save(any(Player.class))).thenReturn(updatedPlayer);
        when(playerMapper.toResponseLite(updatedPlayer)).thenReturn(lite);

        PlayerResponseLite result = playerService.updateRank(player.getId(), request, player.getCategory().getTournament().getHost().getId());
        assertThat(result).isEqualTo(lite);

        verify(playerMapper).updateRankEntity(request, player);
        verify(playerRepository).save(player);
        verify(playerMapper).toResponseLite(updatedPlayer);
    }

    @Test
    void updatePlayerRank_whenNotHost_throws403() {
        Player player = PlayerTestBuilder.aPlayer().build();

        PlayerUpdateRankRequest request = new PlayerUpdateRankRequest();
        request.setRank(1);

        when(playerRepository.findById(player.getId())).thenReturn(Optional.of(player));

        assertThatThrownBy(() -> playerService.updateRank(player.getId(), request, 3L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.FORBIDDEN)
                );
        verify(playerRepository, never()).save(any());
        verifyNoInteractions(playerMapper);
    }

    @Test
    void updatePlayerRank_whenNotFound_throws404() {
        PlayerUpdateRankRequest request = new PlayerUpdateRankRequest();
        request.setRank(1);

        when(playerRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> playerService.updateRank(9999L, request, 1L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.NOT_FOUND));
        verify(playerRepository, never()).save(any());
    }

    @Test
    void updatePlayerSeeded_whenHost_savesAndReturnsMappedLite() {
        Player player = PlayerTestBuilder.aPlayer().build();

        PlayerUpdateSeededRequest request = new PlayerUpdateSeededRequest();
        request.setSeeded(true);

        Player updatedPlayer = PlayerTestBuilder.aPlayer().build();
        updatedPlayer.setSeeded(true);
        PlayerResponseLite lite = new PlayerResponseLite(
                1000L,
                true,
                true,
                0
        );

        when(playerRepository.findById(player.getId())).thenReturn(Optional.of(player));
        when(playerRepository.save(any(Player.class))).thenReturn(updatedPlayer);
        when(playerMapper.toResponseLite(updatedPlayer)).thenReturn(lite);

        PlayerResponseLite result = playerService.updateSeeded(player.getId(), request, player.getCategory().getTournament().getHost().getId());
        assertThat(result).isEqualTo(lite);

        verify(playerMapper).updateSeededEntity(request, player);
        verify(playerRepository).save(player);
        verify(playerMapper).toResponseLite(updatedPlayer);
    }

    @Test
    void updatePlayerSeeded_whenNotHost_throws403() {
        Player player = PlayerTestBuilder.aPlayer().build();

        PlayerUpdateSeededRequest request = new PlayerUpdateSeededRequest();
        request.setSeeded(true);

        when(playerRepository.findById(player.getId())).thenReturn(Optional.of(player));

        assertThatThrownBy(() -> playerService.updateSeeded(player.getId(), request, 3L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.FORBIDDEN)
                );
        verify(playerRepository, never()).save(any());
        verifyNoInteractions(playerMapper);
    }

    @Test
    void updatePlayerSeeded_whenNotFound_throws404() {
        PlayerUpdateSeededRequest request = new PlayerUpdateSeededRequest();
        request.setSeeded(true);

        when(playerRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> playerService.updateSeeded(9999L, request, 1L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.NOT_FOUND));
        verify(playerRepository, never()).save(any());
    }

    @Test
    void deletePlayer_whenHost_deletesPlayer() {
        Player player = PlayerTestBuilder.aPlayer().build();

        when(playerRepository.findById(player.getId())).thenReturn(Optional.of(player));

        playerService.delete(player.getId(), player.getCategory().getTournament().getHost().getId());

        verify(playerRepository).delete(player);
    }

    @Test
    void deletePlayer_whenNotHost_throws403() {
        Player player = PlayerTestBuilder.aPlayer().build();

        when(playerRepository.findById(player.getId())).thenReturn(Optional.of(player));

        assertThatThrownBy(() -> playerService.delete(player.getId(), 3L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.FORBIDDEN)
                );
        verify(playerRepository, never()).save(any());
    }

    @Test
    void deletePlayer_whenNotFound_throws404() {
        when(playerRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> playerService.delete(9999L, 1L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.NOT_FOUND));
        verify(playerRepository, never()).save(any());
    }
}