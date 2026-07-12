package com.example.tts_in_spring.participant;

import com.example.tts_in_spring.participant.dto.ParticipantResponse;
import com.example.tts_in_spring.player.PlayerTestBuilder;
import com.example.tts_in_spring.player.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Import({ParticipantMapperImpl.class})
public class ParticipantMappingIntegrationTest {
    @Autowired
    private ParticipantMapper participantMapper;

    @Test
    void toResponse_mapsAllScalarFields() {
        ParticipantResponse response = participantMapper.toResponse(ParticipantTestBuilder.aParticipant().build());

        assertThat(response.id()).isEqualTo(1000000L);
        assertThat(response.resultText()).isEmpty();
        assertThat(response.winner()).isFalse();
        assertThat(response.player()).isNull();
        assertThat(response.team()).isNull();
        assertThat(response.match()).isNotNull();
        assertThat(response.match().id()).isEqualTo(100000L);
    }

    @Test
    void toResponse_mapsPlayerField() {
        Player player = PlayerTestBuilder.aPlayer().build();
        ParticipantResponse response = participantMapper.toResponse(
                ParticipantTestBuilder.aParticipant().withPlayer(player).build()
        );

        assertThat(response.player()).isNotNull();
        assertThat(response.team()).isNull();
        assertThat(response.player().id()).isEqualTo(1000L);
    }
}
