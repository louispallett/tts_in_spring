package com.example.tts_in_spring.participant;

import com.example.tts_in_spring.match.Match;
import com.example.tts_in_spring.team.Team;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class ParticipantTest {
    @Test
    void settersAndGetters_workCorrectly() {
        Team team = new Team();
        Match match = new Match();
        Participant p = new Participant();

        p.setResultText("");
        p.setWinner(false);
        p.setStatus("");
        p.setTeam(team);
        p.setMatch(match);

        assertThat(p.getResultText()).isEqualTo("");
        assertThat(p.isWinner()).isFalse();
        assertThat(p.getStatus()).isEqualTo("");
        assertThat(p.getTeam()).isNotNull();
        assertThat(p.getTeam()).isSameAs(team);
        assertThat(p.getMatch()).isSameAs(match);
        assertThat(p.getPlayer()).isNull();
    }
}
