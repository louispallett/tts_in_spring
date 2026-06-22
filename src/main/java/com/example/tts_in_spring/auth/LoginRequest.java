package com.example.tts_in_spring.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Must be valid email")
    private String email;

    @NotBlank(message = "Password must not be blank")
    private String password;
}
