package com.example.tts_in_spring.dto;

import com.example.tts_in_spring.model.Participant;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class ParticipantResponseTest {
    @Test
    void constructor_mapsBasicFields() {
        Participant participant = Mockito.mock(Participant.class);

        when(participant.getId()).thenReturn(10L);
        when(participant.getResultText()).thenReturn("6-6");
        when(participant.isWinner()).thenReturn(true);
        when(participant.getStatus()).thenReturn("PLAYED");

        ParticipantResponse response = new ParticipantResponse(participant);

        assertThat(response.id).isEqualTo(10L);
        assertThat(response.resultText).isEqualTo("6-6");
        assertThat(response.isWinner).isTrue();
        assertThat(response.status).isEqualTo("PLAYED");
        assertThat(response.player).isNull();
        assertThat(response.team).isNull();
        assertThat(response.match).isNull();
    }
}
