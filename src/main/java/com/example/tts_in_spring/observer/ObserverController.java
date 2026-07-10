package com.example.tts_in_spring.observer;

import com.example.tts_in_spring.observer.dto.ObserverResponse;
import com.example.tts_in_spring.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/observer")
public class ObserverController {
    private final ObserverService observerService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ObserverResponse>> getAllObservers() {
        return ResponseEntity.ok(observerService.getAllObservers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObserverResponse> getObserver(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(observerService.getObserverById(id, user.userId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteObserver(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        observerService.delete(id, user.userId());
        return ResponseEntity.noContent().build();
    }
}
