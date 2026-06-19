package com.example.tts_in_spring.player;

import com.example.tts_in_spring.match.MatchMapperImpl;
import com.example.tts_in_spring.participant.ParticipantMapperImpl;
import com.example.tts_in_spring.participant.ParticipantTestBuilder;
import com.example.tts_in_spring.team.TeamMapperImpl;
import com.example.tts_in_spring.team.TeamTestBuilder;
import com.example.tts_in_spring.participant.ParticipantResponseLite;
import com.example.tts_in_spring.participant.Participant;
import com.example.tts_in_spring.team.Team;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Import({
        PlayerMapperImpl.class,
        TeamMapperImpl.class,
        MatchMapperImpl.class,
        ParticipantMapperImpl.class
})
public class PlayerMappingIntegrationTest {
    @Autowired
    private PlayerMapper playerMapper;

    @Test
    void toResponse_mapsAllScalarFields() {
        PlayerResponse response = playerMapper.toResponse(PlayerTestBuilder.aPlayer().build());

        assertThat(response.id()).isEqualTo(1000L);
        assertThat(response.male()).isTrue();
        assertThat(response.seeded()).isFalse();
        assertThat(response.rank()).isEqualTo(0);
        assertThat(response.user()).isNotNull();
        assertThat(response.user().id()).isEqualTo(2L);
        assertThat(response.category()).isNotNull();
        assertThat(response.category().id()).isEqualTo(100L);
        assertThat(response.team()).isNull();
    }

    @Test
    void toResponse_mapsTeamField() {
        Team team = TeamTestBuilder.aTeam().build();
        PlayerResponse response = playerMapper.toResponse(PlayerTestBuilder.aPlayer().withTeam(team).build());

        assertThat(response.team()).isNotNull();
        assertThat(response.team().id()).isEqualTo(10000L);
    }

    @Test
    void toResponse_withNoParticipants_returnsEmptyList() {
        PlayerResponse response = playerMapper.toResponse(PlayerTestBuilder.aPlayer().build());

        assertThat(response.participants()).isNotNull().isEmpty();
    }

    @Test
    void toResponse_withPlayerParticipant_mapsFullChain() {
        Player player = PlayerTestBuilder.aPlayer().build();
        Participant participant = ParticipantTestBuilder.aParticipant().withPlayer(player).build();
        player.getParticipants().add(participant);

        PlayerResponse response = playerMapper.toResponse(player);

        assertThat(response.participants()).hasSize(1);

        ParticipantResponseLite mapped = response.participants().getFirst();
        assertThat(mapped.id()).isEqualTo(1000000L);
        assertThat(mapped.resultText()).isEmpty();
        assertThat(mapped.isWinner()).isFalse();
        assertThat(mapped.status()).isEmpty();
    }
}
