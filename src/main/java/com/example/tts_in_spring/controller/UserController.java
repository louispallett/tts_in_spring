package com.example.tts_in_spring.controller;

import com.example.tts_in_spring.dto.AuthResponse;
import com.example.tts_in_spring.dto.LoginRequest;
import com.example.tts_in_spring.dto.TournamentResponse;
import com.example.tts_in_spring.dto.UserResponse;
import com.example.tts_in_spring.model.User;
import com.example.tts_in_spring.repository.UserRepository;
import com.example.tts_in_spring.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private UserResponse mapToResponse(User user) {
        UserResponse userResponse = new UserResponse(user);

        userResponse.tournaments = user.getTournaments().stream().map(i -> {
            TournamentResponse r = new TournamentResponse(i);
            return r;
        }).toList();

        return userResponse;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(u -> ResponseEntity.ok(mapToResponse(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        return userRepository.findByEmail(email.toLowerCase())
                .map(t -> ResponseEntity.ok(mapToResponse(t)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody User incomingUser) {

        // Check if email already used
        if (userRepository.findByEmail(incomingUser.getEmail().toLowerCase()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Email already registered");
        }

        incomingUser.setEmail(incomingUser.getEmail().toLowerCase());

        // Hashing password
        String hashedPassword = passwordEncoder.encode((incomingUser.getPassword()));
        incomingUser.setPassword(hashedPassword);

        User savedUser = userRepository.save(incomingUser);

        return userRepository.findById(savedUser.getId())
                .map(user -> ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        var userOptional = userRepository.findByEmail(loginRequest.getEmail().toLowerCase());

        if (
                userOptional.isPresent() &&
                        passwordEncoder.matches(loginRequest.getPassword(), userOptional.get().getPassword())
        ) {
            String token = jwtUtil.generateToken(userOptional.get().getEmail());
            return ResponseEntity.ok(new AuthResponse(token));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
    }
}