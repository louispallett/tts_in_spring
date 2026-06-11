package com.example.tts_in_spring.dto.User;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    // Regex:
    // - (?=.*[a-z]) --> At least one lowercase
    // - (?=.*[A-Z]) --> At least one uppercase
    // - (?=.*\\d) --> At least one number/digit
    // - (?=.*[!@#$%^&*...]) --> At least one special character
    // - .{8,} --> At least 8 characters total
    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$",
            message = "Password must be at least 8 characters and contain one uppercase letter, one lowercase letter, one number, and one special character"
    )
    private String password;

    @NotBlank(message = "Mobile country code is required")
    @Pattern(regexp = "\\+\\d{1,4}", message = "Mobile code must be in the format +XX or +XXX")
    private String mobCode;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "\\d{7,15}", message = "Mobile number must be between 7 and 15 digits")
    private String mobile;
}