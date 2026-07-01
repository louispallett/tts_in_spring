package com.example.tts_in_spring.match;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MatchFinderTest {
    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private MatchFinder matchFinder;

    @Test
    void getMatchOrThrow_whenMatchExists_returnsMatch() {
        Match match = MatchTestBuilder.aMatch().build();

        when(matchRepository.findById(match.getId())).thenReturn(Optional.of(match));

        assertThat(matchFinder.getMatchOrThrow(match.getId())).isEqualTo(match);
    }

    @Test
    void getMatchOrThrow_whenMatchDoeNotExist_throws404() {
        when(matchRepository.findById(999999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> matchFinder.getMatchOrThrow(999999L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.NOT_FOUND));
    }
}
