package com.example.tts_in_spring.match;

import com.example.tts_in_spring.category.Category;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class MatchTest {
    @Test
    void settersAndGetters_workCorrectly() {
        Match m = new Match();
        Category category = new Category();
        Instant date = Instant.now();

        m.setDeadline(date);
        m.setQualifyingMatch(false);
        m.setCategory(category);

        assertThat(m.getDeadline()).isEqualTo(date);
        assertThat(m.isQualifyingMatch()).isFalse();
        assertThat(m.getCategory()).isSameAs(category);
        assertThat(m.getNextMatch()).isNull();
        assertThat(m.getPreviousMatches()).isNotNull();
        assertThat(m.getPreviousMatches()).isEmpty();
        assertThat(m.getParticipants()).isNotNull();
        assertThat(m.getParticipants()).isEmpty();
    }
}
