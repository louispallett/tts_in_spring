package com.example.tts_in_spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class TtsInSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(TtsInSpringApplication.class, args);
    }

}
