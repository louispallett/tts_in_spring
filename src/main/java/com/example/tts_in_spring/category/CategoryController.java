package com.example.tts_in_spring.category;

import com.example.tts_in_spring.category.dto.CategoryLockedUpdateRequest;
import com.example.tts_in_spring.category.dto.CategoryRequest;
import com.example.tts_in_spring.category.dto.CategoryResponse;
import com.example.tts_in_spring.category.dto.CategoryResponseLite;
import com.example.tts_in_spring.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/category")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    // TODO: Uncomment for prod
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategory(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(categoryService.getCategoryById(id, user.userId()));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createCategory(
            @RequestBody CategoryRequest categoryRequest,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(categoryRequest, user.userId()));
    }

    @PatchMapping("/{id}/update-locked")
    public ResponseEntity<CategoryResponseLite> updateLocked(
            @PathVariable Long id,
            @RequestBody CategoryLockedUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(categoryService.updateLocked(id, request, principal.userId()));
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        categoryService.delete(id, principal.userId());
        return ResponseEntity.noContent().build();
    }
}