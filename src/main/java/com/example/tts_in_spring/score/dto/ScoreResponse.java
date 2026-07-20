package com.example.tts_in_spring.score.dto;

import com.example.tts_in_spring.user.dto.UserResponseLite;

import java.time.Instant;

public record ScoreResponse(
        Long id,
        Instant dateCreated,
        UserResponseLite submittedBy
) {}
