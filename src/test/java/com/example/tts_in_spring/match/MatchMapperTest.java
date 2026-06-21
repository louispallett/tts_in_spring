package com.example.tts_in_spring.match;

import com.example.tts_in_spring.participant.ParticipantMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Import({MatchMapperImpl.class})
public class MatchMapperTest {
    @MockitoBean
    private ParticipantMapper participantMapper;

    @Autowired
    private MatchMapper matchMapper;

    @Test
    void toResponse_mapsAllFields() {
        MatchResponse response = matchMapper.toResponse(MatchTestBuilder.aMatch().build());

        assertThat(response.id()).isEqualTo(100000L);
        assertThat(response.tournamentRoundText()).isEqualTo("1");
        assertThat(response.state()).isEqualTo("SCHEDULED");
        assertThat(response.deadline()).isBeforeOrEqualTo(Instant.now());
        assertThat(response.qualifyingMatch()).isFalse();
        assertThat(response.category()).isNotNull();
        assertThat(response.category().id()).isEqualTo(100L);
        assertThat(response.nextMatch()).isNull();
    }

    @Test
    void toResponse_mapsNextMatch() {
        Match nextMatch = MatchTestBuilder.aMatch().withId(100001L).build();
        MatchResponse response = matchMapper.toResponse(MatchTestBuilder.aMatch().withNextMatch(nextMatch).build());

        assertThat(response.nextMatch()).isNotNull();
        assertThat(response.nextMatch().id()).isEqualTo(100001L);
    }

    @Test
    void toResponseLite_mapsAllFields() {
        MatchResponseLite response = matchMapper.toResponseLite(MatchTestBuilder.aMatch().build());

        assertThat(response.id()).isEqualTo(100000L);
        assertThat(response.tournamentRoundText()).isEqualTo("1");
        assertThat(response.state()).isEqualTo("SCHEDULED");
        assertThat(response.deadline()).isBeforeOrEqualTo(Instant.now());
        assertThat(response.qualifyingMatch()).isFalse();
    }
}
