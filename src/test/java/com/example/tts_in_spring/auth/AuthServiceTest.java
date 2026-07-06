package com.example.tts_in_spring.auth;

import com.example.tts_in_spring.auth.dto.LoginRequest;
import com.example.tts_in_spring.exception.UnauthorizedException;
import com.example.tts_in_spring.user.User;
import com.example.tts_in_spring.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_withValidCredentials_returnsAuthResponse() {
        LoginRequest request = new LoginRequest(
                "john.doe@example.com",
                "Hello123!"
        );
        User user = new User();
        user.setId(1L);
        user.setEmail("john.doe@example.com");
        user.setPassword("hashed_password");

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Hello123!", "hashed_password")).thenReturn(true);

        assertThat(authService.login(request)).isEqualTo(1L);
    }

    @Test
    void login_withInvalidPassword_throws401() {
        LoginRequest request = new LoginRequest(
                "john.doe@example.com",
                "WrongPassword123!"
        );
        User user = new User();
        user.setPassword("hashed_password");

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPassword123!", "hashed_password")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void login_withUnknownEmail_throws401() {
        LoginRequest request = new LoginRequest(
                "random@random.com",
                "AnyString123!"
        );
        when(userRepository.findByEmail("random@random.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class);
    }
}
