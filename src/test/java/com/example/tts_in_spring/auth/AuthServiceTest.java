package com.example.tts_in_spring.auth;

import com.example.tts_in_spring.auth.dto.LoginRequest;
import com.example.tts_in_spring.user.User;
import com.example.tts_in_spring.user.UserFinder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock private UserFinder userFinder;

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

        when(userFinder.getUserByEmailWithPasswordOrThrow(request.email(), request.password())).thenReturn(user);

        assertThat(authService.login(request)).isEqualTo(1L);
    }
}
