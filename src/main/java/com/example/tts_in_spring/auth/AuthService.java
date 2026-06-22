package com.example.tts_in_spring.auth;

import com.example.tts_in_spring.security.JwtUtil;
import com.example.tts_in_spring.user.User;
import com.example.tts_in_spring.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email().toLowerCase())
                .filter(u -> passwordEncoder.matches(loginRequest.password(), u.getPassword()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Credentials"));

        String token = jwtUtil.generateToken(user.getId());
        return new AuthResponse(token);
    }
}
