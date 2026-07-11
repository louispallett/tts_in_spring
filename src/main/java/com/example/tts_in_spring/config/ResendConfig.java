package com.example.tts_in_spring.config;

import com.example.tts_in_spring.emailer.ResendProperties;
import com.resend.Resend;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResendConfig {
    @Bean
    public Resend resend(ResendProperties properties) {
        return new Resend(properties.key());
    }
}
