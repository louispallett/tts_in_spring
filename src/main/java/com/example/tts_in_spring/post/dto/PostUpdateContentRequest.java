package com.example.tts_in_spring.post.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PostUpdateContentRequest(
        @NotNull(message = "Content cannot be null")
        @Min(value = 10, message = "Content must be a minimum of 10 characters")
        @Max(value = 10000, message = "Content cannot be more than 10000 characters")
        String content
) {}
