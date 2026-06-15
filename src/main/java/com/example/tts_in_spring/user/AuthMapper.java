package com.example.tts_in_spring.user;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    AuthResponse toResponse(String token);
}
