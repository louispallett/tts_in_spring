package com.example.tts_in_spring.dto.tournament;

import com.example.tts_in_spring.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.security.SecureRandom;

@Getter
@Setter
public class TournamentRequest {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz0123456789";

    private static String generateRandomString() {
        int len = 12;
        StringBuilder sb = new StringBuilder(len);

        for (int i = 0; i < len; i++) {
            sb.append(ALPHABET.charAt(SECURE_RANDOM.nextInt(ALPHABET.length())));
        }

        return sb.toString();
    }

    public String generateCode(String name) {
        String code;
        String[] words = name.split("\\s+");

        String firstWord = words[0];

        if (firstWord.length() < 8) {
           code = firstWord + "_" + generateRandomString();
        } else {
           String truncatedWord = firstWord.substring(0, 8);
           code = truncatedWord + "_" + generateRandomString();
        }

        return code;
    }

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Stage is required")
    private String stage;

    @NotNull(message = "Host is must not be null")
    private User host;

    private String code;

    @NotNull(message = "Show Mobile boolean must not be null")
    private boolean showMobile;

    public void setCode(String name) {
        this.code = generateCode(name);
    }
}

