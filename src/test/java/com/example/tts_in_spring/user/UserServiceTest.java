package com.example.tts_in_spring.user;

import com.example.tts_in_spring.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AuthMapper authMapper;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    private UserResponse buildUserResponse() {
        return new UserResponse(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                "+44",
                "1234567890",
                null
        );
    }

    private UserRequest buildUserRequest() {
        UserRequest r = new UserRequest();
        r.setFirstName("John");
        r.setLastName("Doe");
        r.setEmail("john.doe@example.com");
        r.setPassword("Hello123!");
        r.setMobCode("+44");
        r.setMobile("123456789");
        return r;
    }

    @Test
    void getAllUsers_returnsMappedList() {
        User user = UserTestBuilder.aUser().build();
        UserResponse response = buildUserResponse();

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toResponse(user)).thenReturn(response);

        List<UserResponse> result = userService.getAllUsers();

        assertThat(result).containsExactly(response);
    }

    @Test
    void getUserById_whenFound_returnsMappedResponse() {
        User user = UserTestBuilder.aUser().build();
        UserResponse response = buildUserResponse();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(response);

        assertThat(userService.getUserById(1L)).isEqualTo(response);
    }

    @Test
    void getUserById_whenNotFound_throws404() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void createUser_whenEmailFree_savesAndReturnsMappedLite() {
        UserRequest request = buildUserRequest();
        User saved = new User();
        UserResponseLite lite = new UserResponseLite(1L, "John", "Doe");

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Hello123!")).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(saved);
        when(userMapper.toResponseLite(saved)).thenReturn(lite);

        assertThat(userService.createUser(request)).isEqualTo(lite);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_whenEmailTaken_throws409() {
        UserRequest request = buildUserRequest();
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.CONFLICT));

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_withValidCredentials_returnsAuthResponse() {
        LoginRequest request = new LoginRequest();
        request.setEmail("john.doe@example.com");
        request.setPassword("Hello123!");
        User user = new User();
        user.setEmail("john.doe@example.com");
        user.setPassword("hashed_password");
        AuthResponse authResponse = new AuthResponse("asbcsdefsg");

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Hello123!", "hashed_password")).thenReturn(true);
        when(jwtUtil.generateToken("john.doe@example.com")).thenReturn("jwt-token");
        when(authMapper.toResponse("jwt-token")).thenReturn(authResponse);

        assertThat(userService.login(request)).isEqualTo(authResponse);
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

        assertThatThrownBy(() -> userService.login(request))
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

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.UNAUTHORIZED));
    }
}
