package com.example.tts_in_spring.mapper;

import com.example.tts_in_spring.builder.TournamentTestBuilder;
import com.example.tts_in_spring.dto.tournament.TournamentResponse;
import com.example.tts_in_spring.dto.tournament.TournamentResponseHost;
import com.example.tts_in_spring.dto.tournament.TournamentResponseLite;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Import({TournamentMapperImpl.class})
public class TournamentMapperTest {
    @MockitoBean
    private CategoryMapper categoryMapper;

    @Autowired
    private TournamentMapper tournamentMapper;

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
    void toResponseHost_mapsAllFields() {
        TournamentResponseHost response = tournamentMapper.toResponseHost(TournamentTestBuilder.aTournament().build());

        assertThat(response.code()).isEqualTo("1234567");
    }
}
