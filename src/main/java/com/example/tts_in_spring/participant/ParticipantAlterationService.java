package com.example.tts_in_spring.participant;

import com.example.tts_in_spring.exception.ConflictException;
import com.example.tts_in_spring.exception.GenericBadRequestException;
import com.example.tts_in_spring.match.Match;
import com.example.tts_in_spring.match.MatchFinder;
import com.example.tts_in_spring.match.State;
import com.example.tts_in_spring.participant.dto.ChangeWinningParticipantRequest;
import com.example.tts_in_spring.participant.dto.ParticipantRequest;
import com.example.tts_in_spring.participant.dto.ParticipantResponseLite;
import com.example.tts_in_spring.participant.dto.ParticipantUpdateWinnerRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipantAlterationService {
    private final MatchFinder matchFinder;
    private final ParticipantFinder participantFinder;
    private final ParticipantService participantService;

    @Transactional
    public ParticipantResponseLite changeWinningParticipant(
            Long matchId,
            ChangeWinningParticipantRequest request,
            Long userId
    ) {
        Match match = matchFinder.getMatchOrThrow(matchId);
        matchFinder.assertHost(match, userId);
        Participant newWinningParticipant = participantFinder.getParticipantOrThrow(request.newWinningParticipant());

        // Check match score hasn't yet been submitted
        if (!match.getState().equals(State.SCORE_DONE))
            throw new GenericBadRequestException("Match score not submitted. Please submit score through standard procedure.");

        // Ensure that participant is in match
        participantFinder.assertParticipants(List.of(newWinningParticipant), match);

        // Check participant is actually not winner
        if (newWinningParticipant.isWinner())
            throw new GenericBadRequestException("Selected participant is already the winner of this match");

        List<Participant> newLosingParticipantFilter = match.getParticipants().stream()
                .filter(p -> !p.getId().equals(newWinningParticipant.getId())).toList();
        if (newLosingParticipantFilter.size() != 1)
            throw new ConflictException("Current winner not found");

        Participant newLosingParticipant = newLosingParticipantFilter.getFirst();

        List<Participant> newLosingParticipantListInNextMatch = match.getNextMatch().getParticipants().stream()
                .filter(p ->
                        p.getPlayer().equals(newLosingParticipant.getPlayer())
                        && p.getTeam().equals(newLosingParticipant.getTeam())
                ).toList();

        if (newLosingParticipantListInNextMatch.isEmpty())
            throw new GenericBadRequestException("Losing player is not in next match");

        // Delete relevant participant in next match
        participantService.delete(newLosingParticipantListInNextMatch.getFirst().getId(), userId);

        // Update winner status
        participantService.updateWinner(newWinningParticipant.getId(), new ParticipantUpdateWinnerRequest(true), userId);
        participantService.updateWinner(newLosingParticipant.getId(), new ParticipantUpdateWinnerRequest(false), userId);

        // Create new participant in nextMatch (from winning participant of current match) and return
        return participantService.createParticipant(new ParticipantRequest(
                newWinningParticipant.getTeam().getId(),
                newWinningParticipant.getPlayer().getId(),
                match.getNextMatch().getId()
        ));
    }
}
