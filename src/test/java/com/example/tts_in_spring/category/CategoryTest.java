package com.example.tts_in_spring.category;

import com.example.tts_in_spring.player.Player;
import com.example.tts_in_spring.tournament.Tournament;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoryTest {
    @Test
    void defaultConstructor_initializesCollections() {
        Category category = new Category();

        assertThat(category.getPlayers()).isNotNull();
        assertThat(category.getMatches()).isNotNull();
    }

    @Test
    void settersAndGetters_workCorrectly() {
        Category category = new Category();
        Tournament tournament = new Tournament();

        category.setName("Mens Singles");
        category.setDoubles(false);
        category.setLocked(false);
        category.setTournament(tournament);

        assertThat(category.getName()).isEqualTo("Mens Singles");
        assertThat(category.isDoubles()).isFalse();
        assertThat(category.isLocked()).isFalse();
        assertThat(category.getTournament()).isSameAs(tournament);
    }

    @Test
    void players_canBeAdded() {
        Category category = new Category();
        Player player = new Player();

        category.getPlayers().add(player);

        assertThat(category.getPlayers()).containsExactly(player);
    }
}
