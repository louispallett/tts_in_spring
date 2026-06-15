package com.example.tts_in_spring.tournament;

import com.example.tts_in_spring.category.CategoryTestBuilder;
import com.example.tts_in_spring.player.PlayerTestBuilder;
import com.example.tts_in_spring.user.UserTestBuilder;
import com.example.tts_in_spring.user.UserResponseLite;
import com.example.tts_in_spring.category.Category;
import com.example.tts_in_spring.player.Player;
import com.example.tts_in_spring.user.User;
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

    private Tournament buildTournamentWithHostAndPlayers(User host, User playerUser) {
        Player player = PlayerTestBuilder.aPlayer().withUser(playerUser).build();

        Category category = CategoryTestBuilder.aCategory().build();
        category.setPlayers(List.of(player));

        Tournament tournament = TournamentTestBuilder.aTournament().withHost(host).build();
        tournament.setCategories(List.of(category));
        return tournament;
    }

    private TournamentResponse buildTournamentResponse() {
        return new TournamentResponse(
                10L,
                "Test Tournament",
                "SIGN_UP",
                false,
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
                false,
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
        Tournament tournament = TournamentTestBuilder.aTournament().build();
        TournamentResponse response = buildTournamentResponse();

        when(tournamentRepository.findAll()).thenReturn(List.of(tournament));
        when(tournamentMapper.toResponse(tournament)).thenReturn(response);

        List<TournamentResponse> result = tournamentService.getAllTournaments();

        assertThat(result).containsExactly(response);
    }

    @Test
    void getTournamentById_whenPlayer_returnsMappedResponseWithoutCode() {
        User host = UserTestBuilder.aUser().build();
        User currentUser = UserTestBuilder.aUser().withId(2L).build();
        mockAuthenticatedUser(currentUser);

        Tournament tournament = buildTournamentWithHostAndPlayers(host, currentUser);
        TournamentResponse response = buildTournamentResponse();

        when(tournamentRepository.findById(10L)).thenReturn(Optional.of(tournament));
        when(tournamentMapper.toResponse(tournament)).thenReturn(response);

        TournamentResponse result = tournamentService.getTournamentById(10L);

        assertThat(result)
                .isInstanceOf(TournamentResponse.class)
                .isNotInstanceOf(TournamentResponseHost.class)
                .isEqualTo(response);
    }

    @Test
    void getTournamentById_whenHost_returnsMappedResponseWithCode() {
        User host = UserTestBuilder.aUser().build();
        mockAuthenticatedUser(host);

        Tournament tournament = TournamentTestBuilder.aTournament().withHost(host).build();
        TournamentResponseHost responseHost = buildTournamentResponseHost();

        when(tournamentRepository.findById(10L)).thenReturn(Optional.of(tournament));
        when(tournamentMapper.toResponseHost(tournament)).thenReturn(responseHost);

        TournamentResponseHost result = tournamentService.getTournamentById(10L);

        assertThat(result)
                .isInstanceOf(TournamentResponseHost.class)
                .isEqualTo(responseHost);

        assertThat((result).code()).isEqualTo("Test_secretcode");
    }

    @Test
    void getTournamentById_whenNotFound_throws404() {
        mockAuthenticatedUser(UserTestBuilder.aUser().build());

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
        User host = UserTestBuilder.aUser().build();
        User outsider = UserTestBuilder.aUser().withId(3L).build();
        mockAuthenticatedUser(outsider);

        Tournament tournament = TournamentTestBuilder.aTournament().withHost(host).build();

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
        User currentUser = UserTestBuilder.aUser().build();
        mockAuthenticatedUser(currentUser);

        Tournament saved = TournamentTestBuilder.aTournament().build();
        TournamentResponseHost responseHost = new TournamentResponseHost(
               10L,
               "Test Tournament",
                "SIGN_UP",
                "1234567",
                false,
                new UserResponseLite(1L, "John", "Doe"),
                List.of()
        );

        when(tournamentRepository.save(any(Tournament.class))).thenReturn(saved);
        when(tournamentMapper.toResponseHost(saved)).thenReturn(responseHost);

        assertThat(tournamentService.createTournament(request)).isEqualTo(responseHost);
        verify(tournamentRepository).save(any(Tournament.class));
    }

    @Test
    void updateTournamentName_whenHost_savesAndReturnsMappedLite() {
        User host = UserTestBuilder.aUser().build();
        mockAuthenticatedUser(host);

        Tournament tournament = TournamentTestBuilder.aTournament().withHost(host).build();

        TournamentRequest request = new TournamentRequest();
        request.setName("New Tournament Name");

        Tournament updatedTournament = TournamentTestBuilder.aTournament().withHost(host).build();
        updatedTournament.setName("New Tournament Name");
        TournamentResponseLite lite = new TournamentResponseLite(
                10L,
                "New Tournament Name",
                "SIGN_UP",
                false
        );

        when(tournamentRepository.findById(100L)).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(updatedTournament);
        when(tournamentMapper.toResponseLite(updatedTournament)).thenReturn(lite);

        TournamentResponseLite result = tournamentService.updateName(10L, request);

        assertThat(result).isEqualTo(lite);
        assertThat(result.name()).isEqualTo("New Tournament Name");
        verify(tournamentRepository).save(any(Tournament.class));
    }

    @Test
    void updateTournamentName_whenNotHost_throws403() {
        User user = UserTestBuilder.aUser().withId(2L).build();
        mockAuthenticatedUser(user);

        User host = UserTestBuilder.aUser().build();
        Tournament tournament = TournamentTestBuilder.aTournament().withHost(host).build();

        TournamentNameUpdateRequest request = new TournamentRequest();
        request.setName("New Tournament Name");

        Tournament updatedTournament = TournamentTestBuilder.aTournament().withHost(host).build();
        updatedTournament.setName("New Tournament Name");

        when(tournamentRepository.findById(100L)).thenReturn(Optional.of(tournament));

        assertThatThrownBy(() -> tournamentService.updateName(10L, request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.FORBIDDEN)
                        );
        verify(tournamentRepository, never()).save(any());
        verifyNoInteractions(tournamentMapper);
    }

    @Test
    void updateTournamentStage_whenHost_returnsMappedLite() {
        User host = UserTestBuilder.aUser().build();
        mockAuthenticatedUser(host);

        Tournament tournament = TournamentTestBuilder.aTournament().withHost(host).build();

        TournamentStageUpdateRequest updateRequest = new TournamentStageUpdateRequest();
        updateRequest.setStage("DRAW");

        Tournament updatedTournament = TournamentTestBuilder.aTournament().withHost(host).build();
        updatedTournament.setStage("DRAW");
        TournamentResponseLite lite = new TournamentResponseLite(
                10L,
                "New Tournament Name",
                "DRAW",
                false
        );

        when(tournamentRepository.findById(100L)).thenReturn(Optional.of(tournament));
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(updatedTournament);
        when(tournamentMapper.toResponseLite(updatedTournament)).thenReturn(lite);

        TournamentResponseLite result = tournamentService.updateStage(10L, updateRequest);

        assertThat(result).isEqualTo(lite);
        assertThat(result.stage()).isEqualTo("DRAW");
        verify(tournamentRepository).save(any(Tournament.class));
    }

    @Test
    void updateTournamentStage_whenNotHost_throws403() {
        User user = UserTestBuilder.aUser().withId(3L).build();
        mockAuthenticatedUser(user);

        User host = UserTestBuilder.aUser().build();
        Tournament tournament = TournamentTestBuilder.aTournament().withHost(host).build();

        TournamentStageUpdateRequest updateRequest = new TournamentStageUpdateRequest();
        updateRequest.setStage("DRAW");

        Tournament updatedTournament = TournamentTestBuilder.aTournament().withHost(host).build();
        updatedTournament.setStage("DRAW");

        when(tournamentRepository.findById(100L)).thenReturn(Optional.of(tournament));

        assertThatThrownBy(() -> tournamentService.updateStage(10L, request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.FORBIDDEN)
                );
        verify(tournamentRepository, never()).save(any());
        verifyNoInteractions(tournamentMapper);
    }
}
