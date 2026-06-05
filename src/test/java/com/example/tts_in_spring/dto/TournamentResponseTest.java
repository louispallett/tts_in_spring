package com.example.tts_in_spring.dto;

import com.example.tts_in_spring.model.Tournament;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class TournamentResponseTest {

    @Test
    void constructor_mapsBasicFields() {
        Tournament tournament = Mockito.mock(Tournament.class);

        when(tournament.getId()).thenReturn(10L);
        when(tournament.getName()).thenReturn("My Tournament");
        when(tournament.getStage()).thenReturn("SIGN_UP");
        when(tournament.getCode()).thenReturn("ABC123");
        when(tournament.getShowMobile()).thenReturn(Boolean.TRUE);

        TournamentResponse response = new TournamentResponse(tournament);

        assertThat(response.id).isEqualTo(10L);
        assertThat(response.name).isEqualTo("My Tournament");
        assertThat(response.stage).isEqualTo("SIGN_UP");
        assertThat(response.code).isEqualTo("ABC123");
        assertThat(response.showMobile).isTrue();
        assertThat(response.host).isNull();
        assertThat(response.categories).isNull();
        assertThat(response.players).isNull();
    }
}
