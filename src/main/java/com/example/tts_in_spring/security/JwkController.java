package com.example.tts_in_spring.security;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
public class JwkController {

    private final JwtUtil jwtUtil;

    public JwkController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @GetMapping(value = "/.well-known/jwt-public-key.pem", produces = "text/plain")
    public String getPublicKeyPem() {
        String encoded = Base64.getMimeEncoder(64, "\n".getBytes())
                .encodeToString(jwtUtil.getPublicKey().getEncoded());
        return "-----BEGIN PUBLIC KEY-----\n" + encoded + "\n-----END PUBLIC KEY-----\n";
    }
}