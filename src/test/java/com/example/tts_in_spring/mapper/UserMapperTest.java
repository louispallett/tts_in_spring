package com.example.tts_in_spring.mapper;

import com.example.tts_in_spring.builder.UserTestBuilder;
import com.example.tts_in_spring.dto.user.UserResponse;
import com.example.tts_in_spring.dto.user.UserResponseLite;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Import({UserMapperImpl.class})
class UserMapperTest {
    @MockitoBean
    private TournamentMapper tournamentMapper;

    @Autowired
    private UserMapper userMapper;

    @Test
    void toResponse_mapsAllFields() {
        UserResponse response = userMapper.toResponse(UserTestBuilder.aUser().build());

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.firstName()).isEqualTo("John");
        assertThat(response.lastName()).isEqualTo("Doe");
        assertThat(response.email()).isEqualTo("john.doe@example.com");
        assertThat(response.mobCode()).isEqualTo("+44");
        assertThat(response.mobile()).isEqualTo("1234567890");
    }

    @Test
    void toResponseLite_mapsIdentityFields() {
        UserResponseLite lite = userMapper.toResponseLite(UserTestBuilder.aUser().build());

        assertThat(lite.id()).isEqualTo(1L);
        assertThat(lite.firstName()).isEqualTo("John");
        assertThat(lite.lastName()).isEqualTo("Doe");
    }
}
