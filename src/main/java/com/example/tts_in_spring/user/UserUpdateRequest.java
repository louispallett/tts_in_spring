package com.example.tts_in_spring.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Mobile country code is required")
    @Pattern(regexp = "\\+\\d{1,4}", message = "Mobile code must be in the format +XX or +XXX")
    private String mobCode;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "\\d{7,15}", message = "Mobile number must be between 7 and 15 digits")
    private String mobile;
}
