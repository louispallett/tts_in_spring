package com.example.tts_in_spring.user;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public User getUserOrThrow(Long id) {
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
        String firstName = userRequest.firstName().trim();
        String lastName = userRequest.lastName().trim();
        String mobCode = userRequest.mobCode().trim();
        String mobile = userRequest.mobile().trim();
        String email = userRequest.email().trim().toLowerCase(Locale.ROOT);

        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        User newUser = userMapper.toEntity(userRequest);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setMobCode(mobCode);
        newUser.setMobile(mobile);
        newUser.setPassword(passwordEncoder.encode(userRequest.password()));

        User savedUser = userRepository.save(newUser);
        return userMapper.toResponseLite(savedUser);
    }

    @Transactional
    public UserResponseLite updateDetails(Long id, UserUpdateRequest request) {
        User existingUser = getUserOrThrow(id);

        String email = request.email().toLowerCase();
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
        if (!request.newPassword().equals(request.confirmNewPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password and confirmed password do not match");
        }

        User existingUser = getUserOrThrow(id);

        if (!passwordEncoder.matches(request.currentPassword(), existingUser.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password incorrect");
        }

        existingUser.setPassword(passwordEncoder.encode(request.newPassword()));

        User savedUser = userRepository.save(existingUser);
        return userMapper.toResponseLite(savedUser);
    }

}
