package com.example.tts_in_spring.controller;

import com.example.tts_in_spring.dto.ParticipantResponse;
import com.example.tts_in_spring.model.*;
import com.example.tts_in_spring.repository.ParticipantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParticipantControllerTest {
    @Mock
    private ParticipantRepository participantRepository;

    @InjectMocks
    private ParticipantController participantController;

    // Overloaded functions to handle participant with team or player
    private Participant createParticipant(Long id, Player player) {
        Participant p = new Participant();
        p.setId(id);
        p.setResultText("");
        p.setWinner(false);
        p.setStatus("");
        p.setPlayer(player);

        return p;
    }

    private Participant createParticipant(Long id, Team team) {
        Participant p = new Participant();
        p.setId(id);
        p.setResultText("");
        p.setWinner(false);
        p.setStatus("");
        p.setTeam(team);

        return p;
    }

    @Test
    void getAllParticipants_returnsMappedResponses() {
        Team team = new Team();
        Player player = new Player();

        // Two participants of the same team
        Participant pTeam1 = createParticipant(1L, team);
        Participant pTeam2 = createParticipant(2L, team);
        // One participant from player
        Participant pPlayer = createParticipant(3L, player);

        when(participantRepository.findAll()).thenReturn(List.of(pTeam1, pTeam2, pPlayer));

        ResponseEntity<List<ParticipantResponse>> response = participantController.getAllParticipants();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(3);
        assertThat(response.getBody().getFirst().id).isEqualTo(1L);
        assertThat(response.getBody().getFirst().isWinner).isFalse();
        assertThat(response.getBody().getFirst().team).isNotNull();
        assertThat(response.getBody().getFirst().player).isNull();
        assertThat(response.getBody().getFirst().match).isNotNull();
        assertThat(response.getBody().get(1).id).isEqualTo(2L);
        assertThat(response.getBody().get(1).status).isEqualTo("");
        assertThat(response.getBody().get(1).team).isNotNull();
        assertThat(response.getBody().get(1).player).isNull();
        assertThat(response.getBody().get(1).match).isNotNull();
        assertThat(response.getBody().get(2).id).isEqualTo(2L);
        assertThat(response.getBody().get(2).resultText).isEqualTo("");
        assertThat(response.getBody().get(2).team).isNull();
        assertThat(response.getBody().get(2).player).isNotNull();
        assertThat(response.getBody().get(2).match).isNotNull();
    }

    @Test
    void getParticipant_returnsWhenParticipantPlayerExists() {
        Player player = new Player();
        Participant p = createParticipant(1L, player);
        when(participantRepository.findById(1L)).thenReturn(Optional.of(p));

        ResponseEntity<ParticipantResponse> response = participantController.getParticipant(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id).isEqualTo(1L);
        assertThat(response.getBody().resultText).isEqualTo("");
        assertThat(response.getBody().isWinner).isFalse();
        assertThat(response.getBody().status).isEqualTo("");
        assertThat(response.getBody().team).isNull();
        assertThat(response.getBody().player).isNotNull();
        assertThat(response.getBody().match).isNotNull();
    }

    @Test
    void getParticipant_returnsWhenParticipantTeamExists() {
        Team team = new Team();
        Participant p = createParticipant(1L, team);
        when(participantRepository.findById(1L)).thenReturn(Optional.of(p));

        ResponseEntity<ParticipantResponse> response = participantController.getParticipant(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id).isEqualTo(1L);
        assertThat(response.getBody().resultText).isEqualTo("");
        assertThat(response.getBody().isWinner).isFalse();
        assertThat(response.getBody().status).isEqualTo("");
        assertThat(response.getBody().team).isNotNull();
        assertThat(response.getBody().player).isNull();
        assertThat(response.getBody().match).isNotNull();
    }

    @Test
    void createParticipant_createsPlayerParticipant() {
        Participant incoming = new Participant();
        Player player = new Player();

        Participant saved = createParticipant(1L, player);
        when(participantRepository.save(any(Participant.class))).thenReturn(saved);
        when(participantRepository.findById(1L)).thenReturn(Optional.of(saved));

        ResponseEntity<?> response = participantController.createParticipant(incoming);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isInstanceOf(ParticipantResponse.class);

        ParticipantResponse body = (ParticipantResponse) response.getBody();
        assertThat(body.id).isEqualTo(1L);
        assertThat(body.player).isNotNull();
        assertThat(body.team).isNull();

        ArgumentCaptor<Participant> captor = ArgumentCaptor.forClass(Participant.class);
        verify(participantRepository).save(captor.capture());
        Participant toSave = captor.getValue();

        assertThat(toSave.getId()).isEqualTo(1L);
    }

    @Test
    void createParticipant_createsTeamParticipant() {
        Participant incoming = new Participant();
        Team team = new Team();

        Participant saved = createParticipant(1L, team);
        when(participantRepository.save(any(Participant.class))).thenReturn(saved);
        when(participantRepository.findById(1L)).thenReturn(Optional.of(saved));

        ResponseEntity<?> response = participantController.createParticipant(incoming);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isInstanceOf(ParticipantResponse.class);

        ParticipantResponse body = (ParticipantResponse) response.getBody();
        assertThat(body.id).isEqualTo(1L);
        assertThat(body.team).isNotNull();
        assertThat(body.player).isNull();

        ArgumentCaptor<Participant> captor = ArgumentCaptor.forClass(Participant.class);
        verify(participantRepository).save(captor.capture());
        Participant toSave = captor.getValue();

        assertThat(toSave.getId()).isEqualTo(1L);
    }
}
