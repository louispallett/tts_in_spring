package com.example.tts_in_spring.match;

import com.example.tts_in_spring.category.dto.CategoryResponseLite;
import com.example.tts_in_spring.match.dto.*;
import com.example.tts_in_spring.notification.NotificationService;
import com.example.tts_in_spring.participant.Participant;
import com.example.tts_in_spring.participant.ParticipantFinder;
import com.example.tts_in_spring.participant.ParticipantService;
import com.example.tts_in_spring.participant.ParticipantTestBuilder;
import com.example.tts_in_spring.participant.dto.ParticipantResponseLite;
import com.example.tts_in_spring.participant.dto.ParticipantSubmitScoreRequest;
import com.example.tts_in_spring.player.Player;
import com.example.tts_in_spring.player.PlayerTestBuilder;
import com.example.tts_in_spring.score.ScoreService;
import com.example.tts_in_spring.score.dto.ScoreResponse;
import com.example.tts_in_spring.user.User;
import com.example.tts_in_spring.user.UserTestBuilder;
import com.example.tts_in_spring.user.dto.UserResponseLite;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MatchScoreSubmissionServiceTest {
    @Mock
    private MatchMapper matchMapper;

    @Mock
    private MatchFinder matchFinder;

    @Mock
    private ParticipantFinder participantFinder;

    @Mock
    private ParticipantService participantService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private ScoreService scoreService;

    @InjectMocks
    private MatchScoreSubmissionService matchScoreSubmissionService;

    private ParticipantResponseLite buildParticipantResponseLite(Long id, String resultText, boolean winner, String name) {
        return new ParticipantResponseLite(
                id,
                resultText,
                winner,
                "PLAYED",
                name
        );
    }

    private MatchResponse buildMatchResponse(List<ParticipantResponseLite> participants) {
        return new MatchResponse(
                100000L,
                "",
                State.SCORE_DONE,
                Instant.now(),
                false,
                new CategoryResponseLite(100L, "Mens Singles", false),
                null,
                new ScoreResponse(10L, Instant.now(), new UserResponseLite(1L, "John", "Doe")),
                List.of(),
                participants
        );
    }

    @Test
    void submitScore_whenHost_savesAndReturnsMappedLite() {
        Match match = MatchTestBuilder.aMatch().build();
        Participant participant1 = ParticipantTestBuilder.aParticipant().withId(1L).withMatch(match).build();
        Participant participant2 = ParticipantTestBuilder.aParticipant().withId(2L).withMatch(match).build();
        match.getParticipants().add(participant1);
        match.getParticipants().add(participant2);

        SubmitScoreRequest request = new SubmitScoreRequest(
                List.of(
                        new ParticipantSubmitScoreRequest(
                                participant1.getId(), "6-6", true
                        ),
                        new ParticipantSubmitScoreRequest(
                                participant2.getId(), "0-0", false
                        )
                )
        );

        List<ParticipantResponseLite> participantLites = List.of(
                buildParticipantResponseLite(1L, "6-6", true, "Winning Player"),
                buildParticipantResponseLite(2L, "0-0", false, "Losing Player")
        );
        MatchResponse response = buildMatchResponse(participantLites);

        when(matchFinder.getMatchOrThrow(match.getId())).thenReturn(match);
        when(matchFinder.isHost(match, match.getCategory().getTournament().getHost().getId())).thenReturn(true);
        when(participantFinder.getParticipantOrThrow(1L)).thenReturn(ParticipantTestBuilder.aParticipant().withId(1L).build());
        when(matchMapper.toResponse(match)).thenReturn(response);

        assertThat(matchScoreSubmissionService.submitScore(
                match.getId(),
                request,
                match.getCategory().getTournament().getHost().getId())
        ).isEqualTo(response);

        verify(matchMapper).updateStateEntity(new UpdateStateRequest(State.SCORE_DONE), match);
        verify(matchMapper).toResponse(match);
    }

    @Test
    void submitScore_whenParticipant_savesAndReturnsMappedLite() {
        Match match = MatchTestBuilder.aMatch().build();
        User user = UserTestBuilder.aUser().withId(2L).build();
        Player player = PlayerTestBuilder.aPlayer().withUser(user).build();
        Participant participant1 = ParticipantTestBuilder.aParticipant().withId(1L).withPlayer(player).withMatch(match).build();
        Participant participant2 = ParticipantTestBuilder.aParticipant().withId(2L).withMatch(match).build();
        match.getParticipants().add(participant1);
        match.getParticipants().add(participant2);

        SubmitScoreRequest request = new SubmitScoreRequest(
                List.of(
                        new ParticipantSubmitScoreRequest(
                                participant1.getId(), "6-6", true
                        ),
                        new ParticipantSubmitScoreRequest(
                                participant2.getId(), "0-0", false
                        )
                )
        );

        List<ParticipantResponseLite> participantLites = List.of(
                buildParticipantResponseLite(1L, "6-6", true, "Winning Player"),
                buildParticipantResponseLite(2L, "0-0", false, "Losing Player")
        );
        MatchResponse response = buildMatchResponse(participantLites);

        when(matchFinder.getMatchOrThrow(match.getId())).thenReturn(match);
        when(matchFinder.isHost(match, participant1.getPlayer().getUser().getId())).thenReturn(false);
        when(matchFinder.isParticipant(match, participant1.getPlayer().getUser().getId())).thenReturn(true);
        when(participantFinder.getParticipantOrThrow(1L)).thenReturn(ParticipantTestBuilder.aParticipant().withId(1L).build());
        when(matchMapper.toResponse(match)).thenReturn(response);

        assertThat(matchScoreSubmissionService.submitScore(
                match.getId(),
                request,
                participant1.getPlayer().getUser().getId()
        )).isEqualTo(response);

        verify(matchMapper).updateStateEntity(new UpdateStateRequest(State.SCORE_DONE), match);
        verify(matchMapper).toResponse(match);
    }
}
