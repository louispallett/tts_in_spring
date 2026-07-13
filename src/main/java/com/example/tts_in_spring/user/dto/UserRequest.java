package com.example.tts_in_spring.user.dto;

import jakarta.validation.constraints.*;

public record UserRequest (
    @NotBlank(message = "First name is required")
    @Size(max = 255, message = "Cannot be longer than 255 characters")
    String firstName,
    @NotBlank(message = "Last name is required")
    String lastName,
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String email,
    // Regex:
    // - (?=.*[a-z]) --> At least one lowercase
    // - (?=.*[A-Z]) --> At least one uppercase
    // - (?=.*\\d) --> At least one number/digit
    // - (?=.*[!@#$%^&*...]) --> At least one special character
    // - .{8,} --> At least 8 characters total
    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$",
            message = "Password must be at least 8 characters and contain one uppercase letter, one lowercase letter, one number, and one special character"
    )
    String password
) {}