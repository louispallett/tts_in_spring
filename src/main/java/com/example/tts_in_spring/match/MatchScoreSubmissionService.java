package com.example.tts_in_spring.match;

import com.example.tts_in_spring.exception.ForbiddenException;
import com.example.tts_in_spring.exception.GenericBadRequestException;
import com.example.tts_in_spring.match.dto.MatchResponse;
import com.example.tts_in_spring.match.dto.SubmitScoreRequest;
import com.example.tts_in_spring.match.dto.UpdateStateRequest;
import com.example.tts_in_spring.participant.Participant;
import com.example.tts_in_spring.participant.ParticipantFinder;
import com.example.tts_in_spring.participant.ParticipantService;
import com.example.tts_in_spring.participant.Status;
import com.example.tts_in_spring.participant.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchScoreSubmissionService {
    private final MatchFinder matchFinder;
    private final MatchMapper matchMapper;
    private final ParticipantFinder participantFinder;
    private final ParticipantService participantService;

    @Transactional
    public MatchResponse submitScore(
            Long id,
            SubmitScoreRequest request,
            Long userId
    ) {
        Match match = matchFinder.getMatchOrThrow(id);

        if (match.getState() == State.SCORE_DONE)
            throw new GenericBadRequestException("Match score already submitted");

        if (!matchFinder.isHost(match, userId) && !matchFinder.isParticipant(match, userId)) {
            throw new ForbiddenException(
                    "Not host of "
                            + match.getCategory().getTournament().getName()
                            + " ("
                            + match.getCategory().getTournament().getId()
                            + ") or participant in match " + match.getId()
            );
        }

        handleParticipants(request.participants(), match);

        matchMapper.updateStateEntity(new UpdateStateRequest(State.SCORE_DONE), match);
        return matchMapper.toResponse(match);
    }

    private void handleParticipants(List<ParticipantSubmitScoreRequest> participants, Match match) {
        for (ParticipantSubmitScoreRequest participant : participants) {
            participantService.updateResultText(
                    participant.id(),
                    new UpdateResultTextRequest(participant.resultText()),
                    match.getCategory().getTournament().getHost().getId()
            );

            participantService.updateIsWinner(
                    participant.id(),
                    new ParticipantUpdateWinnerRequest(participant.winner()),
                    match.getCategory().getTournament().getHost().getId()
            );

            participantService.updateStatus(
                    participant.id(),
                    new ParticipantUpdateStatusRequest(Status.PLAYED),
                    match.getCategory().getTournament().getHost().getId()
            );

            if (participant.winner()) {
                Participant existingParticipant = participantFinder.getParticipantOrThrow(participant.id());
                participantService.createParticipant(
                        new ParticipantRequest(
                                existingParticipant.getTeam() == null ? null : existingParticipant.getTeam().getId(),
                                existingParticipant.getPlayer() == null ? null : existingParticipant.getPlayer().getId(),
                                match.getNextMatch().getId()
                        )
                );
            }
        }
    }
}
