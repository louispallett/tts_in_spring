package com.example.tts_in_spring.participant;

import com.example.tts_in_spring.match.MatchFinder;
import com.example.tts_in_spring.match.dto.MatchResponseLite;
import com.example.tts_in_spring.match.MatchTestBuilder;
import com.example.tts_in_spring.participant.dto.*;
import com.example.tts_in_spring.player.*;
import com.example.tts_in_spring.player.dto.PlayerResponseLite;
import com.example.tts_in_spring.team.Team;
import com.example.tts_in_spring.team.TeamFinder;
import com.example.tts_in_spring.team.dto.TeamResponseLite;
import com.example.tts_in_spring.team.TeamTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.function.Function;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ParticipantServiceTest {
    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private ParticipantMapper participantMapper;

    @Mock
    private ParticipantFinder participantFinder;

    @Mock
    private MatchFinder matchFinder;

    @Mock
    private PlayerFinder playerFinder;

    @Mock
    private TeamFinder teamFinder;

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
        return new ParticipantRequest(
            safeId(team, Team::getId),
            safeId(player, Player::getId),
            MatchTestBuilder.aMatch().build().getId()
        );
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

        when(participantFinder.getParticipantOrThrow(participant.getId())).thenReturn(participant);
        when(participantFinder.isHost(participant, participant.getMatch().getCategory().getTournament().getHost().getId())).thenReturn(true);
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

        when(participantFinder.getParticipantOrThrow(participant.getId())).thenReturn(participant);
        when(participantFinder.isParticipant(participant, participant.getPlayer().getUser().getId())).thenReturn(true);
        when(participantMapper.toResponse(participant)).thenReturn(response);

        assertThat(participantService.getParticipantById(
                participant.getId(),
                participant.getPlayer().getUser().getId()
        )).isEqualTo(response);
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

        when(matchFinder.getMatchOrThrow(request.matchId())).thenReturn(saved.getMatch());
        when(playerFinder.getPlayerOrThrow(request.playerId())).thenReturn(saved.getPlayer());
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

        when(matchFinder.getMatchOrThrow(request.matchId())).thenReturn(saved.getMatch());
        when(teamFinder.getTeamOrThrow(request.teamId())).thenReturn(saved.getTeam());
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
        ParticipantSubmitScoreRequest request = new ParticipantSubmitScoreRequest(
            updatedParticipant.getId(),
            "6-6",
                true
        );

        ParticipantResponseLite lite = buildParticipantResponseLite(
                "6-6",
                true,
                updatedParticipant.getPlayer().getUser().getFirstName() + " " + updatedParticipant.getPlayer().getUser().getLastName()
        );

        when(participantFinder.getParticipantOrThrow(participant.getId())).thenReturn(participant);
        when(participantRepository.save(any(Participant.class))).thenReturn(updatedParticipant);
        when(participantMapper.toResponseLite(updatedParticipant)).thenReturn(lite);

        ParticipantResponseLite result = participantService.submitScore(updatedParticipant.getId(), request);
        assertThat(result).isEqualTo(lite);

        verify(participantMapper).submitScore(request, participant);
        verify(participantRepository).save(participant);
        verify(participantMapper).toResponseLite(updatedParticipant);
    }

    // NOTE: Alternatively, the methods below are called by ParticipantController, and only the host is authorized to call
    // them. Therefore, these DO need to check and authorize.
    @Test
    void updateResultText_whenHost_savesAndReturnsMappedLite() {
        Player player = PlayerTestBuilder.aPlayer().build();
        Participant participant = ParticipantTestBuilder.aParticipant().withPlayer(player).build();
        participant.setWinner(true);
        participant.setStatus(Status.PLAYED);
        participant.setResultText("2-6");

        ParticipantUpdateResultTextRequest request = new ParticipantUpdateResultTextRequest("6-2");

        Participant updatedParticipant = ParticipantTestBuilder.aParticipant().withPlayer(player).build();
        updatedParticipant.setWinner(true);
        updatedParticipant.setStatus(Status.PLAYED);
        updatedParticipant.setResultText("6-2");
        ParticipantResponseLite lite = new ParticipantResponseLite(
                1000000L,
                "6-2",
                true,
                "PLAYED",
                participant.getPlayer().getUser().getFirstName() + " " + participant.getPlayer().getUser().getLastName()
        );

        when(participantFinder.getParticipantOrThrow(participant.getId())).thenReturn(participant);
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
    void updateIsWinner_savesAndReturnsMappedLite() {
        Player player = PlayerTestBuilder.aPlayer().build();
        Participant participant = ParticipantTestBuilder.aParticipant().withPlayer(player).build();
        participant.setWinner(false);
        participant.setStatus(Status.PLAYED);
        participant.setResultText("6-2");

        ParticipantUpdateWinnerRequest request = new ParticipantUpdateWinnerRequest(true);

        Participant updatedParticipant = ParticipantTestBuilder.aParticipant().withPlayer(player).build();
        updatedParticipant.setWinner(true);
        updatedParticipant.setStatus(Status.PLAYED);
        updatedParticipant.setResultText("6-2");
        ParticipantResponseLite lite = new ParticipantResponseLite(
                1000000L,
                "6-2",
                true,
                "PLAYED",
                participant.getPlayer().getUser().getFirstName() + " " + participant.getPlayer().getUser().getLastName()
        );

        when(participantFinder.getParticipantOrThrow(participant.getId())).thenReturn(participant);
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
    void updateStatus_savesAndReturnsMappedLite() {
        Player player = PlayerTestBuilder.aPlayer().build();
        Participant participant = ParticipantTestBuilder.aParticipant().withPlayer(player).build();
        participant.setWinner(false);
        participant.setStatus(Status.PLAYED);
        participant.setResultText("6-2");

        ParticipantUpdateStatusRequest request = new ParticipantUpdateStatusRequest("PLAYED");

        Participant updatedParticipant = ParticipantTestBuilder.aParticipant().withPlayer(player).build();
        updatedParticipant.setWinner(true);
        updatedParticipant.setStatus(Status.PLAYED);
        updatedParticipant.setResultText("6-2");
        ParticipantResponseLite lite = new ParticipantResponseLite(
                1000000L,
                "6-2",
                true,
                "PLAYED",
                participant.getPlayer().getUser().getFirstName() + " " + participant.getPlayer().getUser().getLastName()
        );

        when(participantFinder.getParticipantOrThrow(participant.getId())).thenReturn(participant);
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
}
