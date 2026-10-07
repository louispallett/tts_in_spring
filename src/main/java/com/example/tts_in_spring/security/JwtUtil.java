package com.example.tts_in_spring.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    @Getter
    private final long expiration;

    public JwtUtil(
            @Value("${jwt.private-key-path}") Resource privateKeyResource,
            @Value("${jwt.public-key-path}") Resource publicKeyResource,
            @Value("${jwt.expiration}") long expiration
    ) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        this.privateKey = loadPrivateKey(privateKeyResource);
        this.publicKey = loadPublicKey(publicKeyResource);
        this.expiration = expiration;
    }

    public String generateToken(Long userId) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    public Long extractUserId(String token) {
        return Long.valueOf(getClaims(token).getSubject());
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = getClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    /** Exposed so a controller can serve the public key / build a JWKS response. */
    public PublicKey getPublicKey() {
        return publicKey;
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private PrivateKey loadPrivateKey(Resource resource)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String key = readPem(resource, "PRIVATE KEY");
        byte[] decoded = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    private PublicKey loadPublicKey(Resource resource)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String key = readPem(resource, "PUBLIC KEY");
        byte[] decoded = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    private String readPem(Resource resource, String type) throws IOException {
        try (InputStream is = resource.getInputStream()) {
            String pem = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return pem
                    .replace("-----BEGIN " + type + "-----", "")
                    .replace("-----END " + type + "-----", "")
                    .replaceAll("\\s", "");
        }
    }
}