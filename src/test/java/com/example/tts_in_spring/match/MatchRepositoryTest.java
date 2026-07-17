package com.example.tts_in_spring.match;

import com.example.tts_in_spring.category.Category;
import com.example.tts_in_spring.category.CategoryRepository;
import com.example.tts_in_spring.category.Type;
import com.example.tts_in_spring.tournament.Stage;
import com.example.tts_in_spring.tournament.Tournament;
import com.example.tts_in_spring.tournament.TournamentRepository;
import com.example.tts_in_spring.user.BuildUser;
import com.example.tts_in_spring.user.User;
import com.example.tts_in_spring.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class MatchRepositoryTest {
    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MatchRepository matchRepository;

    private Category category;

    @BeforeEach
    void setUp() {
        User host = BuildUser.buildUser();
        userRepository.save(host);

        Tournament tournament = new Tournament();
        tournament.setName("Test");
        tournament.setStage(Stage.REGISTRATION);
        tournament.setHost(host);
        tournament.setCode("ABC123");
        tournament.setShowMobile(true);
        tournamentRepository.save(tournament);

        category = new Category();
        category.setName(Type.MEN_SINGLES);
        category.setDoubles(false);
        category.setTournament(tournament);
        categoryRepository.save(category);
    }

    @Test
    void save_savesMatchSuccessfully() {
        Match match = new Match();
        Instant date = Instant.now();
        match.setTournamentRoundText("5");
        match.setDeadline(date);
        match.setQualifyingMatch(false);
        match.setCategory(category);

        Match saved = matchRepository.save(match);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTournamentRoundText()).isEqualTo("5");
        assertThat(saved.getDeadline()).isEqualTo(date);
        assertThat(saved.isQualifyingMatch()).isFalse();
        assertThat(saved.getCategory()).isSameAs(category);
    }

    @Test
    void save_throwsException_whenRequiredFieldsMissing() {
        Match match = new Match();

        assertThatThrownBy(() -> {
            matchRepository.saveAndFlush(match);
        }).isInstanceOf(Exception.class);
    }
}
