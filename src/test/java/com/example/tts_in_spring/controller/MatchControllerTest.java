package com.example.tts_in_spring.controller;

import com.example.tts_in_spring.dto.MatchResponse;
import com.example.tts_in_spring.model.*;
import com.example.tts_in_spring.repository.MatchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchControllerTest {
    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private MatchController matchController;

    private Match createMatch(Long id, Instant date, boolean qualifyingMatch, Category category) {
        Match m = new Match();
        m.setId(id);
        m.setTournamentRoundText("5");
        m.setState("SCHEDULED");
        m.setDate(date);
        m.setUpdateNumber(0);
        m.setQualifyingMatch(qualifyingMatch);
        m.setCategory(category);

        return m;
    }

    @Test
    void getAllMatches_returnsMappedResponses() {
        Category category = new Category();
        Instant date = Instant.now();
        Match m1 = createMatch(1L, date, false, category);
        Match m2 = createMatch(2L, date, true, category);

        when(matchRepository.findAll()).thenReturn(List.of(m1, m2));

        ResponseEntity<List<MatchResponse>> response = matchController.getAllMatches();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().getFirst().id).isEqualTo(1L);
        assertThat(response.getBody().getFirst().date).isEqualTo(date);
        assertThat(response.getBody().getFirst().category).isEqualTo(category);
        assertThat(response.getBody().getFirst().qualifyingMatch).isFalse();
        assertThat(response.getBody().get(1).id).isEqualTo(2L);
        assertThat(response.getBody().get(1).date).isEqualTo(date);
        assertThat(response.getBody().get(1).category).isEqualTo(category);
        assertThat(response.getBody().get(1).qualifyingMatch).isTrue();
    }

    @Test
    void getMatch_returnsWhenMatchExists() {
        Category category = new Category();
        Instant date = Instant.now();
        Match m = createMatch(1L, date, false, category);
        when(matchRepository.findById(1L)).thenReturn(Optional.of(m));

        ResponseEntity<MatchResponse> response = matchController.getMatch(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id).isEqualTo(1L);
        assertThat(response.getBody().date).isEqualTo(date);
        assertThat(response.getBody().qualifyingMatch).isFalse();
        assertThat(response.getBody().category).isNotNull();
    }

    @Test
    void getUser_returnsNotFoundWhenMissing() {
        when(matchRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<MatchResponse> response = matchController.getMatch(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void createMatch_createsMatch() {
        Match incoming = new Match();
        Category category = new Category();
        Instant date = Instant.now();

        Match saved = createMatch(1L, date, false, category);
        when(matchRepository.save(any(Match.class))).thenReturn(saved);
        when(matchRepository.findById(1L)).thenReturn(Optional.of(saved));

        ResponseEntity<?> response = matchController.createMatch(incoming);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isInstanceOf(MatchResponse.class);

        MatchResponse body = (MatchResponse) response.getBody();
        assertThat(body.id).isEqualTo(1L);
        assertThat(body.category).isNotNull();
        assertThat(body.date).isEqualTo(date);
        assertThat(body.nextMatch).isNotNull();
        assertThat(body.previousMatches).isNull();
        assertThat(body.participants).isNotNull();

        ArgumentCaptor<Match> captor = ArgumentCaptor.forClass(Match.class);
        verify(matchRepository).save(captor.capture());
        Match toSave = captor.getValue();

        assertThat(toSave.getId()).isEqualTo(1L);
    }

}
