package com.example.tts_in_spring.user;

import com.example.tts_in_spring.tournament.TournamentMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel =  "spring", uses = {TournamentMapper.class})
public interface UserMapper {
    UserResponse toResponse(User user);

    UserResponseLite toResponseLite(User user);
}
