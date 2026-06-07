package com.example.tts_in_spring.repository;

import com.example.tts_in_spring.model.Tournament;
import com.example.tts_in_spring.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class TournamentRepositoryTest {
    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserRepository userRepository;

    private User host;

    @BeforeEach
    void setUp() {
        host = new User("John", "Doe", "john.doe@example.com", "secret", "44", "123456789", List.of());
        userRepository.save(host);
    }

    @Test
    void save_savesTournamentSuccessfully() {
        Tournament tournament = new Tournament();
        tournament.setName("Spring Cup");
        tournament.setStage("SIGN_UP");
        tournament.setHost(host);
        tournament.setCode("ABC123");
        tournament.setShowMobile(true);
        tournament.setCategories(List.of());

        Tournament saved = tournamentRepository.save(tournament);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Spring Cup");
        assertThat(saved.getStage()).isEqualTo("SIGN_UP");
        assertThat(saved.getHost()).isEqualTo(host);
        assertThat(saved.getCode()).isEqualTo("ABC123");
        assertThat(saved.isShowMobile()).isTrue();
    }

    @Test
    void save_throwsException_whenRequiredFieldsMissing() {
        Tournament tournament = new Tournament();

        assertThatThrownBy(() -> {
            tournamentRepository.saveAndFlush(tournament);
        }).isInstanceOf(Exception.class);
    }

    @Test
    void findById_returnsEmpty_whenTournamentDoesNotExist() {
        Optional<Tournament> result = tournamentRepository.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void save_persistsRelationships() {
        Tournament tournament = new Tournament();
        tournament.setName("Rel Test");
        tournament.setStage("DRAW");
        tournament.setHost(host);
        tournament.setCode("REL001");
        tournament.setShowMobile(true);

        Tournament saved = tournamentRepository.saveAndFlush(tournament);

        assertThat(saved.getCategories()).isEmpty();
    }
}
