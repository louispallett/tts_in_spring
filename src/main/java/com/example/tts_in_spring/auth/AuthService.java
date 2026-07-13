package com.example.tts_in_spring.auth;

import com.example.tts_in_spring.auth.dto.LoginRequest;
import com.example.tts_in_spring.config.AppProperties;
import com.example.tts_in_spring.emailer.EmailerService;
import com.example.tts_in_spring.exception.InvalidTokenException;
import com.example.tts_in_spring.exception.UnauthorizedException;
import com.example.tts_in_spring.password_reset_token.PasswordResetToken;
import com.example.tts_in_spring.password_reset_token.PasswordResetTokenRepository;
import com.example.tts_in_spring.password_reset_token.PasswordResetTokenService;
import com.example.tts_in_spring.user.User;
import com.example.tts_in_spring.user.UserRepository;
import com.resend.core.exception.ResendException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordResetTokenService passwordResetTokenService;
    private final EmailerService emailerService;
    private final AppProperties appProperties;

    @Transactional(readOnly = true)
    public Long login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email().toLowerCase())
                .filter(u -> passwordEncoder.matches(loginRequest.password(), u.getPassword()))
                .orElseThrow(() -> new UnauthorizedException("Invalid Credentials"));

        return user.getId();
    }

    @Async
    @Transactional
    public void requestPasswordReset(String email) {
        Optional<User> userOpt = userRepository.findByEmailAndDeletedFalse(email);

        if (userOpt.isEmpty())
            return;

        User user = userOpt.get();

        passwordResetTokenRepository.invalidateAllForUser(user.getId());

        String rawToken = passwordResetTokenService.generateRawToken();
        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setToken(passwordResetTokenService.hashToken(rawToken));
        token.setExpiresAt(Instant.now().plus(30, ChronoUnit.MINUTES));
        passwordResetTokenRepository.save(token);

        try {
            emailerService.sendEmail(
                    email,
                    "Password Reset Request",
                    "Dear " + user.getFirstName()
                            + ",\nPlease click <a href\""
                            + appProperties.frontendUrl()
                            + "/reset-password?token="
                            + rawToken
                            + "\">here</a> to reset your password."
            );
        } catch (ResendException e) {
            log.error("Failed to send password reset email to user {}", user.getId(), e);
        }
    }

    @Transactional
    public void resetPassword(String rawToken, String newPassword) {
        String hash = passwordResetTokenService.hashToken(rawToken);
        PasswordResetToken token = passwordResetTokenRepository.findByToken(hash)
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired token"));

        if (token.isUsed() || token.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidTokenException("Invalid or expired token");
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        token.setUsed(true);
        passwordResetTokenRepository.save(token);
    }
}
