package com.example.tts_in_spring.user;

import com.example.tts_in_spring.auth.dto.LoginRequest;
import com.example.tts_in_spring.exception.ResourceNotFoundException;
import com.example.tts_in_spring.exception.UnauthorizedException;
import com.example.tts_in_spring.user.dto.DeleteRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserFinderTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserFinder userFinder;

    @Test
    void getUserOrThrow_whenUserExists_returnsUser() {
        User user = UserTestBuilder.aUser().build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertThat(userFinder.getUserOrThrow(user.getId())).isEqualTo(user);
    }

    @Test
    void getUserOrThrow_whenUserDoesNotExist_throws404() {
        when(userRepository.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userFinder.getUserOrThrow(9L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getUserByEmailWithPasswordOrThrow_whenCorrectPassword_returnsUser() {
        LoginRequest request = new LoginRequest(
                "john.doe@example.com",
                "Hello123!"
        );

        User user = new User();
        user.setId(1L);
        user.setEmail("john.doe@example.com");
        user.setPassword("hashed_password");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(true);

        assertThat(userFinder.getUserByEmailWithPasswordOrThrow(request.email(), request.password())).isEqualTo(user);
    }

    @Test
    void getUserByEmailWithPasswordOrThrow_whenIncorrectPassword_throws403() {
        LoginRequest request = new LoginRequest(
                "john.doe@example.com",
                "Hello123!"
        );

        User user = new User();
        user.setId(1L);
        user.setEmail("john.doe@example.com");
        user.setPassword("hashed_password");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> userFinder.getUserByEmailWithPasswordOrThrow(request.email(), request.password()))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void getUserByIdWithPasswordOrThrow_whenCorrectPassword_returnsUser() {
        DeleteRequest request = new DeleteRequest(
                "Hello123!"
        );

        User user = new User();
        user.setId(1L);
        user.setEmail("john.doe@example.com");
        user.setPassword("hashed_password");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(true);

        assertThat(userFinder.getUserByIdWithPasswordOrThrow(user.getId(), request.password())).isEqualTo(user);
    }

    @Test
    void getUserByIdWithPasswordOrThrow_whenIncorrectPassword_throws403() {
        DeleteRequest request = new DeleteRequest(
                "Incorrect_password!"
        );

        User user = new User();
        user.setId(1L);
        user.setEmail("john.doe@example.com");
        user.setPassword("hashed_password");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> userFinder.getUserByIdWithPasswordOrThrow(user.getId(), request.password()))
                .isInstanceOf(UnauthorizedException.class);
    }
}
