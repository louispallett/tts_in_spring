package com.example.tts_in_spring.participant;

import com.example.tts_in_spring.match.MatchResponseLite;
import com.example.tts_in_spring.match.MatchService;
import com.example.tts_in_spring.match.MatchTestBuilder;
import com.example.tts_in_spring.player.Player;
import com.example.tts_in_spring.player.PlayerResponseLite;
import com.example.tts_in_spring.player.PlayerService;
import com.example.tts_in_spring.player.PlayerTestBuilder;
import com.example.tts_in_spring.team.Team;
import com.example.tts_in_spring.team.TeamResponseLite;
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

    private ParticipantResponseLite buildParticipantResponseLite(String resultText, boolean isWinner) {
        return new ParticipantResponseLite(
                1000000L,
                resultText,
                isWinner,
                null
        );
    }

    private ParticipantRequest buildParticipantRequest(Team team, Player player) {
        ParticipantRequest r = new ParticipantRequest();
        r.setTeamId(team.getId());
        r.setPlayerId(player.getId());
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
    void createParticipant_whenHost_savesAndReturnsMappedLite() {
        ParticipantRequest request = buildParticipantRequest(null, PlayerTestBuilder.aPlayer().build());

        Participant saved = ParticipantTestBuilder.aParticipant().build();
        ParticipantResponseLite lite = new ParticipantResponseLite(
                1000000L,
                "",
                false,
                null
        );

        when(matchService.getMatchOrThrow(request.getMatchId())).thenReturn(saved.getMatch());
        when(playerService).getPlayerOrThrow(request.getPlayerId()).thenReturn(saved.getPlayer());
    }

    @Test
    void createParticipant_whenNotHost_returns403() {

    }

    @Test
    void updateResultText_whenHost_savesAndReturnsMappedLite() {

    }

    @Test
    void updateResultText_whenParticipantOfMatch_savesAndReturnsMappedLite() {

    }

    @Test
    void updateResultText_whenNotAuthorized_returns403() {

    }

    @Test
    void updateResultText_whenNotFound_returns404() {

    }

    @Test
    void updateIsWinner_whenHost_savesAndReturnsMappedLite() {

    }

    @Test
    void updateIsWinner_whenParticipantOfMatch_savesAndReturnsMappedLite() {

    }

    @Test
    void updateIsWinner_whenNotAuthorized_returns403() {

    }

    @Test
    void updateIsWinner_whenNotFound_returns404() {

    }

    @Test
    void updateStatus_whenHost_savesAndReturnsMappedLite() {

    }

    @Test
    void updateStatus_whenParticipantOfMatch_savesAndReturnsMappedLite() {

    }

    @Test
    void updateStatus_whenNotAuthorized_returns403() {

    }

    @Test
    void updateStatus_whenNotFound_returns404() {

    }
}
