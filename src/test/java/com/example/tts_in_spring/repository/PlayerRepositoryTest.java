package com.example.tts_in_spring.repository;

import com.example.tts_in_spring.dto.PlayerResponse;
import com.example.tts_in_spring.model.Player;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class PlayerRepositoryTest {
    @Test
    void constructor_mapsBasicFields() {
        Player player = Mockito.mock(Player.class);

        when(player.getId()).thenReturn(10L);
        when(player.isMale()).thenReturn(true);
        when(player.getRank()).thenReturn(1);
        when(player.isSeeded()).thenReturn(false);

        PlayerResponse response = new PlayerResponse(player);

        assertThat(response.id).isEqualTo(10L);
        assertThat(response.male).isTrue();
        assertThat(response.rank).isEqualTo(1);
        assertThat(response.seeded).isFalse();
        assertThat(response.tournament).isNull();
        assertThat(response.category).isNull();
        assertThat(response.user).isNull();
    }
}
