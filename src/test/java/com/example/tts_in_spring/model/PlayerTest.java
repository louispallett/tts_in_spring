package com.example.tts_in_spring.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PlayerTest {
   @Test
    void settersAndGetters_workCorrectly() {
       Player player = new Player();
       Tournament tournament = new Tournament();
       User user = new User();
       Category category = new Category();

       player.setMale(true);
       player.setRank(2);
       player.setSeeded(false);
       player.setTournament(tournament);
       player.setUser(user);
       player.setCategory(category);

       assertThat(player.isMale()).isTrue();
       assertThat(player.getRank()).isEqualTo(2);
       assertThat(player.isSeeded()).isFalse();
       assertThat(player.getTournament()).isSameAs(tournament);
       assertThat(player.getUser()).isSameAs(user);
       assertThat(player.getCategory()).isSameAs(category);
   }
}
