package com.example.tts_in_spring.service;

import com.example.tts_in_spring.dto.auth.LoginRequest;
import com.example.tts_in_spring.dto.user.UserRequest;
import com.example.tts_in_spring.dto.user.UserResponse;
import com.example.tts_in_spring.dto.user.UserResponseLite;
import com.example.tts_in_spring.mapper.AuthMapper;
import com.example.tts_in_spring.mapper.UserMapper;
import com.example.tts_in_spring.model.User;
import com.example.tts_in_spring.repository.UserRepository;
import com.example.tts_in_spring.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AuthMapper authMapper;

    @Autowired
    private JwtUtil jwtUtil;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return userMapper.toResponse(user);
    }

    public UserResponseLite createUser(UserRequest userRequest) {
        if (userRepository.findByEmail(userRequest.getEmail().toLowerCase()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        User validatedUser = new User();
        validatedUser.setFirstName(userRequest.getFirstName());
        validatedUser.setLastName(userRequest.getLastName());
        validatedUser.setEmail(userRequest.getEmail().toLowerCase());
        validatedUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        validatedUser.setMobCode(userRequest.getMobCode());
        validatedUser.setMobile(userRequest.getMobile());

        User savedUser = userRepository.save(validatedUser);
        return userMapper.toResponseLite(savedUser);
    }

    public Object login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail().toLowerCase())
                .filter(u -> passwordEncoder.matches(loginRequest.getPassword(), u.getPassword()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Credentials"));

        String token = jwtUtil.generateToken(user.getEmail());
        return authMapper.toResponse(token);
    }
}
