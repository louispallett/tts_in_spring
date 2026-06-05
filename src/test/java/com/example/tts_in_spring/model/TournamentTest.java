package com.example.tts_in_spring.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TournamentTest {
    @Test
    void defaultConstructor_initializesCollections() {
        Tournament tournament = new Tournament();

        assertThat(tournament.getCategories()).isNotNull();
        assertThat(tournament.getCategories()).isEmpty();

        assertThat(tournament.getPlayers()).isNotNull();
        assertThat(tournament.getPlayers()).isEmpty();
    }

    @Test
    void settersAndGetters_workCorrectly() {
        Tournament tournament = new Tournament();
        User host = new User();

        tournament.setName("Spring Championship");
        tournament.setStage("SIGN_UP");
        tournament.setHost(host);
        tournament.setCode("ABC123");
        tournament.setShowMobile(true);

        assertThat(tournament.getName()).isEqualTo("Spring Championship");
        assertThat(tournament.getStage()).isEqualTo("SIGN_UP");
        assertThat(tournament.getHost()).isSameAs(host);
        assertThat(tournament.getCode()).isEqualTo("ABC123");
        assertThat(tournament.isShowMobile()).isTrue();
    }

    @Test
    void categoriesAndPlayers_canBeAdded() {
        Tournament tournament = new Tournament();

        Category category = new Category();
        Player player = new Player();

        tournament.getCategories().add(category);
        tournament.getPlayers().add(player);

        assertThat(tournament.getCategories())
                .containsExactly(category);

        assertThat(tournament.getPlayers())
                .containsExactly(player);
    }
}