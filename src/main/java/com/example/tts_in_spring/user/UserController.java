package com.example.tts_in_spring.user;

import com.example.tts_in_spring.security.UserPrincipal;
import com.example.tts_in_spring.user.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {

        return ResponseEntity.ok(userService.getUserById(principal.userId()));
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequest userRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userRequest));
    }

    @PutMapping("/update-details")
    public ResponseEntity<UserResponseLite> updateDetails(
            @Valid @RequestBody UserUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal principal)
    {
        return ResponseEntity.ok(userService.updateDetails(principal.userId(), request));
    }

    @PutMapping("/update-email-preferences")
    public ResponseEntity<UserResponseLite> updateEmailPreferences(
            @Valid @RequestBody UserEmailPreferencesRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(userService.updateEmailPreferences(principal.userId(), request));
    }

    @PatchMapping("/update-password")
    public ResponseEntity<UserResponseLite> updatePassword(
            @Valid @RequestBody UserUpdatePasswordRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(userService.updatePassword(principal.userId(), request));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(
            @Valid @RequestBody DeleteRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        userService.delete(request, principal.userId());
        return ResponseEntity.noContent().build();
    }
}