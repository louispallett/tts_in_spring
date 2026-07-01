package com.example.tts_in_spring.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserFinderTest {
    @Mock
    private UserRepository userRepository;

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
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.NOT_FOUND));
    }
}
