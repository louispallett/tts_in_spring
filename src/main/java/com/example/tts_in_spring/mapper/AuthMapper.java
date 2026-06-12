package com.example.tts_in_spring.mapper;

import com.example.tts_in_spring.dto.auth.AuthResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    AuthResponse toResponse(String token);
}
