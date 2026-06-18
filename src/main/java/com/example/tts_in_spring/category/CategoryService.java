package com.example.tts_in_spring.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    private Category getCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    public CategoryResponse getCategoryById(Long id, Long userId) {
        Category category = getCategoryOrThrow(id);

        if (userId.equals(category.getTournament().getHost().getId())) return categoryMapper.toResponse(category);

        boolean isPlayer = category.getPlayers().stream()
                .anyMatch(player -> player.getUser().getId().equals(userId));

        if (isPlayer) return categoryMapper.toResponse(category);

        throw(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    public CategoryResponseLite createCategory(CategoryRequest categoryRequest, Long userId) {
        if (categoryRequest.getTournament().getHost().getId().equals(userId)) {
            Category category = categoryMapper.toEntity(categoryRequest);
            category.setLocked(false);

            category.setDoubles(!Objects.equals(categoryRequest.getName(), "Men's Singles")
                    && !Objects.equals(categoryRequest.getName(), "Women's Singles"));

            Category savedCategory = categoryRepository.save(category);
            return categoryMapper.toResponseLite(savedCategory);
        }

        throw(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    public CategoryResponseLite updateLocked(Long id, CategoryLockedUpdateRequest request, Long userId) {
        Category category = getCategoryOrThrow(id);

        if (userId.equals(category.getTournament().getHost().getId())) {
            categoryMapper.updateLockedEntity(request, category);

            Category savedCategory = categoryRepository.save(category);
            return categoryMapper.toResponseLite(savedCategory);
        }

        throw(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }
}
