package com.example.tts_in_spring.model;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;

class BaseTest {
    static class TestEntity extends Base {
        void triggerCreate() {
            super.onCreate();
        }
    }

    @Test
    void onCreate_setsDateCreatedOnce() {
        TestEntity entity = new TestEntity();

        assertThat(entity.getDateCreated()).isNull();

        entity.triggerCreate();
        Instant first = entity.getDateCreated();

        assertThat(first).isNotNull();
    }
}
