package com.example.tts_in_spring.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CompromisedPasswordService {
    private final RestClient pwndPasswordsRestClient;

    public boolean isCompromised(String password) {
        String hash = sha1(password);
        String prefix = hash.substring(0, 5);
        String suffix = hash.substring(5);

        String response = pwndPasswordsRestClient.get()
                .uri(prefix)
                .header("Add-Padding", "true")
                .retrieve()
                .body(String.class);

        if (response == null) {
            return false;
        }

        return response.lines()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .anyMatch(line -> {
                    String[] parts = line.split(":");
                    return parts.length == 2 && parts[0].equalsIgnoreCase(suffix) && Integer.parseInt(parts[1]) > 0;
                });
    }

    private String sha1(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");

            byte[] hash = digest.digest(
                    value.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of()
                    .formatHex(hash)
                    .toUpperCase(Locale.ROOT);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-1 algorithm is not available", e);
        }
    }
}
