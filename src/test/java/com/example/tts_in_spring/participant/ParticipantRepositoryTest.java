package com.example.tts_in_spring.participant;

import com.example.tts_in_spring.category.Category;
import com.example.tts_in_spring.category.CategoryRepository;
import com.example.tts_in_spring.match.Match;
import com.example.tts_in_spring.match.MatchRepository;
import com.example.tts_in_spring.player.Player;
import com.example.tts_in_spring.player.PlayerRepository;
import com.example.tts_in_spring.team.Team;
import com.example.tts_in_spring.team.TeamRepository;
import com.example.tts_in_spring.tournament.Tournament;
import com.example.tts_in_spring.tournament.TournamentRepository;
import com.example.tts_in_spring.user.User;
import com.example.tts_in_spring.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class ParticipantRepositoryTest {
    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MatchRepository matchRepository;

    private Team team;
    private Player player;
    private Match match;

    @BeforeEach
    void setUp() {
        User host = new User("John", "Doe", "john.doe@example.com", "secret", "44", "123456789", List.of());
        User user = new User("Simon", "Smith", "simon.smith@example.com", "secret", "44", "987654321", List.of());
        userRepository.save(host);
        userRepository.save(user);

        Tournament tournament = new Tournament();
        tournament.setName("Test");
        tournament.setStage("SIGN_UP");
        tournament.setHost(host);
        tournament.setCode("ABC123");
        tournament.setShowMobile(true);
        tournamentRepository.save(tournament);

        Category category = new Category();
        category.setName("Mens Singles");
        category.setLocked(false);
        category.setDoubles(false);
        category.setTournament(tournament);
        categoryRepository.save(category);

        team = new Team();
        team.setCategory(category);
        teamRepository.save(team);

        player = new Player();
        player.setMale(true);
        player.setSeeded(false);
        player.setRank(3);
        player.setUser(user);
        player.setCategory(category);
        player.setTeam(team);
        playerRepository.save(player);

        match = new Match();
        match.setQualifyingMatch(false);
        match.setTournamentRoundText("5");
        match.setState("SCHEDULED");
        match.setDate(Instant.now());
        match.setUpdateNumber(0);
        match.setCategory(category);
        matchRepository.save(match);
    }

    @Test
    void save_savesParticipantTeamSuccessfully() {
        Participant participant = new Participant();
        participant.setResultText("");
        participant.setWinner(false);
        participant.setStatus("");
        participant.setTeam(team);
        participant.setMatch(match);

        Participant saved = participantRepository.save(participant);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getResultText()).isEqualTo("");
        assertThat(saved.isWinner()).isFalse();
        assertThat(saved.getStatus()).isEqualTo("");
        assertThat(saved.getMatch()).isSameAs(match);
        assertThat(saved.getTeam()).isSameAs(team);
    }

    @Test
    void save_savesParticipantPlayerSuccessfully() {
        Participant participant = new Participant();
        participant.setResultText("");
        participant.setWinner(false);
        participant.setStatus("");
        participant.setPlayer(player);
        participant.setMatch(match);

        Participant saved = participantRepository.save(participant);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getResultText()).isEqualTo("");
        assertThat(saved.isWinner()).isFalse();
        assertThat(saved.getStatus()).isEqualTo("");
        assertThat(saved.getMatch()).isSameAs(match);
        assertThat(saved.getPlayer()).isSameAs(player);
    }

    @Test
    void save_throwsException_whenRequiredFieldsMissing() {
        Participant participant = new Participant();

        assertThatThrownBy(() -> {
            participantRepository.saveAndFlush(participant);
        }).isInstanceOf(Exception.class);
    }

}
