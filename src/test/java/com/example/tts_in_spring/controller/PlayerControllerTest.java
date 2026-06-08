package com.example.tts_in_spring.controller;

import com.example.tts_in_spring.dto.PlayerResponse;
import com.example.tts_in_spring.dto.UserResponse;
import com.example.tts_in_spring.model.Category;
import com.example.tts_in_spring.model.Player;
import com.example.tts_in_spring.model.User;
import com.example.tts_in_spring.repository.PlayerRepository;
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
class PlayerControllerTest {
    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerController playerController;

    private Player createPlayer(Long id, Boolean male, User user, int rank) {
        Category category = new Category();

        Player p = new Player();
        p.setId(id);
        p.setMale(male);
        p.setUser(user);
        p.setRank(rank);
        p.setCategory(category);
        p.setSeeded(false);

        return p;
    }

    @Test
    void getAllPlayers_returnsMappedResponses() {
        User user1 = new User();
        User user2 = new User();
        Player p1 = createPlayer(1L, true, user1, 3);
        Player p2 = createPlayer(2L, false, user2, 10);

        when(playerRepository.findAll()).thenReturn(List.of(p1, p2));

        ResponseEntity<List<PlayerResponse>> response = playerController.getAllPlayers();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).id).isEqualTo(1L);
        assertThat(response.getBody().get(0).male).isTrue();
        assertThat(response.getBody().get(0).rank).isEqualTo(3);
        assertThat(response.getBody().get(0).user).isNotNull();
        assertThat(response.getBody().get(1).id).isEqualTo(2L);
        assertThat(response.getBody().get(1).male).isFalse();
        assertThat(response.getBody().get(1).rank).isEqualTo(10);
        assertThat(response.getBody().get(1).user).isNotNull();
    }

    @Test
    void getPlayer_returnsWhenPlayerExists() {
        User user = new User();
        Player p = createPlayer(1L, true, user, 1);
        when(playerRepository.findById(1L)).thenReturn(Optional.of(p));

        ResponseEntity<PlayerResponse> response = playerController.getPlayer(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id).isEqualTo(1L);
        assertThat(response.getBody().male).isTrue();
        assertThat(response.getBody().rank).isEqualTo(1);
        assertThat(response.getBody().user).isNotNull();
    }

    @Test
    void getUser_returnsNotFoundWhenMissing() {
        when(playerRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<PlayerResponse> response = playerController.getPlayer(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void createPlayer_createsPlayer() {
        User user = new User();

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);

        SecurityContextHolder.setContext(securityContext);

        Player incoming = new Player();
        incoming.setMale(true);
        incoming.setRank(1);

        Player saved = createPlayer(1L, true, user, 1);
        when(playerRepository.save(any(Player.class))).thenReturn(saved);
        when(playerRepository.findById(1L)).thenReturn(Optional.of(saved));

        ResponseEntity<?> response = playerController.createPlayer(incoming);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isInstanceOf(PlayerResponse.class);

        PlayerResponse body = (PlayerResponse) response.getBody();
        assertThat(body.id).isEqualTo(1L);
        assertThat(body.male).isTrue();
        assertThat(body.rank).isEqualTo(1);

        ArgumentCaptor<Player> captor = ArgumentCaptor.forClass(Player.class);
        verify(playerRepository).save(captor.capture());
        Player toSave = captor.getValue();

        assertThat(toSave.isMale()).isTrue();
        assertThat(toSave.getRank()).isEqualTo(1);

        SecurityContextHolder.clearContext();
    }
}
