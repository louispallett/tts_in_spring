package com.example.tts_in_spring.repository;

import com.example.tts_in_spring.model.Tournament;
import com.example.tts_in_spring.model.User;
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

    @Test
    void save_savesTournamentSuccessfully() {
        User host = new User(
                "John",
                "Doe",
                "john@example.com",
                "secret",
                "44",
                "123456789",
                List.of()
        );
        userRepository.save(host);

        Tournament tournament = new Tournament();
        tournament.setName("Spring Cup");
        tournament.setStage("SIGN_UP");
        tournament.setHost(host);
        tournament.setCode("ABC123");
        tournament.setShowMobile(true);
        tournament.setCategories(List.of());
        tournament.setPlayers(List.of());

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
    void save_persistsEnumCorrectly() {
        User host = new User(
                "Alice",
                "Smith",
                "alice@example.com",
                "secret",
                "44",
                "987654321",
                java.util.List.of()
        );
        userRepository.save(host);

        Tournament tournament = new Tournament();
        tournament.setName("Enum Test");
        tournament.setStage("PLAY");
        tournament.setHost(host);
        tournament.setCode("XYZ789");
        tournament.setShowMobile(false);

        Tournament saved = tournamentRepository.saveAndFlush(tournament);

        Optional<Tournament> found = tournamentRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getStage()).isEqualTo("PLAY");
    }

    @Test
    void save_persistsRelationships() {
        User host = new User(
                "Bob",
                "Marley",
                "bob@example.com",
                "secret",
                "44",
                "555555555",
                java.util.List.of()
        );
        userRepository.save(host);

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
