package com.example.tts_in_spring.controller;

import com.example.tts_in_spring.dto.category.CategoryResponse;
import com.example.tts_in_spring.dto.player.PlayerResponse;
import com.example.tts_in_spring.dto.tournament.TournamentResponse;
import com.example.tts_in_spring.dto.user.UserResponse;
import com.example.tts_in_spring.mapper.CategoryMapper;
import com.example.tts_in_spring.model.Category;
import com.example.tts_in_spring.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @GetMapping
    // TODO: Uncomment for prod
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = categoryRepository.findAll()
                .stream()
                .map(c -> categoryMapper.toResponse(c))
                .toList();

        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable Long id) {
        Category category = categoryRepository.findById(id).orElseThrow();

        return ResponseEntity.ok(categoryMapper.toResponse(category));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createCategory(@RequestBody Category incomingCategory) {
        Category savedCategory = categoryRepository.save(incomingCategory);

        return categoryRepository.findById(savedCategory.getId())
                .map(category -> ResponseEntity.status(HttpStatus.CREATED).body(categoryMapper.toResponse(category)))
                .orElse(ResponseEntity.notFound().build());
    }
}