package com.example.tts_in_spring.tournament;

import com.example.tts_in_spring.category.*;
import com.example.tts_in_spring.category.dto.CategoryRequest;
import com.example.tts_in_spring.player.PlayerTestBuilder;
import com.example.tts_in_spring.tournament.dto.*;
import com.example.tts_in_spring.user.*;
import com.example.tts_in_spring.player.Player;
import com.example.tts_in_spring.user.dto.UserResponseLite;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
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

    @Mock
    private UserService userService;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private TournamentService tournamentService;

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
                "1234567",
                false,
                new UserResponseLite(1L, "John", "Doe"),
                null
        );
    }

    private TournamentRequest buildTournamentRequest() {
        return new TournamentRequest(
                "Test Tournament",
            true,
            true, false, false, false, false
        );
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
    void getAllTournaments_whenEmpty_returnsEmptyList() {
        when(tournamentRepository.findAll()).thenReturn(List.of());

        assertThat(tournamentService.getAllTournaments()).isEmpty();
    }

    @Test
    void getTournamentById_whenPlayer_returnsMappedResponse() {
        User host = UserTestBuilder.aUser().build();
        User currentUser = UserTestBuilder.aUser().withId(2L).build();

        Tournament tournament = buildTournamentWithHostAndPlayers(host, currentUser);
        TournamentResponse response = buildTournamentResponse();

        when(tournamentRepository.findById(tournament.getId())).thenReturn(Optional.of(tournament));
        when(tournamentMapper.toResponse(tournament)).thenReturn(response);

        assertThat(tournamentService.getTournamentById(tournament.getId(), currentUser.getId())).isEqualTo(response);
    }

    @Test
    void getTournamentById_whenHost_returnsMappedResponse() {
        User host = UserTestBuilder.aUser().build();

        Tournament tournament = TournamentTestBuilder.aTournament().withHost(host).build();
        TournamentResponse response = buildTournamentResponse();

        when(tournamentRepository.findById(tournament.getId())).thenReturn(Optional.of(tournament));
        when(tournamentMapper.toResponse(tournament)).thenReturn(response);

        assertThat(tournamentService.getTournamentById(tournament.getId(), host.getId())).isEqualTo(response);
    }

    @Test
    void getTournamentById_whenNotFound_throws404() {
        User user = UserTestBuilder.aUser().build();

        when(tournamentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tournamentService.getTournamentById(99L, user.getId()))
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

        Tournament tournament = TournamentTestBuilder.aTournament().withHost(host).build();

        when(tournamentRepository.findById(tournament.getId())).thenReturn(Optional.of(tournament));

        assertThatThrownBy(() -> tournamentService.getTournamentById(10L, outsider.getId()))
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

        Tournament saved = TournamentTestBuilder.aTournament().build();
        TournamentResponseLite lite = new TournamentResponseLite(
               10L,
               "Test Tournament",
                "SIGN_UP",
                false
        );

        when(userService.getUserOrThrow(currentUser.getId())).thenReturn(currentUser);
        when(tournamentMapper.toEntity(request)).thenReturn(saved);
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(saved);
        when(tournamentMapper.toResponseLite(saved)).thenReturn(lite);

        assertThat(tournamentService.createTournament(request, currentUser.getId())).isEqualTo(lite);
        verify(categoryService).createCategory(any(CategoryRequest.class), eq(currentUser.getId()));
    }

     @Test
     void updateTournamentName_whenHost_savesAndReturnsMappedLite() {
         User host = UserTestBuilder.aUser().build();

         Tournament tournament = TournamentTestBuilder.aTournament().withHost(host).build();

         TournamentNameUpdateRequest request = new TournamentNameUpdateRequest("New Tournament Name");

         Tournament updatedTournament = TournamentTestBuilder.aTournament().withHost(host).build();
         updatedTournament.setName("New Tournament Name");
         TournamentResponseLite lite = new TournamentResponseLite(
                 10L,
                 "New Tournament Name",
                 "SIGN_UP",
                 false
         );

         when(tournamentRepository.findById(tournament.getId())).thenReturn(Optional.of(tournament));
         when(tournamentRepository.save(any(Tournament.class))).thenReturn(updatedTournament);
         when(tournamentMapper.toResponseLite(updatedTournament)).thenReturn(lite);

         TournamentResponseLite result = tournamentService.updateName(tournament.getId(), request, host.getId());
         assertThat(result).isEqualTo(lite);

         verify(tournamentMapper).updateNameEntity(request, tournament);
         verify(tournamentRepository).save(tournament);
         verify(tournamentMapper).toResponseLite(updatedTournament);
     }

     @Test
     void updateTournamentName_whenNotHost_throws403() {
         User user = UserTestBuilder.aUser().withId(2L).build();

         User host = UserTestBuilder.aUser().build();
         Tournament tournament = TournamentTestBuilder.aTournament().withHost(host).build();

         TournamentNameUpdateRequest request = new TournamentNameUpdateRequest("New Tournament Name");

         when(tournamentRepository.findById(tournament.getId())).thenReturn(Optional.of(tournament));

         assertThatThrownBy(() -> tournamentService.updateName(tournament.getId(), request, user.getId()))
                 .isInstanceOf(ResponseStatusException.class)
                 .satisfies(ex ->
                         assertThat(((ResponseStatusException) ex).getStatusCode())
                                 .isEqualTo(HttpStatus.FORBIDDEN)
                         );
         verify(tournamentRepository, never()).save(any());
         verifyNoInteractions(tournamentMapper);
     }

     @Test
     void updateTournamentName_whenNotFound_throws404() {
         TournamentNameUpdateRequest request = new TournamentNameUpdateRequest("New Tournament Name");

         when(tournamentRepository.findById(99L)).thenReturn(Optional.empty());

         assertThatThrownBy(() -> tournamentService.updateName(99L, request, 1L))
                 .isInstanceOf(ResponseStatusException.class)
                 .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                         .isEqualTo(HttpStatus.NOT_FOUND));

         verify(tournamentRepository, never()).save(any());
     }

     @Test
     void updateTournamentStage_whenHost_returnsMappedLite() {
         User host = UserTestBuilder.aUser().build();

         Tournament tournament = TournamentTestBuilder.aTournament().withHost(host).build();

         TournamentStageUpdateRequest request = new TournamentStageUpdateRequest("DRAW");

         Tournament updatedTournament = TournamentTestBuilder.aTournament().withHost(host).build();
         updatedTournament.setStage("DRAW");
         TournamentResponseLite lite = new TournamentResponseLite(
                 10L,
                 "Test Tournament",
                 "DRAW",
                 false
         );

         when(tournamentRepository.findById(tournament.getId())).thenReturn(Optional.of(tournament));
         when(tournamentRepository.save(any(Tournament.class))).thenReturn(updatedTournament);
         when(tournamentMapper.toResponseLite(updatedTournament)).thenReturn(lite);

         TournamentResponseLite result = tournamentService.updateStage(tournament.getId(), request, host.getId());
         assertThat(result).isEqualTo(lite);

         verify(tournamentMapper).updateStageEntity(request, tournament);
         verify(tournamentRepository).save(tournament);
         verify(tournamentMapper).toResponseLite(updatedTournament);
     }

     @Test
     void updateTournamentStage_whenNotHost_throws403() {
         User user = UserTestBuilder.aUser().withId(3L).build();

         User host = UserTestBuilder.aUser().build();
         Tournament tournament = TournamentTestBuilder.aTournament().withHost(host).build();

         TournamentStageUpdateRequest request = new TournamentStageUpdateRequest("DRAW");

         when(tournamentRepository.findById(tournament.getId())).thenReturn(Optional.of(tournament));

         assertThatThrownBy(() -> tournamentService.updateStage(tournament.getId(), request, user.getId()))
                 .isInstanceOf(ResponseStatusException.class)
                 .satisfies(ex ->
                         assertThat(((ResponseStatusException) ex).getStatusCode())
                                 .isEqualTo(HttpStatus.FORBIDDEN)
                 );
         verify(tournamentRepository, never()).save(any());
         verifyNoInteractions(tournamentMapper);
     }

     @Test
     void updateTournamentStage_whenNotFound_throws404() {
         TournamentStageUpdateRequest request = new TournamentStageUpdateRequest("DRAW");

         when(tournamentRepository.findById(99L)).thenReturn(Optional.empty());

         assertThatThrownBy(() -> tournamentService.updateStage(99L, request, 1L))
                 .isInstanceOf(ResponseStatusException.class)
                 .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                         .isEqualTo(HttpStatus.NOT_FOUND));

         verify(tournamentRepository, never()).save(any());
     }

     @Test
     void updateTournamentShowMobile_whenHost_returnsMappedLite() {
         User host = UserTestBuilder.aUser().build();

         Tournament tournament = TournamentTestBuilder.aTournament().withHost(host).build();

         TournamentShowMobileUpdateRequest request = new TournamentShowMobileUpdateRequest(true);

         Tournament updatedTournament = TournamentTestBuilder.aTournament().withHost(host).build();
         updatedTournament.setShowMobile(true);
         TournamentResponseLite lite = new TournamentResponseLite(
                 10L,
                 "Test Tournament",
                 "SIGN_UP",
                 true
         );

         when(tournamentRepository.findById(tournament.getId())).thenReturn(Optional.of(tournament));
         when(tournamentRepository.save(any(Tournament.class))).thenReturn(updatedTournament);
         when(tournamentMapper.toResponseLite(updatedTournament)).thenReturn(lite);

         TournamentResponseLite result = tournamentService.updateShowMobile(tournament.getId(), request, host.getId());
         assertThat(result).isEqualTo(lite);

         verify(tournamentMapper).updateShowMobileEntity(request, tournament);
         verify(tournamentRepository).save(tournament);
         verify(tournamentMapper).toResponseLite(updatedTournament);
     }

     @Test
     void updateTournamentShowMobile_whenNotHost_throws403() {
         User user = UserTestBuilder.aUser().withId(3L).build();

         User host = UserTestBuilder.aUser().build();
         Tournament tournament = TournamentTestBuilder.aTournament().withHost(host).build();

         TournamentShowMobileUpdateRequest request = new TournamentShowMobileUpdateRequest(true);

         when(tournamentRepository.findById(tournament.getId())).thenReturn(Optional.of(tournament));

         assertThatThrownBy(() -> tournamentService.updateShowMobile(tournament.getId(), request, user.getId()))
                 .isInstanceOf(ResponseStatusException.class)
                 .satisfies(ex ->
                         assertThat(((ResponseStatusException) ex).getStatusCode())
                                 .isEqualTo(HttpStatus.FORBIDDEN)
                 );
         verify(tournamentRepository, never()).save(any());
         verifyNoInteractions(tournamentMapper);
     }

     @Test
     void updateTournamentShowMobile_whenNotFound_throws404() {
         TournamentShowMobileUpdateRequest request = new TournamentShowMobileUpdateRequest(true);

         when(tournamentRepository.findById(99L)).thenReturn(Optional.empty());

         assertThatThrownBy(() -> tournamentService.updateShowMobile(99L, request, 1L))
                 .isInstanceOf(ResponseStatusException.class)
                 .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                         .isEqualTo(HttpStatus.NOT_FOUND));

         verify(tournamentRepository, never()).save(any());
     }

    @Test
    void checkCode_whenCorrect_returnsTrue() {
        Tournament tournament = TournamentTestBuilder.aTournament().build();

        when(tournamentRepository.findByCode(tournament.getCode())).thenReturn(Optional.of(tournament));

        assertThat(tournamentService.checkCode(tournament.getCode())).isEqualTo(tournament);
    }

    @Test
    void checkCode_whenNotFound_throws404() {
        String fakeCode = "abcdgefg";
        when(tournamentRepository.findByCode(fakeCode)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tournamentService.checkCode(fakeCode))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.NOT_FOUND));
    }
}