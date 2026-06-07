package com.example.tts_in_spring.dto;

import com.example.tts_in_spring.model.Player;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class PlayerResponseTest {
    @Test
    void constructor_mapsBasicFields() {
        Player player = Mockito.mock(Player.class);

        when(player.getId()).thenReturn(10L);
        when(player.getRank()).thenReturn(1);
        when(player.isSeeded()).thenReturn(true);
        when(player.isMale()).thenReturn(false);

        PlayerResponse response = new PlayerResponse(player);

        assertThat(response.id).isEqualTo(10L);
        assertThat(response.rank).isEqualTo(1);
        assertThat(response.seeded).isTrue();
        assertThat(response.male).isFalse();
        assertThat(response.user).isNull();
        assertThat(response.category).isNull();
    }
}
