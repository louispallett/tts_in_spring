package com.example.tts_in_spring.player.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PlayerRequest (
    @NotNull(message = "Male boolean cannot be null")
    boolean male,
    @NotBlank(message = "Mobile country code is required")
    @Pattern(regexp = "\\+\\d{1,4}", message = "Mobile code must be in the format +XX or +XXX")
    String mobCode,
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "\\d{7,15}", message = "Mobile number must be between 7 and 15 digits")
    String mobile,
    @NotNull(message = "categoryId cannot be null")
    Long categoryId
) {}
