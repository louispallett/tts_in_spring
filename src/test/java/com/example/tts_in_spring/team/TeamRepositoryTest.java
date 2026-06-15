package com.example.tts_in_spring.team;

import com.example.tts_in_spring.category.Category;
import com.example.tts_in_spring.category.CategoryRepository;
import com.example.tts_in_spring.tournament.Tournament;
import com.example.tts_in_spring.tournament.TournamentRepository;
import com.example.tts_in_spring.user.User;
import com.example.tts_in_spring.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
public class TeamRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    private Category category;

    @BeforeEach
    void setUp() {
        User host = new User("John", "Doe", "john.doe@example.com", "secret", "44", "123456789", List.of());
        userRepository.save(host);

        Tournament tournament = new Tournament();
        tournament.setName("Test");
        tournament.setStage("SIGN_UP");
        tournament.setHost(host);
        tournament.setCode("ABC123");
        tournament.setShowMobile(true);
        tournamentRepository.save(tournament);

        category = new Category();
        category.setName("Mens Doubles");
        category.setLocked(false);
        category.setDoubles(true);
        category.setTournament(tournament);
        categoryRepository.save(category);
    }

    @Test
    void save_savesTeamSuccessfully() {
       Team team = new Team();
       team.setCategory(category);

       Team saved = teamRepository.save(team);

       assertThat(saved.getCategory()).isSameAs(category);
       assertThat(saved.getPlayers()).isNotNull();
       assertThat(saved.getParticipants()).isNotNull();
    }

    @Test
    void save_throwsException_whenRequiredFieldsMissing() {
        Team team = new Team();

        assertThatThrownBy(() -> {
            teamRepository.saveAndFlush(team);
        }).isInstanceOf(Exception.class);
    }

    @Test
    void findById_returnsEmpty_whenTeamDoesNotExist() {
        Optional<Team> result = teamRepository.findById(999L);
        assertThat(result).isEmpty();
    }

    @Test
    void save_persistsRelationships() {
        Team team = new Team();
        team.setCategory(category);

        Team saved = teamRepository.save(team);

        assertThat(saved.getPlayers()).isEmpty();
        assertThat(saved.getParticipants()).isEmpty();
    }
}
