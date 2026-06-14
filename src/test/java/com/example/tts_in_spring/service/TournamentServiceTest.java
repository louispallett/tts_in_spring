package com.example.tts_in_spring.service;

import com.example.tts_in_spring.dto.tournament.TournamentRequest;
import com.example.tts_in_spring.dto.tournament.TournamentResponse;
import com.example.tts_in_spring.dto.tournament.TournamentResponseHost;
import com.example.tts_in_spring.dto.tournament.TournamentResponseLite;
import com.example.tts_in_spring.dto.user.UserResponseLite;
import com.example.tts_in_spring.mapper.TournamentMapper;
import com.example.tts_in_spring.model.Category;
import com.example.tts_in_spring.model.Player;
import com.example.tts_in_spring.model.Tournament;
import com.example.tts_in_spring.model.User;
import com.example.tts_in_spring.repository.TournamentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TournamentServiceTest {
    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private TournamentMapper tournamentMapper;

    @InjectMocks
    private TournamentService tournamentService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void mockAuthenticatedUser(User user) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    private User buildUser(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private Tournament buildTournamentWithHost(User host) {
        Tournament tournament = new Tournament();
        tournament.setHost(host);
        return tournament;
    }

    private Tournament buildTournamentWithHostAndPlayer(User host, User playerUser) {
        Player player = new Player();
        player.setUser(playerUser);

        Category category = new Category();
        category.setPlayers(List.of(player));

        Tournament tournament = new Tournament();
        tournament.setHost(host);
        tournament.setCategories(List.of(category));
        return tournament;
    }

    private TournamentResponse buildTournamentResponse() {
        return new TournamentResponse(
                10L,
                "Test Tournament",
                "SIGN_UP",
                true,
                new UserResponseLite(1L, "John", "Doe"),
                null
        );
    }

    private TournamentResponseHost buildTournamentResponseHost() {
        return new TournamentResponseHost(
                10L,
                "Test Tournament",
                "SIGN_UP",
                "Test_secretcode",
                true,
                new UserResponseLite(1L, "John", "Doe"),
                null
        );
    }

    private TournamentRequest buildTournamentRequest() {
        TournamentRequest r = new TournamentRequest();
        r.setName("Test Tournament");
        r.setStage("SIGN_UP");
        r.setShowMobile(true);

        return r;
    }

    @Test
    void getAllTournaments_returnsMappedList() {
        Tournament tournament = new Tournament();
        TournamentResponse response = buildTournamentResponse();

        when(tournamentRepository.findAll()).thenReturn(List.of(tournament));
        when(tournamentMapper.toResponse(tournament)).thenReturn(response);

        List<TournamentResponse> result = tournamentService.getAllTournaments();

        assertThat(result).containsExactly(response);
    }

    @Test
    void getTournamentById_whenPlayer_returnsMappedResponseWithoutCode() {
        User host = buildUser(1L);
        User currentUser = buildUser(2L);
        mockAuthenticatedUser(currentUser);

        Tournament tournament = buildTournamentWithHostAndPlayer(host, currentUser);
        TournamentResponse response = buildTournamentResponse();

        when(tournamentRepository.findById(10L)).thenReturn(Optional.of(tournament));
        when(tournamentMapper.toResponse(tournament)).thenReturn(response);

        Object result = tournamentService.getTournamentById(10L);

        assertThat(result)
                .isInstanceOf(TournamentResponse.class)
                .isNotInstanceOf(TournamentResponseHost.class)
                .isEqualTo(response);
    }

    @Test
    void getTournamentById_whenHost_returnsMappedResponseWithCode() {
        User host = buildUser(1L);
        mockAuthenticatedUser(host);

        Tournament tournament = buildTournamentWithHost(host);
        TournamentResponseHost responseHost = buildTournamentResponseHost();

        when(tournamentRepository.findById(10L)).thenReturn(Optional.of(tournament));
        when(tournamentMapper.toResponseHost(tournament)).thenReturn(responseHost);

        Object result = tournamentService.getTournamentById(10L);

        assertThat(result)
                .isInstanceOf(TournamentResponseHost.class)
                .isEqualTo(responseHost);

        assertThat(((TournamentResponseHost) result).code()).isEqualTo("Test_secretcode");
    }

    @Test
    void getTournamentById_whenNotFound_throws404() {
        mockAuthenticatedUser(buildUser(1L));

        when(tournamentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tournamentService.getTournamentById(99L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.NOT_FOUND)
                );
    }

    @Test
    void getTournamentById_whenNotAuthorised_throws403() {
        User host = buildUser(1L);
        User outsider = buildUser(3L);
        mockAuthenticatedUser(outsider);

        Tournament tournament = buildTournamentWithHost(host);

        when(tournamentRepository.findById(10L)).thenReturn(Optional.of(tournament));

        assertThatThrownBy(() -> tournamentService.getTournamentById(10L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.FORBIDDEN)
                );
    }

    @Test
    void createTournament_savesAndReturnsMappedLite() {
        TournamentRequest request = buildTournamentRequest();
        User currentUser = buildUser(1L);
        mockAuthenticatedUser(currentUser);

        Tournament saved = new Tournament();
        TournamentResponseLite lite = new TournamentResponseLite(
               1L,
               "Test Tournament",
                true
        );

        when(tournamentRepository.save(any(Tournament.class))).thenReturn(saved);
        when(tournamentMapper.toResponseLite(saved)).thenReturn(lite);

        assertThat(tournamentService.createTournament(request)).isEqualTo(lite);
        verify(tournamentRepository).save(any(Tournament.class));
    }
}
