package com.example.tts_in_spring.auth;

import com.example.tts_in_spring.security.JwtUtil;
import com.example.tts_in_spring.user.User;
import com.example.tts_in_spring.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private AuthMapper authMapper;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_withValidCredentials_returnsAuthResponse() {
        LoginRequest request = new LoginRequest();
        request.setEmail("john.doe@example.com");
        request.setPassword("Hello123!");
        User user = new User();
        user.setId(1L);
        user.setEmail("john.doe@example.com");
        user.setPassword("hashed_password");
        AuthResponse authResponse = new AuthResponse("asbcsdefsg");

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Hello123!", "hashed_password")).thenReturn(true);
        when(jwtUtil.generateToken(user.getId())).thenReturn("jwt-token");
        when(authMapper.toResponse("jwt-token")).thenReturn(authResponse);

        assertThat(authService.login(request)).isEqualTo(authResponse);
        verify(jwtUtil).generateToken(user.getId());
    }

    @Test
    void login_withInvalidPassword_throws401() {
        LoginRequest request = new LoginRequest();
        request.setEmail("john.doe@example.com");
        request.setPassword("WrongPassword123!");
        User user = new User();
        user.setPassword("hashed_password");

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPassword123!", "hashed_password")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.UNAUTHORIZED));
    }

    @Test
    void login_withUnknownEmail_throws401() {
        LoginRequest request = new LoginRequest();
        request.setEmail("random@random.com");
        request.setPassword("AnyString123!");
        when(userRepository.findByEmail("random@random.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.UNAUTHORIZED));
    }
}
