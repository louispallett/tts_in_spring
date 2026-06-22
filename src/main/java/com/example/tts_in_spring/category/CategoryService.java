package com.example.tts_in_spring.category;

import com.example.tts_in_spring.category.dto.CategoryLockedUpdateRequest;
import com.example.tts_in_spring.category.dto.CategoryRequest;
import com.example.tts_in_spring.category.dto.CategoryResponse;
import com.example.tts_in_spring.category.dto.CategoryResponseLite;
import com.example.tts_in_spring.tournament.Tournament;
import com.example.tts_in_spring.tournament.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final TournamentService tournamentService;

    public Category getCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
    }

    private void assertHost(Category category, Long userId) {
        if (!category.getTournament().getHost().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id, Long userId) {
        Category category = getCategoryOrThrow(id);

        if (userId.equals(category.getTournament().getHost().getId())) return categoryMapper.toResponse(category);

        boolean isPlayer = category.getPlayers().stream()
                .anyMatch(player -> player.getUser().getId().equals(userId));

        if (isPlayer) return categoryMapper.toResponse(category);

        throw(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @Transactional
    public CategoryResponseLite createCategory(CategoryRequest categoryRequest, Long userId) {
        Tournament tournament = tournamentService.getTournamentOrThrow(categoryRequest.tournamentId());

        if (tournament.getHost().getId().equals(userId)) {
            Category category = categoryMapper.toEntity(categoryRequest);
            category.setTournament(tournament);
            category.setLocked(false);

            category.setDoubles(!Objects.equals(categoryRequest.name(), "Men's Singles")
                    && !Objects.equals(categoryRequest.name(), "Women's Singles"));

            Category savedCategory = categoryRepository.save(category);
            return categoryMapper.toResponseLite(savedCategory);
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    @Transactional
    public CategoryResponseLite updateLocked(Long id, CategoryLockedUpdateRequest request, Long userId) {
        Category category = getCategoryOrThrow(id);
        assertHost(category, userId);

        categoryMapper.updateLockedEntity(request, category);

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponseLite(savedCategory);
    }
}
