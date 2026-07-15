package com.example.tts_in_spring.player;

import com.example.tts_in_spring.category.CategoryFinder;
import com.example.tts_in_spring.category.dto.CategoryResponseLite;
import com.example.tts_in_spring.exception.ForbiddenException;
import com.example.tts_in_spring.player.dto.*;
import com.example.tts_in_spring.user.UserFinder;
import com.example.tts_in_spring.user.dto.UserResponseLite;
import com.example.tts_in_spring.category.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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
    private PlayerFinder playerFinder;

    @Mock
    private CategoryFinder categoryFinder;

    @Mock
    private UserFinder userFinder;

    @InjectMocks
    private PlayerService playerService;

    private PlayerResponse buildPlayerResponse() {
        return new PlayerResponse(
                1000L,
                "Player",
                false,
                false,
                0,
                "+44",
                "1234567890",
                new UserResponseLite(2L, "John", "Doe"),
                null,
                new CategoryResponseLite(100L, "Mens Singles", false, false),
                null
        );
    }

    private PlayerRequest buildPlayerRequest(Category category) {
        return new PlayerRequest(
            true,
            "+44",
            "1234567890",
            category.getId()
        );
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

        when(playerFinder.getPlayerOrThrow(player.getId())).thenReturn(player);
        when(playerMapper.toResponse(player)).thenReturn(response);

        assertThat(playerService.getPlayerById(player.getId(), player.getUser().getId())).isEqualTo(response);
    }

    @Test
    void getPlayerById_withHost_returnsMappedResponse() {
        Player player = PlayerTestBuilder.aPlayer().build();
        PlayerResponse response = buildPlayerResponse();

        when(playerFinder.getPlayerOrThrow(player.getId())).thenReturn(player);
        when(playerMapper.toResponse(player)).thenReturn(response);

        assertThat(playerService.getPlayerById(player.getId(), player.getCategory().getTournament().getHost().getId())).isEqualTo(response);
    }

    @Test
    void getPlayerById_whenNotAuthorised_throws403() {
        Player player = PlayerTestBuilder.aPlayer().build();

        when(playerFinder.getPlayerOrThrow(player.getId())).thenReturn(player);

        assertThatThrownBy(() -> playerService.getPlayerById(player.getId(), 3L))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void updatePlayerRank_whenHost_savesAndReturnsMappedLite() {
        Player player = PlayerTestBuilder.aPlayer().build();

        PlayerUpdateRankRequest request = new PlayerUpdateRankRequest(1);

        Player updatedPlayer = PlayerTestBuilder.aPlayer().build();
        updatedPlayer.setRank(1);
        PlayerResponseLite lite = new PlayerResponseLite(
                1000L,
                "Player",
                true,
                false,
                1
        );

        when(playerFinder.getPlayerOrThrow(player.getId())).thenReturn(player);
        when(playerRepository.save(any(Player.class))).thenReturn(updatedPlayer);
        when(playerMapper.toResponseLite(updatedPlayer)).thenReturn(lite);

        PlayerResponseLite result = playerService.updateRank(player.getId(), request, player.getCategory().getTournament().getHost().getId());
        assertThat(result).isEqualTo(lite);

        verify(playerMapper).updateRankEntity(request, player);
        verify(playerRepository).save(player);
        verify(playerMapper).toResponseLite(updatedPlayer);
    }

    @Test
    void updatePlayerSeeded_whenHost_savesAndReturnsMappedLite() {
        Player player = PlayerTestBuilder.aPlayer().build();

        PlayerUpdateSeededRequest request = new PlayerUpdateSeededRequest(true);

        Player updatedPlayer = PlayerTestBuilder.aPlayer().build();
        updatedPlayer.setSeeded(true);
        PlayerResponseLite lite = new PlayerResponseLite(
                1000L,
                "Player",
                true,
                true,
                0
        );

        when(playerFinder.getPlayerOrThrow(player.getId())).thenReturn(player);
        when(playerRepository.save(any(Player.class))).thenReturn(updatedPlayer);
        when(playerMapper.toResponseLite(updatedPlayer)).thenReturn(lite);

        PlayerResponseLite result = playerService.updateSeeded(player.getId(), request, player.getCategory().getTournament().getHost().getId());
        assertThat(result).isEqualTo(lite);

        verify(playerMapper).updateSeededEntity(request, player);
        verify(playerRepository).save(player);
        verify(playerMapper).toResponseLite(updatedPlayer);
    }

    @Test
    void deletePlayer_whenHost_deletesPlayer() {
        Player player = PlayerTestBuilder.aPlayer().build();

        when(playerFinder.getPlayerOrThrow(player.getId())).thenReturn(player);

        playerService.delete(player.getId(), player.getCategory().getTournament().getHost().getId());

        verify(playerRepository).delete(player);
    }
}