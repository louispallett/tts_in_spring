package com.example.tts_in_spring.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserUpdatePasswordRequest (
    @NotBlank(message = "Current Password is required")
    String currentPassword,

    @NotBlank(message = "New Password is required")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$",
            message = "Password must be at least 8 characters and contain one uppercase letter, one lowercase letter, one number, and one special character"
    )
    String newPassword,

    @NotBlank(message = "Confirm new password is required")
    String confirmNewPassword
) {}
