package com.example.tts_in_spring.category;

import com.example.tts_in_spring.exception.ResourceNotFoundException;
import com.example.tts_in_spring.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryFinder {
    private final CategoryRepository categoryRepository;

    public Category getCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category " + id + " not found"));
    }

    public void assertHost(Category category, Long userId) {
        if (!category.getTournament().getHost().getId().equals(userId)) {
            throw new ForbiddenException(
                    "Not host of tournament "
                            + category.getTournament().getName()
                            + " ("
                            + category.getTournament().getId()
                            + ")"
            );
        }
    }
}
