package com.example.tts_in_spring.dto;

import com.example.tts_in_spring.model.Team;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class TeamResponseTest {
    @Test
    void constructor_mapsBasicFields() {
        Team team = Mockito.mock(Team.class);

        when(team.getId()).thenReturn(1L);

        TeamResponse response = new TeamResponse(team);

        assertThat(response.id).isEqualTo(1L);
    }
}
