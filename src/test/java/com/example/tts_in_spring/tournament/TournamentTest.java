package com.example.tts_in_spring.tournament;

import com.example.tts_in_spring.category.Category;
import com.example.tts_in_spring.user.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TournamentTest {
    @Test
    void defaultConstructor_initializesCollections() {
        Tournament tournament = new Tournament();

        assertThat(tournament.getCategories()).isNotNull();
        assertThat(tournament.getCategories()).isEmpty();
    }

    @Test
    void settersAndGetters_workCorrectly() {
        Tournament tournament = new Tournament();
        User host = new User();

        tournament.setName("Spring Championship");
        tournament.setStage(Stage.REGISTRATION);
        tournament.setHost(host);
        tournament.setCode("ABC123");
        tournament.setShowMobile(true);

        assertThat(tournament.getName()).isEqualTo("Spring Championship");
        assertThat(tournament.getStage()).isEqualTo(Stage.REGISTRATION);
        assertThat(tournament.getHost()).isSameAs(host);
        assertThat(tournament.getCode()).isEqualTo("ABC123");
        assertThat(tournament.isShowMobile()).isTrue();
    }

    @Test
    void categories_canBeAdded() {
        Tournament tournament = new Tournament();

        Category category = new Category();

        tournament.getCategories().add(category);

        assertThat(tournament.getCategories())
                .containsExactly(category);
    }
}