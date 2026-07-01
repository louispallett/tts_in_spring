package com.example.tts_in_spring.user;

import com.example.tts_in_spring.exception.ConflictException;
import com.example.tts_in_spring.exception.GenericBadRequestException;
import com.example.tts_in_spring.user.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserFinder userFinder;


    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userFinder.getUserOrThrow(id);
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
            throw new ConflictException("Email " + email + " is already registered");
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
        User existingUser = userFinder.getUserOrThrow(id);

        String email = request.email().toLowerCase();
        if (
                !email.equals(existingUser.getEmail()) &&
                userRepository.findByEmail(email).isPresent()
        ) {
            throw new ConflictException("Email " + email + " is already registered");
        }

        userMapper.updateEntity(request, existingUser);
        existingUser.setEmail(email);

        User savedUser = userRepository.save(existingUser);
        return userMapper.toResponseLite(savedUser);
    }

    @Transactional
    public UserResponseLite updatePassword(Long id, UserUpdatePasswordRequest request) {
        if (!request.newPassword().equals(request.confirmNewPassword())) {
            throw new GenericBadRequestException("New password and confirmed password do not match");
        }

        User existingUser = userFinder.getUserOrThrow(id);

        if (!passwordEncoder.matches(request.currentPassword(), existingUser.getPassword())) {
            throw new GenericBadRequestException("Current password incorrect");
        }

        existingUser.setPassword(passwordEncoder.encode(request.newPassword()));

        User savedUser = userRepository.save(existingUser);
        return userMapper.toResponseLite(savedUser);
    }

    // Delete User route wipes user personal data from database whilst maintaining their row. This is critical to maintain
    // things like tournament and match results.
    @Transactional
    public void delete(Long id) {
        User user = userFinder.getUserOrThrow(id);

        user.setDeleted(true);
        user.setDeletedAt(Instant.now());

        user.setFirstName("Deleted");
        user.setLastName("User");
        user.setEmail(UUID.randomUUID() + "@deleted.local");
        user.setMobile("");
        user.setMobCode("");
        user.setPassword("");
        user.setActive(false);

        userRepository.save(user);
    }
}
