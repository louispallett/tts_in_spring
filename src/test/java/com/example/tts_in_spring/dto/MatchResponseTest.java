package com.example.tts_in_spring.dto;

import com.example.tts_in_spring.model.Match;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class MatchResponseTest {
    @Test
    void constructor_mapsBasicFields() {
        Match match = Mockito.mock(Match.class);
        Instant date = Instant.now();

        when(match.getId()).thenReturn(10L);
        when(match.getTournamentRoundText()).thenReturn("5");
        when(match.getState()).thenReturn("SCHEDULED");
        when(match.getDate()).thenReturn(date);
        when(match.getUpdateNumber()).thenReturn(0);
        when(match.isQualifyingMatch()).thenReturn(false);

        MatchResponse response = new MatchResponse(match);

        assertThat(response.id).isEqualTo(10L);
        assertThat(response.tournamentRoundText).isEqualTo("5");
        assertThat(response.state).isEqualTo("SCHEDULED");
        assertThat(response.date).isEqualTo(date);
        assertThat(response.updateNumber).isEqualTo(0);
        assertThat(response.qualifyingMatch).isFalse();
        assertThat(response.category).isNull();
        assertThat(response.nextMatch).isNull();
        assertThat(response.previousMatches).isNull();
        assertThat(response.participants).isNull();

    }
}
