package com.example.tts_in_spring.user;

import com.example.tts_in_spring.tournament.TournamentMapper;
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

    @Test
    void toEntity_mapsAllowedFieldsOnly() {
        UserRequest request = new UserRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("Hello123!");
        request.setMobCode("+44");
        request.setMobile("1234567890");

        User user = userMapper.toEntity(request);

        assertThat(user).isNotNull();

        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getMobCode()).isEqualTo("+44");
        assertThat(user.getMobile()).isEqualTo("1234567890");

        assertThat(user.getId()).isNull();
        assertThat(user.getEmail()).isNull();
        assertThat(user.getPassword()).isNull();
    }

    @Test
    void updateEntity_updatesAllowedFieldsOnly() {
        User user = UserTestBuilder.aUser().build();

        UserUpdateRequest request = new UserUpdateRequest();
        request.setFirstName("Simon");
        request.setLastName("Smith");
        request.setEmail("simon.smith@example.com");
        request.setMobCode("+1");
        request.setMobile("987654321");

        userMapper.updateEntity(request, user);

        assertThat(user.getFirstName()).isEqualTo("Simon");
        assertThat(user.getLastName()).isEqualTo("Smith");
        assertThat(user.getEmail()).isEqualTo("simon.smith@example.com");
        assertThat(user.getMobCode()).isEqualTo("+1");
        assertThat(user.getMobile()).isEqualTo("987654321");

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getPassword()).isEqualTo("Hello123!");
    }
}
