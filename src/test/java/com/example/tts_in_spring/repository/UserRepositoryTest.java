package com.example.tts_in_spring.repository;

import com.example.tts_in_spring.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void save_throwsException_whenEmailAlreadyExists() {
        User user1 = new User(
                "John",
                "Doe",
                "john@example.com",
                "secret",
                "44",
                "123456789",
                List.of()
        );

        User user2 = new User(
                "Jane",
                "Smith",
                "john@example.com",
                "secret",
                "44",
                "987654321",
                List.of()
        );

        userRepository.save(user1);

        assertThatThrownBy(() -> {
            userRepository.saveAndFlush(user2);
        }).isInstanceOf(Exception.class);
    }

    @Test
    void findByEmail_returnsEmpty_whenUserDoesNotExist() {
        Optional<User> result =
                userRepository.findByEmail("missing@example.com");

        assertThat(result).isEmpty();
    }
}