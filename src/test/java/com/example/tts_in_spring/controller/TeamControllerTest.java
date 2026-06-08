package com.example.tts_in_spring.controller;

import com.example.tts_in_spring.dto.TeamResponse;
import com.example.tts_in_spring.model.Category;
import com.example.tts_in_spring.model.Team;
import com.example.tts_in_spring.repository.TeamRepository;
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
public class TeamControllerTest {
    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TeamController teamController;

    private Team createTeam(Long id) {
        Category category = new Category();

        Team t = new Team();
        t.setId(id);
        t.setCategory(category);

        return t;
    }

    @Test
    void getAllTeams_returnsMappedResponses() {
        Team t1 = createTeam(1L);
        Team t2 = createTeam(2L);

        when(teamRepository.findAll()).thenReturn(List.of(t1, t2));

        ResponseEntity<List<TeamResponse>> response = teamController.getAllTeams();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().getFirst().id).isEqualTo(1L);
        assertThat(response.getBody().getFirst().players).isNotNull();
        assertThat(response.getBody().getFirst().participants).isNotNull();
        assertThat(response.getBody().get(1).id).isEqualTo(2L);
        assertThat(response.getBody().get(1).players).isNotNull();
        assertThat(response.getBody().get(1).participants).isNotNull();
    }

    @Test
    void getTeam_returnsTeamWhenTeamExists() {
        Team t = createTeam(1L);
        when(teamRepository.findById(1L)).thenReturn(Optional.of(t));

        ResponseEntity<TeamResponse> response = teamController.getTeam(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().id).isEqualTo(1L);
        assertThat(response.getBody().players).isNotNull();
        assertThat(response.getBody().participants).isNotNull();
    }

    @Test
    void createTeam_createsTeam() {
        Team incoming = new Team();

        Team saved = createTeam(1L);
        when(teamRepository.save(any(Team.class))).thenReturn(saved);
        when(teamRepository.findById(1L)).thenReturn(Optional.of(saved));

        ResponseEntity<?> response = teamController.createTeam(incoming);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isInstanceOf(TeamResponse.class);

        TeamResponse body = (TeamResponse) response.getBody();
        assertThat(body.id).isEqualTo(1L);

        ArgumentCaptor<Team> captor = ArgumentCaptor.forClass(Team.class);
        verify(teamRepository).save(captor.capture());
        Team toSave = captor.getValue();

        assertThat(toSave.getId()).isEqualTo(1L);
    }
}
