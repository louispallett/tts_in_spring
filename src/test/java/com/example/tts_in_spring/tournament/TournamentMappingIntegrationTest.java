package com.example.tts_in_spring.tournament;

import com.example.tts_in_spring.category.CategoryMapperImpl;
import com.example.tts_in_spring.category.CategoryTestBuilder;
import com.example.tts_in_spring.category.CategoryResponseLite;
import com.example.tts_in_spring.category.Category;
import com.example.tts_in_spring.match.MatchMapperImpl;
import com.example.tts_in_spring.participant.ParticipantMapperImpl;
import com.example.tts_in_spring.player.PlayerMapperImpl;
import com.example.tts_in_spring.team.TeamMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Import({
        TournamentMapperImpl.class,
        CategoryMapperImpl.class,
        PlayerMapperImpl.class,
        TeamMapperImpl.class,
        MatchMapperImpl.class,
        ParticipantMapperImpl.class
})
public class TournamentMappingIntegrationTest {
    @Autowired
    private TournamentMapper tournamentMapper;

    @Test
    void toResponse_mapsAllScalarFields() {
        TournamentResponse response = tournamentMapper.toResponse(TournamentTestBuilder.aTournament().build());

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.name()).isEqualTo("Test Tournament");
        assertThat(response.host().id()).isEqualTo(1L);
        assertThat(response.host().firstName()).isEqualTo("John");
        assertThat(response.host().lastName()).isEqualTo("Doe");
        assertThat(response.stage()).isEqualTo("SIGN_UP");
        assertThat(response.showMobile()).isFalse();
    }

    @Test
    void toResponse_withNoCategories_returnsEmptyList() {
        TournamentResponse response = tournamentMapper.toResponse(TournamentTestBuilder.aTournament().build());

        assertThat(response.categories()).isNotNull().isEmpty();
    }

    @Test
    void toResponse_withCategories_mapsFullChain() {
        Tournament tournament = TournamentTestBuilder.aTournament().build();
        Category category = CategoryTestBuilder.aCategory().build();
        tournament.getCategories().add(category);

        TournamentResponse response = tournamentMapper.toResponse(tournament);

        assertThat(response.categories()).hasSize(1);

        CategoryResponseLite mapped = response.categories().getFirst();
        assertThat(mapped.id()).isEqualTo(100L);
        assertThat(mapped.name()).isEqualTo("Mens Singles");
        assertThat(mapped.locked()).isFalse();
        assertThat(mapped.doubles()).isFalse();
    }
}
