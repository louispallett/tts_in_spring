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

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        return categoryMapper.toResponse(category);
    }

    public CategoryResponseLite createCategory(CategoryRequest categoryRequest) {
        Category validatedCategory = new Category();
        validatedCategory.setName(categoryRequest.getName());
        validatedCategory.setLocked(false);
        validatedCategory.setTournament(categoryRequest.getTournament());

        validatedCategory.setDoubles(!Objects.equals(categoryRequest.getName(), "Mens Singles")
                && !Objects.equals(categoryRequest.getName(), "Womens Singles"));

        Category savedCategory = categoryRepository.save(validatedCategory);
        return categoryMapper.toResponseLite(savedCategory);
    }
}
