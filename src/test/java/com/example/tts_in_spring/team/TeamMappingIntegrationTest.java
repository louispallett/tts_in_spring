package com.example.tts_in_spring.team;

import com.example.tts_in_spring.match.MatchMapperImpl;
import com.example.tts_in_spring.participant.Participant;
import com.example.tts_in_spring.participant.ParticipantMapperImpl;
import com.example.tts_in_spring.participant.dto.ParticipantResponseLite;
import com.example.tts_in_spring.participant.ParticipantTestBuilder;
import com.example.tts_in_spring.player.Player;
import com.example.tts_in_spring.player.PlayerMapperImpl;
import com.example.tts_in_spring.player.dto.PlayerResponseLite;
import com.example.tts_in_spring.player.PlayerTestBuilder;
import com.example.tts_in_spring.team.dto.TeamResponse;
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
public class TeamMappingIntegrationTest {
    @Autowired
    private TeamMapper teamMapper;

    @Test
    void toResponse_mapsAllScalarFields()  {
        TeamResponse response = teamMapper.toResponse(TeamTestBuilder.aTeam().build());

        assertThat(response.id()).isEqualTo(10000L);
        assertThat(response.category()).isNotNull();
        assertThat(response.category().id()).isEqualTo(100L);
    }

    @Test
    void toResponse_withPlayer_mapsFullChain() {
        Team team = TeamTestBuilder.aTeam().build();
        Player player = PlayerTestBuilder.aPlayer().build();
        team.getPlayers().add(player);

        TeamResponse response = teamMapper.toResponse(team);

        assertThat(response.players()).hasSize(1);

        PlayerResponseLite mapped = response.players().getFirst();

        assertThat(mapped.id()).isEqualTo(1000L);
        assertThat(mapped.male()).isTrue();
        assertThat(mapped.rank()).isEqualTo(0);
    }

    @Test
    void toResponse_withParticipant_mapsFullChain() {
        Team team = TeamTestBuilder.aTeam().build();
        Participant participant = ParticipantTestBuilder.aParticipant().withTeam(team).build();
        team.getParticipants().add(participant);

        TeamResponse response = teamMapper.toResponse(team);

        assertThat(response.participants()).hasSize(1);

        ParticipantResponseLite mapped = response.participants().getFirst();
        assertThat(mapped.id()).isEqualTo(1000000L);
        assertThat(mapped.resultText()).isEmpty();
        assertThat(mapped.winner()).isFalse();
    }
}
