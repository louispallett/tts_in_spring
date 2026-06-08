package com.example.tts_in_spring.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class MatchTest {
    @Test
    void settersAndGetters_workCorrectly() {
        Match m = new Match();
        Category category = new Category();
        Instant date = Instant.now();

        m.setState("SCHEDULED");
        m.setDate(date);
        m.setUpdateNumber(0);
        m.setQualifyingMatch(false);
        m.setCategory(category);

        assertThat(m.getState()).isEqualTo("SCHEDULED");
        assertThat(m.getDate()).isEqualTo(date);
        assertThat(m.getUpdateNumber()).isEqualTo(0);
        assertThat(m.isQualifyingMatch()).isFalse();
        assertThat(m.getCategory()).isSameAs(category);
        assertThat(m.getNextMatch()).isNull();
        assertThat(m.getPreviousMatches()).isNotNull();
        assertThat(m.getPreviousMatches()).isEmpty();
        assertThat(m.getParticipants()).isNotNull();
        assertThat(m.getParticipants()).isEmpty();
    }
}
