package com.example.tts_in_spring.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "password-checker")
public record PasswordCheckerProperties(String baseUrl) {}
