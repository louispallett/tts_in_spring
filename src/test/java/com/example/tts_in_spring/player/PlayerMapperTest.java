package com.example.tts_in_spring.player;

import com.example.tts_in_spring.team.TeamTestBuilder;
import com.example.tts_in_spring.participant.ParticipantMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Import({PlayerMapperImpl.class})
public class PlayerMapperTest {
    @MockitoBean
    private ParticipantMapper participantMapper;

    @Autowired
    private PlayerMapper playerMapper;

    @Test
    void toResponse_mapsAllFields() {
        PlayerResponse response = playerMapper.toResponse(PlayerTestBuilder.aPlayer().build());

        assertThat(response.id()).isEqualTo(1000L);
        assertThat(response.male()).isTrue();
        assertThat(response.seeded()).isFalse();
        assertThat(response.rank()).isEqualTo(0);
        assertThat(response.user()).isNotNull();
        assertThat(response.user().id()).isEqualTo(1L);
        assertThat(response.category()).isNotNull();
        assertThat(response.category().id()).isEqualTo(100L);
        assertThat(response.team()).isNull();
    }

    @Test
    void toResponse_mapsTeamField() {
        PlayerResponse response = playerMapper.toResponse(
                PlayerTestBuilder.aPlayer().withTeam(
                        TeamTestBuilder.aTeam().build()
                ).build()
        );

        assertThat(response.team()).isNotNull();
        assertThat(response.team().id()).isEqualTo(10000L);
    }

    @Test
    void toResponseLite_mapsAllFields() {
        PlayerResponseLite response = playerMapper.toResponseLite(PlayerTestBuilder.aPlayer().build());

        assertThat(response.id()).isEqualTo(1000L);
        assertThat(response.male()).isTrue();
        assertThat(response.seeded()).isFalse();
        assertThat(response.rank()).isEqualTo(0);
    }
}
