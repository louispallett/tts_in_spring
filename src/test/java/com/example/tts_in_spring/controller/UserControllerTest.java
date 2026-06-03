package com.example.tts_in_spring.controller;

import com.example.tts_in_spring.dto.AuthResponse;
import com.example.tts_in_spring.dto.LoginRequest;
import com.example.tts_in_spring.dto.UserResponse;
import com.example.tts_in_spring.model.User;
import com.example.tts_in_spring.repository.UserRepository;
import com.example.tts_in_spring.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserController userController;

    private User createUser(Long id, String email) {
        User u = new User();
        u.setId(id);
        u.setFirstName("John");
        u.setLastName("Doe");
        u.setEmail(email);
        u.setPassword("encoded");
        u.setMobCode("44");
        u.setMobile("123456789");
        return u;
    }

    @Test
    void getAllUsers_returnsMappedResponses() {
        User u1 = createUser(1L, "john@example.com");
        User u2 = createUser(2L, "jane@example.com");
        when(userRepository.findAll()).thenReturn(List.of(u1, u2));

        ResponseEntity<List<UserResponse>> response = userController.getAllUsers();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).id).isEqualTo(1L);
        assertThat(response.getBody().get(0).email).isEqualTo("john@example.com");
        assertThat(response.getBody().get(1).id).isEqualTo(2L);
        assertThat(response.getBody().get(1).email).isEqualTo("jane@example.com");
    }

    @Test
    void getUser_returnsUserWhenExists() {
        User u = createUser(1L, "john@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(u));

        ResponseEntity<UserResponse> response = userController.getUser(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id).isEqualTo(1L);
        assertThat(response.getBody().email).isEqualTo("john@example.com");
    }

    @Test
    void getUser_returnsNotFoundWhenMissing() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<UserResponse> response = userController.getUser(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void getUserByEmail_returnsUserWhenExists() {
        User u = createUser(1L, "john@example.com");
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(u));

        ResponseEntity<UserResponse> response = userController.getUserByEmail("john@example.com");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id).isEqualTo(1L);
        assertThat(response.getBody().email).isEqualTo("john@example.com");
    }

    @Test
    void getUserByEmail_returnsNotFoundWhenMissing() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

        ResponseEntity<UserResponse> response = userController.getUserByEmail("john@example.com");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void createUser_createsUserWithLowercasedEmailAndEncodedPassword() {
        String rawPassword = "MySecret123!";
        String encodedPassword = "$2a$10$encoded";

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        User incoming = new User();
        incoming.setFirstName("John");
        incoming.setLastName("Doe");
        incoming.setEmail("john@example.com");
        incoming.setPassword(rawPassword);
        incoming.setMobCode("44");
        incoming.setMobile("123456789");

        User saved = createUser(1L, "john@example.com");
        when(userRepository.save(any(User.class))).thenReturn(saved);

        ResponseEntity<?> response = userController.createUser(incoming);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isInstanceOf(UserResponse.class);

        UserResponse body = (UserResponse) response.getBody();
        assertThat(body.id).isEqualTo(1L);
        assertThat(body.email).isEqualTo("john@example.com");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User toSave = captor.getValue();

        assertThat(toSave.getEmail()).isEqualTo("john@example.com"); // lowercased
        assertThat(toSave.getPassword()).isEqualTo(encodedPassword);
    }

    @Test
    void createUser_rejectsDuplicateEmail() {
        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.of(createUser(1L, "john@example.com")));

        User incoming = new User();
        incoming.setFirstName("John");
        incoming.setLastName("Doe");
        incoming.setEmail("john@example.com");
        incoming.setPassword("secret");
        incoming.setMobCode("44");
        incoming.setMobile("123456789");

        ResponseEntity<?> response = userController.createUser(incoming);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_returnsJwtOnValidCredentials() {
        String email = "john@example.com";
        String rawPassword = "MySecret123!";
        String encodedPassword = "$2a$10$encoded";
        String token = "jwt-token";

        User user = createUser(1L, email);
        user.setPassword(encodedPassword);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
        when(jwtUtil.generateToken(email)).thenReturn(token);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(rawPassword);

        ResponseEntity<?> response = userController.login(loginRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(AuthResponse.class);

        AuthResponse auth = (AuthResponse) response.getBody();
        assertThat(auth.getToken()).isEqualTo(token);
    }

    @Test
    void login_returnsUnauthorizedOnInvalidCredentials() {
        String email = "john@example.com";
        String rawPassword = "wrong";

        User user = createUser(1L, email);
        user.setPassword("$2a$10$encoded");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, user.getPassword())).thenReturn(false);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(rawPassword);

        ResponseEntity<?> response = userController.login(loginRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
