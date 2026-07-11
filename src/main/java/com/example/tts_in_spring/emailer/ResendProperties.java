package com.example.tts_in_spring.emailer;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "resend")
public record ResendProperties(
        String key,
        String from,
        String replyTo
) {}
