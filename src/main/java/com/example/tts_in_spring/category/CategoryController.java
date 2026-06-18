package com.example.tts_in_spring.category;

import com.example.tts_in_spring.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

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
}