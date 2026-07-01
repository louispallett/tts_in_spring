package com.example.tts_in_spring.team;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeamFinderTest {
    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TeamFinder teamFinder;

    @Test
    void getTeamOrThrow_whenTeamExists_returnsTeam() {
        Team team = TeamTestBuilder.aTeam().build();

        when(teamRepository.findById(team.getId())).thenReturn(Optional.of(team));

        assertThat(teamFinder.getTeamOrThrow(team.getId())).isEqualTo(team);
    }

    @Test
    void getTeamOrThrow_whenTeamDoesNotExist_throws404() {
        when(teamRepository.findById(99999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> teamFinder.getTeamOrThrow(99999L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.NOT_FOUND));
    }
}
