package com.example.tts_in_spring.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class UserTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void defaultConstructor_initializesTournamentsList() {
        User user = new User();

        assertThat(user.getTournaments()).isNotNull();
        assertThat(user.getTournaments()).isEmpty();
    }

    @Test
    void allArgsConstructor_setsFields() {
        User user = new User(
                "John",
                "Doe",
                "john@example.com",
                "secret",
                "44",
                "123456789",
                List.of()
        );

        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getEmail()).isEqualTo("john@example.com");
        assertThat(user.getPassword()).isEqualTo("secret");
        assertThat(user.getMobCode()).isEqualTo("44");
        assertThat(user.getMobile()).isEqualTo("123456789");
    }

    @Test
    void password_isWriteOnlyInJson() throws Exception {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@example.com");
        user.setPassword("secret");
        user.setMobCode("44");
        user.setMobile("123456789");

        String json = objectMapper.writeValueAsString(user);

        assertThat(json).doesNotContain("password");

        String jsonWithPassword = """
                {
                  "firstName": "John",
                  "lastName": "Doe",
                  "email": "john@example.com",
                  "password": "secret",
                  "mobCode": "44",
                  "mobile": "123456789"
                }
                """;

        User deserialized = objectMapper.readValue(jsonWithPassword, User.class);
        assertThat(deserialized.getPassword()).isEqualTo("secret");
    }
}
