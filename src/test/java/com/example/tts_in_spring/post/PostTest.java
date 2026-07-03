package com.example.tts_in_spring.post;

import com.example.tts_in_spring.tournament.Tournament;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PostTest {
    @Test
    void settersAndGetters_workCorrectly() {
        Post post = new Post();
        Tournament tournament = new Tournament();

        post.setTournament(tournament);
        post.setTitle("Test title");
        post.setContent("Test content for test title.");

        assertThat(post.getTitle()).isEqualTo("Test title");
        assertThat(post.getContent()).isEqualTo("Test content for test title.");
        assertThat(post.getTournament()).isSameAs(tournament);
    }
}
