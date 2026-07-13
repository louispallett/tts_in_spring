package com.example.tts_in_spring.team;

import com.example.tts_in_spring.category.Category;
import com.example.tts_in_spring.category.CategoryFinder;
import com.example.tts_in_spring.category.Type;
import com.example.tts_in_spring.exception.ForbiddenException;
import com.example.tts_in_spring.exception.GenericBadRequestException;
import com.example.tts_in_spring.exception.TeamGenerationException;
import com.example.tts_in_spring.player.Player;
import com.example.tts_in_spring.player.PlayerMapper;
import com.example.tts_in_spring.player.PlayerService;
import com.example.tts_in_spring.player.dto.AddTeamRequest;
import com.example.tts_in_spring.player.dto.PlayerResponse;
import com.example.tts_in_spring.team.dto.TeamRequest;
import com.example.tts_in_spring.team.dto.TeamResponse;
import com.example.tts_in_spring.team.dto.TeamResponseLite;
import com.example.tts_in_spring.team.dto.TeamsRequest;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;
    private final TeamFinder teamFinder;
    private final PlayerMapper playerMapper;
    private final PlayerService playerService;
    private final CategoryFinder categoryFinder;

    @Transactional(readOnly = true)
    public List<TeamResponse> getAllTeams() {
        return teamRepository.findAll()
                .stream()
                .map(teamMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TeamResponse getTeamById(Long id, Long userId) {
        Team team = teamFinder.getTeamOrThrow(id);

        boolean isHost = team.getCategory()
                .getTournament()
                .getHost()
                .getId()
                .equals(userId);

        boolean isTeamMember = team.getPlayers().stream()
                .anyMatch(p -> p.getUser().getId().equals(userId));

        if (isHost || isTeamMember) {
            return teamMapper.toResponse(team);
        }

        throw new ForbiddenException();
    }

    public List<List<Player>> generateMixed(List<Player> players) {
        if (players.size() % 2 != 0) throw new TeamGenerationException("Players are not an even number");

        List<Player> maleSeeded = new ArrayList<>(players.stream().filter(p -> p.isMale() && p.isSeeded()).toList());
        List<Player> maleNonSeeded = new ArrayList<>(players.stream().filter(p -> p.isMale() && !p.isSeeded()).toList());
        List<Player> femaleSeeded = new ArrayList<>(players.stream().filter(p -> !p.isMale() && p.isSeeded()).toList());
        List<Player> femaleNonSeeded = new ArrayList<>(players.stream().filter(p -> !p.isMale() && !p.isSeeded()).toList());

        Collections.shuffle(maleSeeded);
        Collections.shuffle(maleNonSeeded);
        Collections.shuffle(femaleSeeded);
        Collections.shuffle(femaleNonSeeded);

        Queue<Player> maleSeededQueue = new ArrayDeque<>(maleSeeded);
        Queue<Player> maleNonSeededQueue = new ArrayDeque<>(maleNonSeeded);
        Queue<Player> femaleSeededQueue = new ArrayDeque<>(femaleSeeded);
        Queue<Player> femaleNonSeededQueue = new ArrayDeque<>(femaleNonSeeded);

        List<List<Player>> teams = new ArrayList<>();

        while(!maleSeededQueue.isEmpty() && !femaleNonSeededQueue.isEmpty()) {
            teams.add(List.of(maleSeededQueue.poll(), femaleNonSeededQueue.poll()));
        }

        while(!femaleSeededQueue.isEmpty() && !maleNonSeededQueue.isEmpty()) {
            teams.add(List.of(femaleSeededQueue.poll(), maleNonSeededQueue.poll()));
        }

        boolean seededHeavy = maleSeeded.size() + femaleSeeded.size() > maleNonSeeded.size() + femaleNonSeeded.size();

        if (seededHeavy) {
            while (!maleSeededQueue.isEmpty() && !femaleSeededQueue.isEmpty()) {
                teams.add(List.of(maleSeededQueue.poll(), femaleSeededQueue.poll()));
            }
        } else {
            while (!maleNonSeededQueue.isEmpty() && !femaleNonSeededQueue.isEmpty()) {
                teams.add(List.of(maleNonSeededQueue.poll(), femaleNonSeededQueue.poll()));
            }
        }

        return teams;
    }

    public List<List<Player>> generateDoubles(List<Player> players) {
        if (players.size() % 2 != 0) throw new TeamGenerationException("Players are not an even number");

        List<Player> seeded = new ArrayList<>(players.stream().filter(Player::isSeeded).toList());
        List<Player> nonSeeded = new ArrayList<>(players.stream().filter(p -> !p.isSeeded()).toList());

        Collections.shuffle(seeded);
        Collections.shuffle(nonSeeded);

        Queue<Player> seededQueue = new ArrayDeque<>(seeded);
        Queue<Player> nonSeededQueue = new ArrayDeque<>(nonSeeded);

        List<List<Player>> teams = new ArrayList<>();

        while (!seededQueue.isEmpty() && !nonSeededQueue.isEmpty()) {
            teams.add(List.of(seededQueue.poll(), nonSeededQueue.poll()));
        }

        if (seededQueue.size() > 1) {
            while (!seededQueue.isEmpty()) {
                teams.add(List.of(seededQueue.poll(), seededQueue.poll()));
            }
        } else {
            while (nonSeededQueue.size() > 1) {
                teams.add(List.of(nonSeededQueue.poll(), nonSeededQueue.poll()));
            }

        }

        return teams;
    }

    public List<List<PlayerResponse>> generateTeams(Long categoryId, Long userId) {
        Category category = categoryFinder.getCategoryOrThrow(categoryId);
        categoryFinder.assertHost(category, userId);

        if (!category.isDoubles()) throw new GenericBadRequestException("Cannot create teams for a singles tournament");

        List<List<Player>> teams;

        if (category.getName().equals(Type.MIXED_DOUBLES)) {
            teams = generateMixed(category.getPlayers());
        } else {
            teams = generateDoubles(category.getPlayers());
        }

        return teams.stream().map(
                t -> t.stream().map(
                        playerMapper::toResponse
                ).toList()
        ).toList();
    }

    @Transactional
    public List<TeamResponse> saveTeams(Long categoryId, TeamsRequest request, Long userId) {
        Category category = categoryFinder.getCategoryOrThrow(categoryId);
        categoryFinder.assertHost(category, userId);
        List<List<PlayerResponse>> teams = request.teams();

        if (teams.size() != category.getPlayers().size() / 2) throw new TeamGenerationException("Incorrect number of teams");

        List<Team> teamsFinal = new ArrayList<>();

        for (List<PlayerResponse> team : teams) {
            PlayerResponse player1 = getPlayerResponse(categoryId, team.getFirst());
            PlayerResponse player2 = getPlayerResponse(categoryId, team.getLast());

            Team newTeamMapped = teamMapper.toEntity(new TeamRequest(categoryId));
            newTeamMapped.setCategory(category);
            teamsFinal.add(newTeamMapped);
            Team newTeam = teamRepository.save(newTeamMapped);

            playerService.addTeam(player1.id(), new AddTeamRequest(newTeam));
            playerService.addTeam(player2.id(), new AddTeamRequest(newTeam));
        }

        List<Team> teamsSaved = teamRepository.saveAll(teamsFinal);

        return teamsSaved.stream().map(teamMapper::toResponse).toList();
    }

    private static @NonNull PlayerResponse getPlayerResponse(Long categoryId, PlayerResponse player) {
        if (!player.category().id().equals(categoryId))
            throw new TeamGenerationException("CategoryId and Player Category Id do not match!");
        if (player.team() != null)
            throw new TeamGenerationException("Player " + player.id() + " is already part of a team");
        return player;
    }

    @Transactional
    public TeamResponseLite createTeam(TeamRequest teamRequest, Long userId) {
        Category category = categoryFinder.getCategoryOrThrow(teamRequest.categoryId());

        if (category.getTournament().getHost().getId().equals(userId)) {
            Team team = teamMapper.toEntity(teamRequest);
            team.setCategory(category);

            Team savedTeam = teamRepository.save(team);
            return teamMapper.toResponseLite(savedTeam);
        }

        throw new ForbiddenException();
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Team team = teamFinder.getTeamOrThrow(id);
        teamFinder.assertHost(team, userId);

        teamRepository.delete(team);
    }
}
