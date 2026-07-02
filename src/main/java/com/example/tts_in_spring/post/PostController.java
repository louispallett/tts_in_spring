package com.example.tts_in_spring.post;

import com.example.tts_in_spring.post.dto.*;
import com.example.tts_in_spring.security.UserPrincipal;
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
@RequestMapping("/api/post")
public class PostController {
    private final PostService postService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(postService.getPostById(id, user.userId()));
    }

    @PostMapping
    public ResponseEntity<?> createPost(
            @Valid @RequestBody PostRequest request,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(request, user.userId()));
    }

    @PatchMapping("/{id}/update-title")
    public ResponseEntity<PostResponseLite> updateTitle(
            @PathVariable Long id,
            @Valid @RequestBody PostUpdateTitleRequest request,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(postService.updateTitle(id, request, user.userId()));
    }

    @PatchMapping("/{id}/update-content")
    public ResponseEntity<PostResponseLite> updateContent(
            @PathVariable Long id,
            @Valid @RequestBody PostUpdateContentRequest request,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(postService.updateContent(id, request, user.userId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        postService.delete(id, user.userId());
        return ResponseEntity.noContent().build();
    }
}
