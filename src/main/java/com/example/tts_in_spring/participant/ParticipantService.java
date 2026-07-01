package com.example.tts_in_spring.participant;

import com.example.tts_in_spring.category.Category;
import com.example.tts_in_spring.exception.GenericBadRequestException;
import com.example.tts_in_spring.exception.ForbiddenException;
import com.example.tts_in_spring.match.Match;
import com.example.tts_in_spring.match.MatchFinder;
import com.example.tts_in_spring.participant.dto.*;
import com.example.tts_in_spring.player.Player;
import com.example.tts_in_spring.player.PlayerFinder;
import com.example.tts_in_spring.team.Team;
import com.example.tts_in_spring.team.TeamFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipantService {
    private final ParticipantRepository participantRepository;
    private final ParticipantMapper participantMapper;
    private final ParticipantFinder participantFinder;
    private final TeamFinder teamFinder;
    private final PlayerFinder playerFinder;
    private final MatchFinder matchFinder;

    @Transactional(readOnly = true)
    public List<ParticipantResponse> getAllParticipants() {
        return participantRepository.findAll()
                .stream()
                .map(participantMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ParticipantResponse getParticipantById(Long id, Long userId) {
        Participant participant = participantFinder.getParticipantOrThrow(id);

        if (participantFinder.isParticipant(participant, userId) || participantFinder.isHost(participant, userId)) {
            return participantMapper.toResponse(participant);
        }

        throw new ForbiddenException();
    }

    // This is only used when creating matches, so is only called by matchService, which already authorizes the user
    @Transactional
    public ParticipantResponseLite createParticipant(ParticipantRequest request) {
        Team team = request.teamId() == null ? null : teamFinder.getTeamOrThrow(request.teamId());
        Player player = request.playerId() == null ? null : playerFinder.getPlayerOrThrow(request.playerId());
        if (team == null && player == null) {
            throw new GenericBadRequestException("Both team and player are null");
        }

        Match match = matchFinder.getMatchOrThrow(request.matchId());

        Participant participant = participantMapper.toEntity(request);
        participant.setPlayer(player);
        participant.setTeam(team);
        participant.setMatch(match);
        participant.setResultText("");
        participant.setWinner(false);
        participant.setStatus("TBC");

        Participant savedParticipant = participantRepository.save(participant);
        return participantMapper.toResponseLite(savedParticipant);
    }

    private Participant createDefaultParticipant() {
        Participant participant = new Participant();
        participant.setResultText("");
        participant.setWinner(false);
        participant.setStatus("TBC");
        return participant;
    }

    public List<Participant> generateParticipants(Category category) {
        if (category.getPlayers().isEmpty() && category.getTeams().isEmpty()) {
            throw new GenericBadRequestException("No players or teams");
        }

        if (!category.getPlayers().isEmpty() && !category.getTeams().isEmpty()) {
            throw new GenericBadRequestException("Category has both teams and players");
        }

        List<Participant> participants = new ArrayList<>();
        for (Player player : category.getPlayers()) {
            Participant newParticipant = createDefaultParticipant();
            newParticipant.setPlayer(player);

            participants.add(newParticipant);
        }

        for (Team team : category.getTeams()) {
            Participant newParticipant = createDefaultParticipant();
            newParticipant.setTeam(team);

            participants.add(newParticipant);
        }

        return participants;
    }

    public void saveAllParticipants(List<Participant> participants) {
        participantRepository.saveAll(participants);
    }

    @Transactional
    public ParticipantResponseLite submitScore(Long id, ParticipantSubmitScoreRequest request) {
        Participant participant = participantFinder.getParticipantOrThrow(id);

        participantMapper.submitScore(request, participant);

        Participant savedParticipant = participantRepository.save(participant);
        return participantMapper.toResponseLite(savedParticipant);
    }

    @Transactional
    public ParticipantResponseLite updateResultText(
            Long id,
            ParticipantUpdateResultTextRequest request,
            Long userId
    ) {
        Participant participant = participantFinder.getParticipantOrThrow(id);
        participantFinder.assertHost(participant, userId);

        participantMapper.updateResultText(request, participant);

        Participant savedParticipant = participantRepository.save(participant);
        return participantMapper.toResponseLite(savedParticipant);
    }

    @Transactional
    public ParticipantResponseLite updateIsWinner(
            Long id,
            ParticipantUpdateWinnerRequest request,
            Long userId
    ) {
        Participant participant = participantFinder.getParticipantOrThrow(id);
        participantFinder.assertHost(participant, userId);

        participantMapper.updateIsWinner(request, participant);

        Participant savedParticipant = participantRepository.save(participant);
        return participantMapper.toResponseLite(savedParticipant);
    }

    @Transactional
    public ParticipantResponseLite updateStatus(Long id, ParticipantUpdateStatusRequest request, Long userId) {
        Participant participant = participantFinder.getParticipantOrThrow(id);
        participantFinder.assertHost(participant, userId);

        participantMapper.updateStatus(request, participant);

        Participant savedParticipant = participantRepository.save(participant);
        return participantMapper.toResponseLite(savedParticipant);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Participant participant = participantFinder.getParticipantOrThrow(id);
        participantFinder.assertHost(participant, userId);

        participantRepository.delete(participant);
    }
}
