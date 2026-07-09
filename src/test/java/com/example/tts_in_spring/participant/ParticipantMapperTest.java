package com.example.tts_in_spring.participant;

import com.example.tts_in_spring.participant.dto.ParticipantResponse;
import com.example.tts_in_spring.participant.dto.ParticipantResponseLite;
import com.example.tts_in_spring.player.PlayerTestBuilder;
import com.example.tts_in_spring.team.TeamTestBuilder;
import com.example.tts_in_spring.player.Player;
import com.example.tts_in_spring.team.Team;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

public class ParticipantMapperTest {
    private final ParticipantMapper participantMapper = Mappers.getMapper(ParticipantMapper.class);

    @Test
    void toResponse_mapsAllFields() {
        ParticipantResponse response = participantMapper.toResponse(
                ParticipantTestBuilder.aParticipant().build()
        );

        assertThat(response.id()).isEqualTo(1000000L);
        assertThat(response.resultText()).isEmpty();
        assertThat(response.winner()).isFalse();
        assertThat(response.player()).isNull();
        assertThat(response.team()).isNull();
        assertThat(response.match()).isNotNull();
        assertThat(response.match().id()).isEqualTo(100000L);
    }

    @Test
    void toResponse_mapsPlayer() {
        Player player = PlayerTestBuilder.aPlayer().build();
        ParticipantResponse response = participantMapper.toResponse(
                ParticipantTestBuilder.aParticipant().withPlayer(player).build()
        );

        assertThat(response.player()).isNotNull();
        assertThat(response.team()).isNull();
        assertThat(response.player().id()).isEqualTo(1000L);
    }

    @Test
    void toResponse_mapsTeam() {
        Team team = TeamTestBuilder.aTeam().build();
        ParticipantResponse response = participantMapper.toResponse(
                ParticipantTestBuilder.aParticipant().withTeam(team).build()
        );

        assertThat(response.player()).isNull();
        assertThat(response.team()).isNotNull();
        assertThat(response.team().id()).isEqualTo(10000L);
    }

    @Test
    void toResponseLite_mapsAllFields() {
        ParticipantResponseLite response = participantMapper.toResponseLite(
                ParticipantTestBuilder.aParticipant().build()
        );

        assertThat(response.id()).isEqualTo(1000000L);
        assertThat(response.resultText()).isEmpty();
        assertThat(response.winner()).isFalse();
    }
}
