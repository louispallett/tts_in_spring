package com.example.tts_in_spring.post;

import com.example.tts_in_spring.tournament.Stage;
import com.example.tts_in_spring.tournament.Tournament;
import com.example.tts_in_spring.tournament.TournamentRepository;
import com.example.tts_in_spring.user.User;
import com.example.tts_in_spring.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
public class PostRepositoryTest {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserRepository userRepository;

    private Tournament tournament;

    @BeforeEach
    void setUp() {
        User host = new User("John", "Doe", "john.doe@example.com", "secret", "44", "123456789", false, null, null, List.of(), List.of());
        userRepository.save(host);

        tournament = new Tournament();
        tournament.setName("Test Tournament");
        tournament.setStage(Stage.REGISTRATION);
        tournament.setCode("1234567");
        tournament.setShowMobile(true);
        tournament.setHost(host);
        tournament.setCategories(List.of());

        tournamentRepository.save(tournament);
    }

    @Test
    void save_savesPostSuccessfully() {
        Post post = new Post();
        post.setTitle("Test title");
        post.setContent("Test Content.");
        post.setTournament(tournament);

        Post saved = postRepository.save(post);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Test title");
        assertThat(saved.getContent()).isEqualTo("Test Content.");
        assertThat(saved.getTournament()).isSameAs(tournament);
    }

    @Test
    void save_throwsException_whenRequiredFieldsMissing() {
        Post post = new Post();

        assertThatThrownBy(() -> postRepository.saveAndFlush(post))
                .isInstanceOf(Exception.class);
    }
}
