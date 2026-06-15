package com.example.tts_in_spring.player;

import com.example.tts_in_spring.category.Category;
import com.example.tts_in_spring.user.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PlayerTest {
   @Test
    void settersAndGetters_workCorrectly() {
       Player player = new Player();
       User user = new User();
       Category category = new Category();

       player.setMale(true);
       player.setRank(2);
       player.setSeeded(false);
       player.setUser(user);
       player.setCategory(category);

       assertThat(player.isMale()).isTrue();
       assertThat(player.getRank()).isEqualTo(2);
       assertThat(player.isSeeded()).isFalse();
       assertThat(player.getUser()).isSameAs(user);
       assertThat(player.getCategory()).isSameAs(category);
   }
}
