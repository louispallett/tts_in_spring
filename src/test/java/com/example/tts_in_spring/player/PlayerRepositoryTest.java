package com.example.tts_in_spring.player;

import com.example.tts_in_spring.category.Category;
import com.example.tts_in_spring.category.CategoryRepository;
import com.example.tts_in_spring.tournament.Stage;
import com.example.tts_in_spring.tournament.Tournament;
import com.example.tts_in_spring.user.BuildUser;
import com.example.tts_in_spring.user.User;
import com.example.tts_in_spring.tournament.TournamentRepository;
import com.example.tts_in_spring.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class PlayerRepositoryTest {
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User user;
    private Category category;

    @BeforeEach
    void setUp() {
        User host = BuildUser.buildUser();
        user = BuildUser.buildUser();
        user.setFirstName("Simon");
        user.setLastName("Smith");
        user.setEmail("simon.smith@example.com");
        user.setDeleted(false);
        userRepository.save(host);

        Tournament tournament = new Tournament();
        tournament.setName("Test");
        tournament.setStage(Stage.REGISTRATION);
        tournament.setHost(host);
        tournament.setCode("ABC123");
        tournament.setShowMobile(true);

        category = new Category();
        category.setName("Mens Singles");
        category.setLocked(false);
        category.setDoubles(false);
        category.setTournament(tournament);

        userRepository.save(user);
        tournamentRepository.save(tournament);
        categoryRepository.save(category);
    }

    @Test
    void save_savesPlayerSuccessfully() {
        Player player = new Player();
        player.setMale(true);
        player.setSeeded(false);
        player.setRank(3);
        player.setMobCode("+44");
        player.setMobile("1234567890");
        player.setUser(user);
        player.setCategory(category);

       Player saved = playerRepository.save(player);

       assertThat(saved.getId()).isNotNull();
       assertThat(saved.isMale()).isTrue();
       assertThat(saved.isSeeded()).isFalse();
       assertThat(saved.getRank()).isEqualTo(3);
       assertThat(saved.getMobCode()).isEqualTo("+44");
       assertThat(saved.getMobile()).isEqualTo("1234567890");
       assertThat(saved.getUser()).isSameAs(user);
       assertThat(saved.getCategory()).isSameAs(category);
    }

    @Test
    void save_throwsException_whenRequiredFieldsMissing() {
        Player player = new Player();

        assertThatThrownBy(() -> {
            playerRepository.saveAndFlush(player);
        }).isInstanceOf(Exception.class);
    }

    @Test
    void findById_returnsEmpty_whenPlayerDoesNotExist() {
        Optional<Player> result = playerRepository.findById(999L);
        assertThat(result).isEmpty();
    }

    @Test
    void save_persistsRelationships() {
        Player player = new Player();
        player.setMale(true);
        player.setSeeded(false);
        player.setRank(3);
        player.setMobCode("+44");
        player.setMobile("1234567890");
        player.setUser(user);
        player.setCategory(category);

        Player saved = playerRepository.save(player);

        assertThat(saved.getParticipants()).isEmpty();

    }
}
