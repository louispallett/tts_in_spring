package com.example.tts_in_spring.participant;

import com.example.tts_in_spring.match.Match;
import com.example.tts_in_spring.match.MatchService;
import com.example.tts_in_spring.participant.dto.*;
import com.example.tts_in_spring.player.Player;
import com.example.tts_in_spring.player.PlayerService;
import com.example.tts_in_spring.team.Team;
import com.example.tts_in_spring.team.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipantService {
    private final ParticipantRepository participantRepository;
    private final ParticipantMapper participantMapper;
    private final TeamService teamService;
    private final PlayerService playerService;
    private final MatchService matchService;

    public Participant getParticipantOrThrow(Long id) {
        return participantRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Participant not found"));
    }

    private void assertHost(Participant participant, Long userId) {
        if (!participant.getMatch().getCategory().getTournament().getHost().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    boolean isParticipant(Participant participant, Long userId) {
        boolean isTeam = participant.getTeam() != null;
        if (isTeam) {
            return participant.getTeam().getPlayers().stream()
                            .anyMatch(p -> p.getUser().getId().equals(userId));
        }

        return participant.getPlayer().getUser().getId().equals(userId);
    }

    boolean isHost(Participant participant, Long userId) {
        return participant.getMatch().getCategory().getTournament().getHost().getId().equals(userId);
    }

    @Transactional(readOnly = true)
    public List<ParticipantResponse> getAllParticipants() {
        return participantRepository.findAll()
                .stream()
                .map(participantMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ParticipantResponse getParticipantById(Long id, Long userId) {
        Participant participant = getParticipantOrThrow(id);

        if (isParticipant(participant, userId) || isHost(participant, userId)) {
            return participantMapper.toResponse(participant);
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    // This is only used when creating matches, so is only called by matchService, which already authorizes the user
    @Transactional
    public ParticipantResponseLite createParticipant(ParticipantRequest request) {
        Team team = request.teamId() == null ? null : teamService.getTeamOrThrow(request.teamId());
        Player player = request.playerId() == null ? null : playerService.getPlayerOrThrow(request.playerId());
        if (team == null && player == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Both team and player are null");
        }

        Match match = matchService.getMatchOrThrow(request.matchId());

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

    @Transactional
    public ParticipantResponseLite submitScore(Long id, ParticipantSubmitScoreRequest request) {
        Participant participant = getParticipantOrThrow(id);

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
        Participant participant = getParticipantOrThrow(id);
        assertHost(participant, userId);

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
        Participant participant = getParticipantOrThrow(id);
        assertHost(participant, userId);

        participantMapper.updateIsWinner(request, participant);

        Participant savedParticipant = participantRepository.save(participant);
        return participantMapper.toResponseLite(savedParticipant);
    }

    @Transactional
    public ParticipantResponseLite updateStatus(Long id, ParticipantUpdateStatusRequest request, Long userId) {
        Participant participant = getParticipantOrThrow(id);
        assertHost(participant, userId);

        participantMapper.updateStatus(request, participant);

        Participant savedParticipant = participantRepository.save(participant);
        return participantMapper.toResponseLite(savedParticipant);
    }
}
