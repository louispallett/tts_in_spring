package com.example.tts_in_spring.controller;

import com.example.tts_in_spring.dto.*;
import com.example.tts_in_spring.model.User;
import com.example.tts_in_spring.repository.UserRepository;
import com.example.tts_in_spring.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
    // TODO: Uncomment for prod
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return ResponseEntity.ok(users);
    }

    /* Request restricted current user */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        User principal = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return userRepository.findById(principal.getId())
                .map(u -> ResponseEntity.ok(mapToResponse(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequest userRequest) {
        // Check if email already used
        if (userRepository.findByEmail(userRequest.getEmail().toLowerCase()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Email already registered");
        }

        User validatedUser = new User();
        validatedUser.setFirstName(userRequest.getFirstName());
        validatedUser.setLastName(userRequest.getLastName());
        validatedUser.setEmail(userRequest.getEmail().toLowerCase());
        validatedUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        validatedUser.setMobCode(userRequest.getMobCode());
        validatedUser.setMobile(userRequest.getMobile());

        User savedUser = userRepository.save(validatedUser);
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