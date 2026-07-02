package com.example.tts_in_spring.post.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record PostUpdateTitleRequest(
        @NotNull(message = "Title cannot be null")
        @NotEmpty(message = "Title cannot be empty")
        String title
) {}
