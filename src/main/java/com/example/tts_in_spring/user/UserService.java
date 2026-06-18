package com.example.tts_in_spring.user;

import com.example.tts_in_spring.security.JwtUtil;
import org.springframework.transaction.annotation.Transactional;
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

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = getUserOrThrow(id);
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponseLite createUser(UserRequest userRequest) {
        String email = userRequest.getEmail().toLowerCase();
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        User newUser = userMapper.toEntity(userRequest);
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        User savedUser = userRepository.save(newUser);
        return userMapper.toResponseLite(savedUser);
    }

    @Transactional
    public UserResponseLite updateDetails(Long id, UserUpdateRequest request) {
        User existingUser = getUserOrThrow(id);

        String email = request.getEmail().toLowerCase();
        if (
                !email.equals(existingUser.getEmail()) &&
                userRepository.findByEmail(email).isPresent()
        ) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        userMapper.updateEntity(request, existingUser);
        existingUser.setEmail(email);

        User savedUser = userRepository.save(existingUser);
        return userMapper.toResponseLite(savedUser);
    }

    @Transactional
    public UserResponseLite updatePassword(Long id, UserUpdatePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password and confirmed password do not match");
        }

        User existingUser = getUserOrThrow(id);

        if (!passwordEncoder.matches(request.getCurrentPassword(), existingUser.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password incorrect");
        }

        existingUser.setPassword(passwordEncoder.encode(request.getNewPassword()));

        User savedUser = userRepository.save(existingUser);
        return userMapper.toResponseLite(savedUser);
    }

    @Transactional(readOnly = true)
    public Object login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail().toLowerCase())
                .filter(u -> passwordEncoder.matches(loginRequest.getPassword(), u.getPassword()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Credentials"));

        String token = jwtUtil.generateToken(user.getId());
        return authMapper.toResponse(token);
    }
}
