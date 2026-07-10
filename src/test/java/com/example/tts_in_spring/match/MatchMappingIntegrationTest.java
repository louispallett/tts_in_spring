package com.example.tts_in_spring.match;

import com.example.tts_in_spring.match.dto.MatchResponse;
import com.example.tts_in_spring.match.dto.MatchResponseLite;
import com.example.tts_in_spring.participant.ParticipantMapperImpl;
import com.example.tts_in_spring.participant.ParticipantTestBuilder;
import com.example.tts_in_spring.participant.dto.ParticipantResponseLite;
import com.example.tts_in_spring.participant.Participant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Import({
        MatchMapperImpl.class,
        ParticipantMapperImpl.class
})
public class MatchMappingIntegrationTest {
    @Autowired
    private MatchMapper matchMapper;

    @Test
    void toResponse_mapsAllScalarFields() {
        MatchResponse response = matchMapper.toResponse(MatchTestBuilder.aMatch().build());

        assertThat(response.id()).isEqualTo(100000L);
        assertThat(response.tournamentRoundText()).isEqualTo("1");
        assertThat(response.state()).isEqualTo(State.SCHEDULED);
        assertThat(response.deadline()).isBeforeOrEqualTo(Instant.now());
        assertThat(response.qualifyingMatch()).isFalse();
        assertThat(response.category()).isNotNull();
        assertThat(response.category().id()).isEqualTo(100L);
        assertThat(response.nextMatch()).isNull();
    }

    @Test
    void toResponse_mapsNextMatchField() {
        Match nextMatch = MatchTestBuilder.aMatch().withId(100001L).build();
        MatchResponse response = matchMapper.toResponse(MatchTestBuilder.aMatch().withNextMatch(nextMatch).build());

        assertThat(response.nextMatch()).isNotNull();
        assertThat(response.nextMatch().id()).isEqualTo(100001L);
    }

    @Test
    void toResponse_withPreviousMatches_returnsEmptyList() {
        MatchResponse response = matchMapper.toResponse(MatchTestBuilder.aMatch().build());

        assertThat(response.previousMatches()).isNotNull().isEmpty();
    }

    @Test
    void toResponse_withNoParticipants_returnsEmptyList() {
        MatchResponse response = matchMapper.toResponse(MatchTestBuilder.aMatch().build());

        assertThat(response.participants()).isNotNull().isEmpty();
    }

    @Test
    void toResponse_withPreviousMatches_mapsFullChain() {
        Match match = MatchTestBuilder.aMatch().build();
        Match previousMatch = MatchTestBuilder.aMatch().withId(100001L).build();
        match.getPreviousMatches().add(previousMatch);

        MatchResponse response = matchMapper.toResponse(match);

        assertThat(response.previousMatches()).hasSize(1);

        MatchResponseLite mapped = response.previousMatches().getFirst();
        assertThat(mapped.id()).isEqualTo(100001L);
    }

    @Test
    void toResponse_withParticipants_mapsFullChain() {
        Match match = MatchTestBuilder.aMatch().build();
        Participant participant = ParticipantTestBuilder.aParticipant().build();
        match.getParticipants().add(participant);

        MatchResponse response = matchMapper.toResponse(match);

        assertThat(response.participants()).hasSize(1);

        ParticipantResponseLite mapped = response.participants().getFirst();
        assertThat(mapped.id()).isEqualTo(1000000L);
        assertThat(mapped.winner()).isFalse();
    }

}
