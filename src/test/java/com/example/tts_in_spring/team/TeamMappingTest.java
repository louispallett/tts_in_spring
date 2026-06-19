package com.example.tts_in_spring.team;

import com.example.tts_in_spring.participant.ParticipantMapper;
import com.example.tts_in_spring.player.PlayerMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Import({TeamMapperImpl.class})
public class TeamMappingTest {
    @MockitoBean
    private PlayerMapper playerMapper;

    @MockitoBean
    private ParticipantMapper participantMapper;

    @Autowired
    private TeamMapper teamMapper;

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
