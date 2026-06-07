package com.example.tts_in_spring.model;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TeamTest {
    @Test
    void settersAndGetters_workCorrectly() {
        Category category = new Category();
        Team t = new Team();

        t.setCategory(category);

        assertThat(t.getCategory()).isSameAs(category);
        assertThat(t.getPlayers()).isNotNull();
    }
}
