package com.example.tts_in_spring.controller;

import com.example.tts_in_spring.dto.CategoryResponse;
import com.example.tts_in_spring.dto.PlayerResponse;
import com.example.tts_in_spring.dto.TournamentResponse;
import com.example.tts_in_spring.dto.UserResponse;
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

    private CategoryResponse mapToResponse(Category category) {
        CategoryResponse categoryResponse = new CategoryResponse(category);

        categoryResponse.tournament = new TournamentResponse(category.getTournament());
        categoryResponse.tournament.host = new UserResponse(category.getTournament().getHost());

        categoryResponse.players = Optional.ofNullable(category.getPlayers())
                .orElse(List.of())
                .stream()
                .map(player -> {
                    PlayerResponse response = new PlayerResponse(player);

                    if (player.getUser() != null) {
                        response.user = new UserResponse(player.getUser());
                    }

                    return response;
                })
                .toList();

        return categoryResponse;
    }

    @GetMapping
    // TODO: Uncomment for prod
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = categoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable Long id) {
        return categoryRepository.findById(id)
                .map(c -> ResponseEntity.ok(mapToResponse(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<?> createCategory(@RequestBody Category incomingCategory) {
        Category savedCategory = categoryRepository.save(incomingCategory);

        return categoryRepository.findById(savedCategory.getId())
                .map(category -> ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(category)))
                .orElse(ResponseEntity.notFound().build());
    }
}