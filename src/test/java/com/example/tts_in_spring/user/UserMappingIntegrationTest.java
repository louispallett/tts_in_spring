package com.example.tts_in_spring.user;

import com.example.tts_in_spring.mapper.*;
import com.example.tts_in_spring.tournament.TournamentTestBuilder;
import com.example.tts_in_spring.tournament.TournamentResponseLite;
import com.example.tts_in_spring.tournament.Tournament;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Import({
        UserMapperImpl.class,
        TournamentMapperImpl.class,
        CategoryMapperImpl.class,
        PlayerMapperImpl.class,
        TeamMapperImpl.class,
        MatchMapperImpl.class,
        ParticipantMapperImpl.class
})
class UserMappingIntegrationTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    void toResponse_mapsAllScalarFields() {
        UserResponse response = userMapper.toResponse(UserTestBuilder.aUser().build());

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.firstName()).isEqualTo("John");
        assertThat(response.lastName()).isEqualTo("Doe");
        assertThat(response.email()).isEqualTo("john.doe@example.com");
        assertThat(response.mobCode()).isEqualTo("+44");
        assertThat(response.mobile()).isEqualTo("1234567890");
    }

    @Test
    void toResponse_withNoTournaments_returnsEmptyList() {
        UserResponse response = userMapper.toResponse(UserTestBuilder.aUser().build());

        assertThat(response.tournaments()).isNotNull().isEmpty();
    }

    @Test
    void toResponse_withTournaments_mapsFullChain() {
        User user = UserTestBuilder.aUser().build();
        Tournament tournament = TournamentTestBuilder.aTournament().build();
        user.getTournaments().add(tournament);

        UserResponse response = userMapper.toResponse(user);

        assertThat(response.tournaments()).hasSize(1);

        TournamentResponseLite mapped = response.tournaments().getFirst();
        assertThat(mapped.id()).isEqualTo(10L);
        assertThat(mapped.name()).isEqualTo("Test Tournament");
        assertThat(mapped.showMobile()).isFalse();
    }
}
