package com.example.tts_in_spring.category;

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
public class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserRepository userRepository;

    private Tournament tournament;

    @BeforeEach
    void setUp() {
        User host = new User("John", "Doe", "john.doe@example.com", "secret", "44", "123456789", null, null, List.of());
        userRepository.save(host);

        tournament = new Tournament();
        tournament.setName("Test Tournament");
        tournament.setStage("SIGN_UP");
        tournament.setCode("1234567");
        tournament.setShowMobile(true);
        tournament.setHost(host);
        tournament.setCategories(List.of());

        tournamentRepository.save(tournament);
    }

    @Test
    void save_savesCategorySuccessfully() {
        Category category = new Category();
        category.setName("Mens Singles");
        category.setLocked(false);
        category.setDoubles(false);
        category.setTournament(tournament);

        Category saved = categoryRepository.save(category);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Mens Singles");
        assertThat(saved.getTournament()).isSameAs(tournament);
        assertThat(saved.isLocked()).isFalse();
        assertThat(saved.isDoubles()).isFalse();
    }

    @Test
    void save_throwsException_whenRequiredFieldsMissing() {
        Category category = new Category();

        assertThatThrownBy(() -> {
            categoryRepository.saveAndFlush(category);
        }).isInstanceOf(Exception.class);
    }

    @Test
    void findById_returnsEmpty_whenCategoryDoesNotExist() {
        Optional<Category> result = categoryRepository.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void save_persistsRelationships() {
        Category category = new Category();
        category.setName("Mens Singles");
        category.setLocked(false);
        category.setDoubles(false);
        category.setTournament(tournament);

        Category saved = categoryRepository.saveAndFlush(category);

        assertThat(saved.getPlayers()).isEmpty();
        assertThat(saved.getMatches()).isEmpty();
    }
}
