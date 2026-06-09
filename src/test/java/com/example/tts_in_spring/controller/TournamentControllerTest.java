package com.example.tts_in_spring.controller;

import com.example.tts_in_spring.dto.TournamentRequest;
import com.example.tts_in_spring.dto.TournamentResponse;
import com.example.tts_in_spring.model.Tournament;
import com.example.tts_in_spring.model.User;
import com.example.tts_in_spring.repository.TournamentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TournamentControllerTest {
    @Mock
    private TournamentRepository tournamentRepository;

    @InjectMocks
    private TournamentController tournamentController;

    private Tournament createTournament(Long id, String name, User host) {
        Tournament t = new Tournament();
        t.setId(id);
        t.setName(name);
        t.setHost(host);
        t.setShowMobile(true);

        return t;
    }

    @Test
    void getAllUsers_returnsMappedResponses() {
        User t1Host = new User();
        User t2Host = new User();
        Tournament t1 = createTournament(1L, "Test Tournament 1", t1Host);
        Tournament t2 = createTournament(2L, "Test Tournament 2", t2Host);
        t1.setCode("okasdafji");
        t2.setCode("adjiafe33w");
        when(tournamentRepository.findAll()).thenReturn(List.of(t1, t2));

        ResponseEntity<List<TournamentResponse>> response = tournamentController.getAllTournaments();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().getFirst().id).isEqualTo(1);
        assertThat(response.getBody().getFirst().name).isEqualTo("Test Tournament 1");
        assertThat(response.getBody().getFirst().host).isNotNull();
        assertThat(response.getBody().getFirst().code).isNull();
        assertThat(response.getBody().get(1).id).isEqualTo(2);
        assertThat(response.getBody().get(1).name).isEqualTo("Test Tournament 2");
        assertThat(response.getBody().get(1).host).isNotNull();
        assertThat(response.getBody().get(1).code).isNull();
    }

    @Test
    void getTournament_returnsTournamentWhenExists() {
        User host = new User();
        Tournament t = createTournament(1L, "Test Tournament", host);
        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(t));

        ResponseEntity<TournamentResponse> response = tournamentController.getTournament(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id).isEqualTo(1L);
        assertThat(response.getBody().name).isEqualTo("Test Tournament");
    }

    @Test
    void getTournament_returnsNotFoundWhenMissing() {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<TournamentResponse> response = tournamentController.getTournament(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void createTournament_createsTournament() {
        User host = new User();

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(host);
        SecurityContextHolder.setContext(securityContext);

        TournamentRequest incoming = new TournamentRequest();
        incoming.setName("Test Tournament");
        incoming.setStage("SIGN_UP");
        incoming.setShowMobile(true);

        Tournament saved = new Tournament();
        saved.setId(1L);
        when(tournamentRepository.save(any(Tournament.class))).thenAnswer(invocation -> {
            Tournament toSave = invocation.getArgument(0);
            toSave.setId(1L);
            return toSave;
        });
        when(tournamentRepository.findById(1L)).thenAnswer(invocation -> {
            Tournament toReturn = new Tournament();
            toReturn.setId(1L);
            toReturn.setName("Test Tournament");
            toReturn.setStage("SIGN_UP");
            toReturn.setHost(host);
            toReturn.setShowMobile(true);
            toReturn.setCode(incoming.generateCode(incoming.getName()));
            return Optional.of(toReturn);
        });

        ResponseEntity<?> response = tournamentController.createTournament(incoming);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isInstanceOf(TournamentResponse.class);

        TournamentResponse body = (TournamentResponse) response.getBody();
        assertThat(body.id).isEqualTo(1L);
        assertThat(body.name).isEqualTo("Test Tournament");
        assertThat(body.showMobile).isTrue();
        assertThat(body.code).isNotNull();
        assertThat(body.stage).isEqualTo("SIGN_UP");

        ArgumentCaptor<Tournament> captor = ArgumentCaptor.forClass(Tournament.class);
        verify(tournamentRepository).save(captor.capture());
        Tournament toSave = captor.getValue();

        assertThat(toSave.getName()).isEqualTo("Test Tournament");
        assertThat(toSave.isShowMobile()).isTrue();
        assertThat(toSave.getCode()).isNotNull();

        SecurityContextHolder.clearContext();
    }
}
