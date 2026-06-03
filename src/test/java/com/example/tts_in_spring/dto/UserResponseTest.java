package com.example.tts_in_spring.dto;

import com.example.tts_in_spring.model.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserResponseTest {

    @Test
    void constructor_mapsBasicFields() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@example.com");
        user.setMobCode("44");
        user.setMobile("123456789");

        UserResponse response = new UserResponse(user);

        assertThat(response.id).isEqualTo(1L);
        assertThat(response.firstName).isEqualTo("John");
        assertThat(response.lastName).isEqualTo("Doe");
        assertThat(response.email).isEqualTo("john@example.com");
        assertThat(response.mobCode).isEqualTo("44");
        assertThat(response.mobile).isEqualTo("123456789");
        // tournaments is intentionally left for controller to populate
        assertThat(response.tournaments).isNull();
    }
}
