package com.example.tts_in_spring.auth;

import com.example.tts_in_spring.auth.dto.LoginRequest;
import com.example.tts_in_spring.exception.UnauthorizedException;
import com.example.tts_in_spring.user.User;
import com.example.tts_in_spring.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Long login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email().toLowerCase())
                .filter(u -> passwordEncoder.matches(loginRequest.password(), u.getPassword()))
                .orElseThrow(() -> new UnauthorizedException("Invalid Credentials"));

        return user.getId();
    }
}
