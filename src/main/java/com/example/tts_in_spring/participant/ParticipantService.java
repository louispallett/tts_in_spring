package com.example.tts_in_spring.participant;

import com.example.tts_in_spring.category.Category;
import com.example.tts_in_spring.exception.GenericBadRequestException;
import com.example.tts_in_spring.exception.ForbiddenException;
import com.example.tts_in_spring.exception.TeamGenerationException;
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
import java.util.Comparator;
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

        Participant savedParticipant = participantRepository.save(participant);
        return participantMapper.toResponseLite(savedParticipant);
    }

    private Participant createDefaultParticipant() {
        Participant participant = new Participant();
        participant.setResultText("");
        participant.setWinner(false);
        return participant;
    }

    public List<Participant> generateParticipants(Category category) {
        if (category.getPlayers().isEmpty())
            throw new GenericBadRequestException("No players");

        List<Participant> participants = new ArrayList<>();

        if (category.isDoubles()) {
            if (category.getTeams().isEmpty())
                throw new TeamGenerationException("No teams in doubles category");

            List<Team> teams = category.getTeams().stream().sorted(
                    Comparator.comparingInt(team ->
                            team.getPlayers().stream()
                                    .mapToInt(Player::getRank)
                                    .sum()
                    ))
                    .toList();

            for (Team team : teams) {
                Participant newParticipant = createDefaultParticipant();
                newParticipant.setTeam(team);

                participants.add(newParticipant);
            }
        } else {
            List<Player> players = category.getPlayers().stream().sorted(
                            Comparator.comparingInt(Player::getRank))
                    .toList();

            for (Player player : players) {
                Participant newParticipant = createDefaultParticipant();
                newParticipant.setPlayer(player);

                participants.add(newParticipant);
            }
        }

        return participants;
    }

    public void saveAllParticipants(List<Participant> participants) {
        participantRepository.saveAll(participants);
    }

    @Transactional
    public ParticipantResponseLite updateResultText(
            Long id,
            UpdateResultTextRequest request,
            Long userId
    ) {
        Participant participant = participantFinder.getParticipantOrThrow(id);
        participantFinder.assertHost(participant, userId);

        participantMapper.updateResultText(request, participant);

        return participantMapper.toResponseLite(participant);
    }

    @Transactional
    public ParticipantResponseLite updateWinner(
            Long id,
            ParticipantUpdateWinnerRequest request,
            Long userId
    ) {
        Participant participant = participantFinder.getParticipantOrThrow(id);
        participantFinder.assertHost(participant, userId);

        participantMapper.updateWinner(request, participant);
        return participantMapper.toResponseLite(participant);
    }

    @Transactional
    public ParticipantResponseLite updateStatus(
            Long id,
            ParticipantUpdateStatusRequest request,
            Long userId
    ) {
        Participant participant = participantFinder.getParticipantOrThrow(id);
        participantFinder.assertHost(participant, userId);

        participantMapper.updateStatus(request, participant);
        return participantMapper.toResponseLite(participant);
    }

    @Transactional
    public ParticipantResponseLite changeParticipantMatch(Long id, ParticipantChangeMatchRequest request, Long userId) {
        if (request.oldParticipantId().equals(id))
            throw new GenericBadRequestException("id param must be old participant id");

        Participant participant = participantFinder.getParticipantOrThrow(id);
        Match match = matchFinder.getMatchOrThrow(participant.getMatch().getId());
        participantFinder.assertHost(participant, userId);

        if (match.getCategory().isDoubles()) {
            if (request.teamId() == null)
                throw new GenericBadRequestException("TeamId cannot be null");

            delete(participant.getId(), userId);

            return createParticipant(new ParticipantRequest(
                    request.teamId(), null, match.getId()
            ));
        } else {
            if (request.playerId() == null)
                throw new GenericBadRequestException("PlayerId cannot be null");

            delete(participant.getId(), userId);

            return createParticipant(new ParticipantRequest(
                    null, request.playerId(), match.getId()
            ));
        }
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Participant participant = participantFinder.getParticipantOrThrow(id);
        participantFinder.assertHost(participant, userId);

        participantRepository.delete(participant);
    }
}
