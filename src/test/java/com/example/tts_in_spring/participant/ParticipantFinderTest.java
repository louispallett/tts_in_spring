package com.example.tts_in_spring.participant;

import com.example.tts_in_spring.player.Player;
import com.example.tts_in_spring.player.PlayerTestBuilder;
import com.example.tts_in_spring.team.Team;
import com.example.tts_in_spring.team.TeamTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParticipantFinderTest {
    @Mock
    private ParticipantRepository participantRepository;

    @InjectMocks
    private ParticipantFinder participantFinder;

    @Test
    void getParticipantOrThrow_whenParticipantExists_returnsParticipant() {
        Participant participant = ParticipantTestBuilder.aParticipant().build();

        when(participantRepository.findById(participant.getId())).thenReturn(Optional.of(participant));

        assertThat(participantFinder.getParticipantOrThrow(participant.getId())).isEqualTo(participant);
    }

    @Test
    void getParticipantOrThrow_whenParticipantDoesNotExist_throws404() {
        when(participantRepository.findById(9999999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> participantFinder.getParticipantOrThrow(9999999L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void assertHost_whenHost_doesNotThrow() {
        Participant participant = ParticipantTestBuilder.aParticipant().build();

        assertThatCode(() -> participantFinder.assertHost(participant, participant.getMatch().getCategory().getTournament().getHost().getId()))
                .doesNotThrowAnyException();
    }

    @Test
    void assertHost_whenNotHost_throws403() {
        assertThatThrownBy(() -> participantFinder.assertHost(ParticipantTestBuilder.aParticipant().build(), 3L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.FORBIDDEN));
    }

    @Test
    void isParticipant_whenPlayer_returnsTrue() {
        Player player = PlayerTestBuilder.aPlayer().build();
        Participant participant = ParticipantTestBuilder.aParticipant().withPlayer(player).build();

        assertThat(participantFinder.isParticipant(participant, player.getUser().getId())).isTrue();
    }

    @Test
    void isParticipant_whenTeam_returnsTrue() {
        Team team = TeamTestBuilder.aTeam().build();
        Player player = PlayerTestBuilder.aPlayer().withTeam(team).build();
        team.getPlayers().add(player);
        Participant participant = ParticipantTestBuilder.aParticipant().withTeam(team).build();

        assertThat(participantFinder.isParticipant(participant, player.getUser().getId())).isTrue();
    }

    @Test
    void isParticipant_whenNotPlayerOrTeam_returnsFalse() {
        Participant participant = ParticipantTestBuilder.aParticipant().withPlayer(PlayerTestBuilder.aPlayer().build()).build();

        assertThat(participantFinder.isParticipant(participant, 3L)).isFalse();
    }

    @Test
    void isHost_whenHost_returnsTrue() {
        Participant participant = ParticipantTestBuilder.aParticipant().build();

        assertThat(participantFinder.isHost(participant, participant.getMatch().getCategory().getTournament().getHost().getId())).isTrue();
    }

    @Test
    void isHost_whenNotHost_returnsFalse() {
        assertThat(participantFinder.isHost(ParticipantTestBuilder.aParticipant().build(), 3L)).isFalse();
    }
}
