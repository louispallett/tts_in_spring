package com.example.tts_in_spring.tournament;

import com.example.tts_in_spring.category.CategoryMapper;
import com.example.tts_in_spring.tournament.dto.*;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;

public class TournamentMapperTest {
    @MockitoBean
    private CategoryMapper categoryMapper;

    private final TournamentMapper tournamentMapper = Mappers.getMapper(TournamentMapper.class);

    @Test
    void toResponse_mapsAllFields() {
        TournamentResponse response = tournamentMapper.toResponse(TournamentTestBuilder.aTournament().build());

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.name()).isEqualTo("Test Tournament");
        assertThat(response.showMobile()).isFalse();
        assertThat(response.host().id()).isEqualTo(1L);
        assertThat(response.host().firstName()).isEqualTo("John");
        assertThat(response.host().lastName()).isEqualTo("Doe");
    }

    @Test
    void toResponseLite_mapsAllFields() {
        TournamentResponseLite response = tournamentMapper.toResponseLite(TournamentTestBuilder.aTournament().build());

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.name()).isEqualTo("Test Tournament");
        assertThat(response.showMobile()).isFalse();
    }

    @Test
    void toEntity_mapsAllowedFieldsOnly() {
        TournamentRequest request = new TournamentRequest(
                "Test Tournament",
                false,
                true, false, false, false, false
        );

        Tournament tournament = tournamentMapper.toEntity(request);

        assertThat(tournament).isNotNull();

        assertThat(tournament.getName()).isEqualTo("Test Tournament");
        assertThat(tournament.isShowMobile()).isFalse();

        assertThat(tournament.getId()).isNull();
        assertThat(tournament.getStage()).isNull();
        assertThat(tournament.getCode()).isNull();
        assertThat(tournament.getHost()).isNull();
    }

    @Test
    void updateNameEntity_mapsFields() {
        Tournament tournament = TournamentTestBuilder.aTournament().build();

        TournamentNameUpdateRequest request = new TournamentNameUpdateRequest("New Tournament Name");

        tournamentMapper.updateNameEntity(request, tournament);

        assertThat(tournament.getName()).isEqualTo("New Tournament Name");
        assertThat(tournament.getHost().getId()).isNotNull().isEqualTo(1L);
    }

    @Test
    void updateStageEntity_mapsFields() {
        Tournament tournament = TournamentTestBuilder.aTournament().build();

        TournamentStageUpdateRequest request = new TournamentStageUpdateRequest("DRAW");

        tournamentMapper.updateStageEntity(request, tournament);

        assertThat(tournament.getStage()).isEqualTo("DRAW");
    }

    @Test
    void updateShowMobileEntity_mapsFields() {
        Tournament tournament = TournamentTestBuilder.aTournament().build();

        TournamentShowMobileUpdateRequest request = new TournamentShowMobileUpdateRequest(true);

        tournamentMapper.updateShowMobileEntity(request, tournament);

        assertThat(tournament.isShowMobile()).isTrue();
    }
}
