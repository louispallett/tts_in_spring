package com.example.tts_in_spring.tournament;

import com.example.tts_in_spring.exception.ResourceNotFoundException;
import com.example.tts_in_spring.exception.ForbiddenException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
                .isInstanceOf(ResourceNotFoundException.class);
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
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void checkCode_whenCorrect_returnsCode() {
        Tournament tournament = TournamentTestBuilder.aTournament().build();

        when(tournamentRepository.findByCode(tournament.getCode())).thenReturn(Optional.of(tournament));

        assertThat(tournamentFinder.getTournamentByCodeOrThrow(tournament.getCode())).isEqualTo(tournament);
    }

    @Test
    void checkCode_whenIncorrect_throws404() {
        String fakeCode = "aaaaaaa";
        when(tournamentRepository.findByCode(fakeCode)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tournamentFinder.getTournamentByCodeOrThrow(fakeCode))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
