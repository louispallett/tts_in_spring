package com.example.tts_in_spring.user;

import com.example.tts_in_spring.exception.ConflictException;
import com.example.tts_in_spring.exception.GenericBadRequestException;
import com.example.tts_in_spring.user.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private UserFinder userFinder;

    @InjectMocks
    private UserService userService;

    private UserResponse buildUserResponse() {
        return new UserResponse(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                true,
                null,
                null,
                null,
                null
        );
    }

    private UserRequest buildUserRequest() {
        return new UserRequest(
            " John ",
            " Doe ",
            "john.doe@example.com",
            "Hello123!"
        );
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
    void getAllUsers_whenEmpty_returnsEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        assertThat(userService.getAllUsers()).isEmpty();
    }

    @Test
    void getUserById_whenFound_returnsMappedResponse() {
        User user = UserTestBuilder.aUser().build();
        UserResponse response = buildUserResponse();

        when(userFinder.getUserOrThrow(user.getId())).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        assertThat(userService.getUserById(1L)).isEqualTo(response);
    }

    @Test
    void createUser_whenEmailFree_savesAndReturnsMappedLite() {
        UserRequest request = buildUserRequest();
        User saved = UserTestBuilder.aUser().build();
        UserResponseLite lite = new UserResponseLite(1L, "John", "Doe");
        User mappedUser = UserTestBuilder.aUser().build();

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(saved.getPassword())).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(saved);
        when(userMapper.toResponseLite(saved)).thenReturn(lite);
        when(userMapper.toEntity(request)).thenReturn(mappedUser);


        assertThat(userService.createUser(request)).isEqualTo(lite);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getPassword()).isEqualTo("hashed_password");
        assertThat(captor.getValue().getEmail()).isEqualTo("john.doe@example.com");
        verify(passwordEncoder).encode("Hello123!");
    }

    @Test
    void createUser_whenEmailTaken_throws409() {
        UserRequest request = buildUserRequest();
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(ConflictException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserDetails_whenUser_savesAndReturnsMappedLite() {
        User user = UserTestBuilder.aUser().build();

        UserUpdateRequest request = new UserUpdateRequest(
            "Simon",
            "Smith",
            "Simon.Smith@example.com"
        );

        User updatedUser = UserTestBuilder.aUser()
                .withFirstName("Simon")
                .withLastName("Smith")
                .withEmail("simon.smith@example.com")
                .build();

        UserResponseLite lite = new UserResponseLite(
                1L,
                "Simon",
                "Smith"
        );

        when(userFinder.getUserOrThrow(user.getId())).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toResponseLite(updatedUser)).thenReturn(lite);

        UserResponseLite result = userService.updateDetails(1L, request);

        assertThat(result).isEqualTo(lite);

        verify(userMapper).updateEntity(request, user);
        verify(userRepository).save(user);
        verify(userRepository).findByEmail("simon.smith@example.com");
        verify(userMapper).toResponseLite(updatedUser);
    }

    @Test
    void updatePassword_whenCurrentPasswordCorrect_savesAndReturnsMappedLite() {
        User user = UserTestBuilder.aUser().withPassword("hashed_old_password").build();

        UserUpdatePasswordRequest request = new UserUpdatePasswordRequest(
            "Hello123!",
            "NewPassword1!",
            "NewPassword1!"
        );

        UserResponseLite lite = new UserResponseLite(1L, "John", "Doe");

        when(userFinder.getUserOrThrow(user.getId())).thenReturn(user);
        when(passwordEncoder.matches("Hello123!", "hashed_old_password")).thenReturn(true);
        when(passwordEncoder.encode("NewPassword1!")).thenReturn("new_hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponseLite(user)).thenReturn(lite);

        assertThat(userService.updatePassword(user.getId(), request)).isEqualTo(lite);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getPassword()).isEqualTo("new_hashed_password");
    }

    @Test
    void updatePassword_whenCurrentPasswordIncorrect_throws401() {
        User user = UserTestBuilder.aUser().withPassword("hashed_old_password").build();

        UserUpdatePasswordRequest request = new UserUpdatePasswordRequest(
            "WrongPassword!",
            "NewPassword1!",
            "NewPassword1!"
        );

        when(userFinder.getUserOrThrow(user.getId())).thenReturn(user);
        when(passwordEncoder.matches("WrongPassword!", "hashed_old_password")).thenReturn(false);

        assertThatThrownBy(() -> userService.updatePassword(user.getId(), request))
                .isInstanceOf(GenericBadRequestException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void updatePassword_whenConfirmationDoesNotMatch_throws400() {
        UserUpdatePasswordRequest request = new UserUpdatePasswordRequest(
            "Hello123!",
            "NewPassword1!",
            "Mismatch1!"
        );

        assertThatThrownBy(() -> userService.updatePassword(1L, request))
                .isInstanceOf(GenericBadRequestException.class);

        verify(userRepository, never()).save(any());
    }
}