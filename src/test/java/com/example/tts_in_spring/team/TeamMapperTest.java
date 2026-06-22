package com.example.tts_in_spring.team;

import com.example.tts_in_spring.participant.ParticipantMapper;
import com.example.tts_in_spring.player.PlayerMapper;
import com.example.tts_in_spring.team.dto.TeamResponse;
import com.example.tts_in_spring.team.dto.TeamResponseLite;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;

public class TeamMapperTest {
    @MockitoBean
    private PlayerMapper playerMapper;

    @MockitoBean
    private ParticipantMapper participantMapper;

    private final TeamMapper teamMapper = Mappers.getMapper(TeamMapper.class);

    @Test
    void toResponse_mapsAllFields() {
        TeamResponse response = teamMapper.toResponse(TeamTestBuilder.aTeam().build());

        assertThat(response.id()).isEqualTo(10000L);
        assertThat(response.category()).isNotNull();
    }

    @Test
    void toResponseLite_mapsAllFields() {
        TeamResponseLite response = teamMapper.toResponseLite(TeamTestBuilder.aTeam().build());

        assertThat(response.id()).isEqualTo(10000L);
    }
}
