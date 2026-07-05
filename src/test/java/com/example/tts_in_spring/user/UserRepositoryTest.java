package com.example.tts_in_spring.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void save_throwsException_whenEmailAlreadyExists() {
        User user1 = BuildUser.buildUser();

        User user2 = BuildUser.buildUser();
        user2.setFirstName("Jane");
        user2.setLastName("Smith");

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