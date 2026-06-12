package com.example.tts_in_spring.mapper;

import com.example.tts_in_spring.dto.user.UserResponseLite;
import com.example.tts_in_spring.dto.user.UserResponse;
import com.example.tts_in_spring.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel =  "spring", uses = {TournamentMapper.class})
public interface UserMapper {
    UserResponse toResponse(User user);

    UserResponseLite toResponseLite(User user);
}
