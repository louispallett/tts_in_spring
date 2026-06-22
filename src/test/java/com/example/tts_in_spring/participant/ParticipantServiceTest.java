package com.example.tts_in_spring.participant;

import com.example.tts_in_spring.match.MatchResponseLite;
import com.example.tts_in_spring.match.MatchService;
import com.example.tts_in_spring.match.MatchTestBuilder;
import com.example.tts_in_spring.player.*;
import com.example.tts_in_spring.team.Team;
import com.example.tts_in_spring.team.TeamResponseLite;
import com.example.tts_in_spring.team.TeamService;
import com.example.tts_in_spring.team.TeamTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ParticipantServiceTest {
    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private ParticipantMapper participantMapper;

    @Mock
    private MatchService matchService;

    @Mock
    private PlayerService playerService;

    @Mock
    private TeamService teamService;

    @InjectMocks
    private ParticipantService participantService;

    private ParticipantResponse buildParticipantResponse(PlayerResponseLite player, TeamResponseLite team) {
        return new ParticipantResponse(
                1000000L,
                "",
                false,
                null,
                team,
                player,
                new MatchResponseLite(100000L, "1", "SCHEDULED", Instant.now(), false)
        );
    }

    private ParticipantResponseLite buildParticipantResponseLite(String resultText, boolean isWinner, String name) {
        return new ParticipantResponseLite(
                1000000L,
                resultText,
                isWinner,
                null,
                name
        );
    }

    private static <T, R> R safeId(T obj, Function<T, R> mapper) {
        return obj == null ? null : mapper.apply(obj);
    }

    private ParticipantRequest buildParticipantRequest(Team team, Player player) {
        ParticipantRequest r = new ParticipantRequest();
        r.setTeamId(safeId(team, Team::getId));
        r.setPlayerId(safeId(player, Player::getId));
        r.setMatchId(MatchTestBuilder.aMatch().build().getId());

        return r;
    }

    @Test
    void getAllParticipants_returnsMappedResponse() {
        Participant participant = ParticipantTestBuilder.aParticipant().build();
        ParticipantResponse response = buildParticipantResponse(
                new PlayerResponseLite(1000L, true, false, 0),
                null
        );

        when(participantRepository.findAll()).thenReturn(List.of(participant));
        when(participantMapper.toResponse(participant)).thenReturn(response);

        List<ParticipantResponse> result = participantService.getAllParticipants();

        assertThat(result).containsExactly(response);
    }

    @Test
    void getAllParticipants_whenEmpty_returnsEmptyList() {
        when(participantRepository.findAll()).thenReturn(List.of());

        assertThat(participantService.getAllParticipants()).isEmpty();
    }

    @Test
    void getParticipantById_whenHost_returnsMappedResponse() {
        Player player = PlayerTestBuilder.aPlayer().build();
        Participant participant = ParticipantTestBuilder.aParticipant().withPlayer(player).build();
        ParticipantResponse response = buildParticipantResponse(
                new PlayerResponseLite(player.getId(), player.isMale(), player.isSeeded(), player.getRank()),
                null
        );

        when(participantRepository.findById(participant.getId())).thenReturn(Optional.of(participant));
        when(participantMapper.toResponse(participant)).thenReturn(response);

        assertThat(participantService.getParticipantById(
                participant.getId(),
                participant.getPlayer().getCategory().getTournament().getHost().getId()
        )).isEqualTo(response);
    }

    @Test
    void getParticipantById_whenParticipant_returnsMappedResponse() {
        Player player = PlayerTestBuilder.aPlayer().build();
        Participant participant = ParticipantTestBuilder.aParticipant().withPlayer(player).build();
        ParticipantResponse response = buildParticipantResponse(
                new PlayerResponseLite(player.getId(), player.isMale(), player.isSeeded(), player.getRank()),
                null
        );

        when(participantRepository.findById(participant.getId())).thenReturn(Optional.of(participant));
        when(participantMapper.toResponse(participant)).thenReturn(response);

        assertThat(participantService.getParticipantById(
                participant.getId(),
                participant.getPlayer().getUser().getId()
        )).isEqualTo(response);
    }

    @Test
    void getParticipantById_whenNotAuthorized_returns403() {
        Player player = PlayerTestBuilder.aPlayer().build();
        Participant participant = ParticipantTestBuilder.aParticipant().withPlayer(player).build();

        when(participantRepository.findById(participant.getId())).thenReturn(Optional.of(participant));

        assertThatThrownBy(() -> participantService.getParticipantById(participant.getId(), 3L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.FORBIDDEN)
                );
    }

    @Test
    void getParticipantById_whenEmpty_returns404() {
        when(participantRepository.findById(9999999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> participantService.getParticipantById(9999999L, 3L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.NOT_FOUND)
                );
    }

    @Test
    void createParticipantWithPlayer_whenHost_savesAndReturnsMappedLite() {
        Player player = PlayerTestBuilder.aPlayer().build();
        ParticipantRequest request = buildParticipantRequest(null, player);

        Participant saved = ParticipantTestBuilder.aParticipant().withPlayer(player).build();
        ParticipantResponseLite lite = buildParticipantResponseLite(
                "",
                false,
                player.getUser().getFirstName() + " " + player.getUser().getLastName()
        );

        when(matchService.getMatchOrThrow(request.getMatchId())).thenReturn(saved.getMatch());
        when(playerService.getPlayerOrThrow(request.getPlayerId())).thenReturn(saved.getPlayer());
        when(participantMapper.toEntity(request)).thenReturn(saved);
        when(participantRepository.save(any(Participant.class))).thenReturn(saved);
        when(participantMapper.toResponseLite(saved)).thenReturn(lite);

        assertThat(participantService.createParticipant(request)).isEqualTo(lite);
    }

    @Test
    void createParticipantWithTeam_whenHost_savesAndReturnsMappedLite() {
        Team team = TeamTestBuilder.aTeam().build();
        ParticipantRequest request = buildParticipantRequest(team, null);

        Participant saved = ParticipantTestBuilder.aParticipant().withTeam(team).build();
        ParticipantResponseLite lite = buildParticipantResponseLite(
                "",
                false,
                "John Doe and Simon Smith"
        );

        when(matchService.getMatchOrThrow(request.getMatchId())).thenReturn(saved.getMatch());
        when(teamService.getTeamOrThrow(request.getTeamId())).thenReturn(saved.getTeam());
        when(participantMapper.toEntity(request)).thenReturn(saved);
        when(participantRepository.save(any(Participant.class))).thenReturn(saved);
        when(participantMapper.toResponseLite(saved)).thenReturn(lite);

        assertThat(participantService.createParticipant(request)).isEqualTo(lite);
    }

    // NOTE: Submit score is a request which is only called by MatchService.submitScore. Therefore, MatchService handles
    // authorization, hence no need to handle this here. Submit score is only ever called during a score submission by the
    // host or one of the participants.
    @Test
    void submitScore_savesAndReturnsMappedLite() {
        Player player = PlayerTestBuilder.aPlayer().build();
        Participant participant = ParticipantTestBuilder.aParticipant().withPlayer(player).build();
        Participant updatedParticipant = ParticipantTestBuilder.aParticipant().withPlayer(player).build();
        ParticipantSubmitScoreRequest request = new ParticipantSubmitScoreRequest();
        request.setId(updatedParticipant.getId());
        request.setWinner(true);
        request.setResultText("6-6");

        ParticipantResponseLite lite = buildParticipantResponseLite(
                "6-6",
                true,
                updatedParticipant.getPlayer().getUser().getFirstName() + " " + updatedParticipant.getPlayer().getUser().getLastName()
        );

        when(participantRepository.findById(request.getId())).thenReturn(Optional.of(participant));
        when(participantRepository.save(any(Participant.class))).thenReturn(updatedParticipant);
        when(participantMapper.toResponseLite(updatedParticipant)).thenReturn(lite);

        ParticipantResponseLite result = participantService.submitScore(updatedParticipant.getId(), request);
        assertThat(result).isEqualTo(lite);

        verify(participantMapper).submitScore(request, participant);
        verify(participantRepository).save(participant);
        verify(participantMapper).toResponseLite(updatedParticipant);
    }

    @Test
    void submitScore_whenNotFound_throws404() {
        ParticipantSubmitScoreRequest request = new ParticipantSubmitScoreRequest();
        request.setId(9999999L);
        request.setWinner(true);
        request.setResultText("6-6");

        when(participantRepository.findById(9999999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> participantService.submitScore(9999999L, request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.NOT_FOUND)
                );
        verify(participantRepository, never()).save(any());
        verifyNoInteractions(participantMapper);
    }

    // NOTE: Alternatively, the methods below are called by ParticipantController, and only the host is authorized to call
    // them. Therefore, these DO need to check and authorize.
    @Test
    void updateResultText_whenHost_savesAndReturnsMappedLite() {
        Player player = PlayerTestBuilder.aPlayer().build();
        Participant participant = ParticipantTestBuilder.aParticipant().withPlayer(player).build();
        participant.setWinner(true);
        participant.setStatus("PLAYED");
        participant.setResultText("2-6");

        ParticipantUpdateResultTextRequest request = new ParticipantUpdateResultTextRequest();
        request.setResultText("6-2");

        Participant updatedParticipant = ParticipantTestBuilder.aParticipant().withPlayer(player).build();
        updatedParticipant.setWinner(true);
        updatedParticipant.setStatus("PLAYED");
        updatedParticipant.setResultText("6-2");
        ParticipantResponseLite lite = new ParticipantResponseLite(
                1000000L,
                "6-2",
                true,
                "PLAYED",
                participant.getPlayer().getUser().getFirstName() + " " + participant.getPlayer().getUser().getLastName()
        );

        when(participantRepository.findById(participant.getId())).thenReturn(Optional.of(participant));
        when(participantRepository.save(any(Participant.class))).thenReturn(updatedParticipant);
        when(participantMapper.toResponseLite(updatedParticipant)).thenReturn(lite);

        ParticipantResponseLite result = participantService.updateResultText(
                participant.getId(),
                request,
                participant.getMatch().getCategory().getTournament().getHost().getId()
        );

        assertThat(result).isEqualTo(lite);

        verify(participantMapper).updateResultText(request, participant);
        verify(participantRepository).save(participant);
        verify(participantMapper).toResponseLite(updatedParticipant);
    }

    @Test
    void updateResultText_whenNotHost_returns403() {
        Player player = PlayerTestBuilder.aPlayer().build();
        Participant participant = ParticipantTestBuilder.aParticipant().withPlayer(player).build();
        participant.setWinner(true);
        participant.setStatus("PLAYED");
        participant.setResultText("2-6");

        ParticipantUpdateResultTextRequest request = new ParticipantUpdateResultTextRequest();
        request.setResultText("6-2");

        when(participantRepository.findById(participant.getId())).thenReturn(Optional.of(participant));

        assertThatThrownBy(() -> participantService.updateResultText(participant.getId(), request, 3L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.FORBIDDEN)
                );
    }

    @Test
    void updateResultText_whenNotFound_returns404() {
        ParticipantUpdateResultTextRequest request = new ParticipantUpdateResultTextRequest();
        request.setResultText("6-2");
        when(participantRepository.findById(9999999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> participantService.updateResultText(9999999L, request, 1L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.NOT_FOUND)
                );
    }

    @Test
    void updateIsWinner_savesAndReturnsMappedLite() {
        Player player = PlayerTestBuilder.aPlayer().build();
        Participant participant = ParticipantTestBuilder.aParticipant().withPlayer(player).build();
        participant.setWinner(false);
        participant.setStatus("PLAYED");
        participant.setResultText("6-2");

        ParticipantUpdateWinnerRequest request = new ParticipantUpdateWinnerRequest();
        request.setWinner(true);

        Participant updatedParticipant = ParticipantTestBuilder.aParticipant().withPlayer(player).build();
        updatedParticipant.setWinner(true);
        updatedParticipant.setStatus("PLAYED");
        updatedParticipant.setResultText("6-2");
        ParticipantResponseLite lite = new ParticipantResponseLite(
                1000000L,
                "6-2",
                true,
                "PLAYED",
                participant.getPlayer().getUser().getFirstName() + " " + participant.getPlayer().getUser().getLastName()
        );

        when(participantRepository.findById(participant.getId())).thenReturn(Optional.of(participant));
        when(participantRepository.save(any(Participant.class))).thenReturn(updatedParticipant);
        when(participantMapper.toResponseLite(updatedParticipant)).thenReturn(lite);

        ParticipantResponseLite result = participantService.updateIsWinner(
                participant.getId(),
                request,
                participant.getMatch().getCategory().getTournament().getHost().getId()
        );

        assertThat(result).isEqualTo(lite);

        verify(participantMapper).updateIsWinner(request, participant);
        verify(participantRepository).save(participant);
        verify(participantMapper).toResponseLite(updatedParticipant);
    }

    @Test
    void updateIsWinner_whenNotHost_returns403() {
        Player player = PlayerTestBuilder.aPlayer().build();
        Participant participant = ParticipantTestBuilder.aParticipant().withPlayer(player).build();
        participant.setWinner(false);
        participant.setStatus("PLAYED");
        participant.setResultText("6-2");

        ParticipantUpdateWinnerRequest request = new ParticipantUpdateWinnerRequest();
        request.setWinner(true);

        when(participantRepository.findById(participant.getId())).thenReturn(Optional.of(participant));

        assertThatThrownBy(() -> participantService.updateIsWinner(participant.getId(), request, 3L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.FORBIDDEN)
                );
    }

    @Test
    void updateIsWinner_whenNotFound_returns404() {
        ParticipantUpdateWinnerRequest request = new ParticipantUpdateWinnerRequest();
        request.setWinner(true);

        when(participantRepository.findById(9999999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> participantService.updateIsWinner(9999999L, request, 3L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.NOT_FOUND)
                );
    }

    @Test
    void updateStatus_savesAndReturnsMappedLite() {
        Player player = PlayerTestBuilder.aPlayer().build();
        Participant participant = ParticipantTestBuilder.aParticipant().withPlayer(player).build();
        participant.setWinner(false);
        participant.setStatus("PLAYE");
        participant.setResultText("6-2");

        ParticipantUpdateStatusRequest request = new ParticipantUpdateStatusRequest();
        request.setStatus("PLAYED");

        Participant updatedParticipant = ParticipantTestBuilder.aParticipant().withPlayer(player).build();
        updatedParticipant.setWinner(true);
        updatedParticipant.setStatus("PLAYED");
        updatedParticipant.setResultText("6-2");
        ParticipantResponseLite lite = new ParticipantResponseLite(
                1000000L,
                "6-2",
                true,
                "PLAYED",
                participant.getPlayer().getUser().getFirstName() + " " + participant.getPlayer().getUser().getLastName()
        );

        when(participantRepository.findById(participant.getId())).thenReturn(Optional.of(participant));
        when(participantRepository.save(any(Participant.class))).thenReturn(updatedParticipant);
        when(participantMapper.toResponseLite(updatedParticipant)).thenReturn(lite);

        ParticipantResponseLite result = participantService.updateStatus(
                participant.getId(),
                request,
                participant.getMatch().getCategory().getTournament().getHost().getId()
        );

        assertThat(result).isEqualTo(lite);

        verify(participantMapper).updateStatus(request, participant);
        verify(participantRepository).save(participant);
        verify(participantMapper).toResponseLite(updatedParticipant);
    }

    @Test
    void updateStatus_whenNotFound_returns403() {
        Player player = PlayerTestBuilder.aPlayer().build();
        Participant participant = ParticipantTestBuilder.aParticipant().withPlayer(player).build();
        participant.setWinner(false);
        participant.setStatus("PLAYE");
        participant.setResultText("6-2");

        ParticipantUpdateStatusRequest request = new ParticipantUpdateStatusRequest();
        request.setStatus("PLAYED");

        when(participantRepository.findById(participant.getId())).thenReturn(Optional.of(participant));

        assertThatThrownBy(() -> participantService.updateStatus(participant.getId(), request, 3L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.FORBIDDEN)
                );
    }

    @Test
    void updateStatus_whenNotFound_returns404() {
        ParticipantUpdateStatusRequest request = new ParticipantUpdateStatusRequest();
        request.setStatus("PLAYED");

        when(participantRepository.findById(9999999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> participantService.updateStatus(9999999L, request, 3L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.NOT_FOUND)
                );
    }
}
