package com.example.tts_in_spring.post.dto;

import jakarta.validation.constraints.*;

public record PostRequest(
        @NotBlank(message = "Title cannot be blank")
        @Size(max = 255, message = "Title cannot be more than 255 characters")
        String title,
        @NotBlank(message = "Content cannot be blank")
        @Size(
                min = 10,
                max = 10000,
                message = "Content must be between 10 and 10000 characters"
        )
        String content,
        @NotNull(message = "tournamentId must not be null")
        Long tournamentId
) {}
