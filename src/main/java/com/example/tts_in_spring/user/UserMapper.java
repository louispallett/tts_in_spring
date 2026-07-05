package com.example.tts_in_spring.user;

import com.example.tts_in_spring.tournament.TournamentMapper;
import com.example.tts_in_spring.user.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel =  "spring", uses = {TournamentMapper.class})
public interface UserMapper {
    UserResponse toResponse(User user);

    UserResponseLite toResponseLite(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toEntity(UserRequest userRequest);

    void updateEntity(UserUpdateRequest request, @MappingTarget User user);

    void updateEmailPreferencesEntity(UserEmailPreferencesRequest request, @MappingTarget User user);
}
