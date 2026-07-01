package com.example.tts_in_spring.tournament;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TournamentFinderTest {
    @Mock
    private TournamentRepository tournamentRepository;

    @InjectMocks
    private TournamentFinder tournamentFinder;

    @Test
    void getTournamentOrThrow_whenTournamentExists_returnsTournament() {
        Tournament tournament = TournamentTestBuilder.aTournament().build();

        when(tournamentRepository.findById(tournament.getId())).thenReturn(Optional.of(tournament));

        assertThat(tournamentFinder.getTournamentOrThrow(tournament.getId())).isEqualTo(tournament);
    }

    @Test
    void getTournamentOrThrow_whenTournamentDoesNotExist_throws404() {
        when(tournamentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tournamentFinder.getTournamentOrThrow(99L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void assertHost_whenHost_doesNotThrow() {
        Tournament tournament = TournamentTestBuilder.aTournament().build();

        assertThatCode(() -> tournamentFinder.assertHost(tournament, tournament.getHost().getId()))
                .doesNotThrowAnyException();
    }

    @Test
    void assertHost_whenNotHost_throws403() {
        assertThatThrownBy(() -> tournamentFinder.assertHost(TournamentTestBuilder.aTournament().build(), 3L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.FORBIDDEN));
    }
}
