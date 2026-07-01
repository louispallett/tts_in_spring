package com.example.tts_in_spring.player;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlayerFinderTest {
    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerFinder playerFinder;

    @Test
    void getPlayerOrThrow_whenPlayerExists_returnsPlayer() {
        Player player = PlayerTestBuilder.aPlayer().build();

        when(playerRepository.findById(player.getId())).thenReturn(Optional.of(player));

        assertThat(playerFinder.getPlayerOrThrow(player.getId())).isEqualTo(player);
    }
    
    @Test
    void getPlayerOrThrow_whenPlayerDoesNotExist_throws404() {
        when(playerRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> playerFinder.getPlayerOrThrow(9999L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void assertHost_whenHost_doesNotThrow() {
        Player player = PlayerTestBuilder.aPlayer().build();

        assertThatCode(() -> playerFinder.assertHost(player, player.getCategory().getTournament().getHost().getId()))
                .doesNotThrowAnyException();
    }

    @Test
    void assertHost_whenNotHost_throws403() {
        assertThatThrownBy(() -> playerFinder.assertHost(PlayerTestBuilder.aPlayer().build(), 3L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.FORBIDDEN));
    }
}
