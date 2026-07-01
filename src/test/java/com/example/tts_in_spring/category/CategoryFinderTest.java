package com.example.tts_in_spring.category;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryFinderTest {
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryFinder categoryFinder;

    @Test
    void getCategoryOrThrow_whenCategoryExists_returnsCategory() {
        Category category = CategoryTestBuilder.aCategory().build();

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        assertThat(categoryFinder.getCategoryOrThrow(category.getId())).isEqualTo(category);
    }

    @Test
    void getCategoryOrThrow_whenCategoryDoesNotExist_throws404() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryFinder.getCategoryOrThrow(999L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void assertHost_whenHost_doesNotThrow() {
        Category category = CategoryTestBuilder.aCategory().build();

        assertThatCode(() -> categoryFinder.assertHost(category, category.getTournament().getHost().getId()))
                .doesNotThrowAnyException();
    }

    @Test
    void assertHost_whenNotHost_throws403() {
        assertThatThrownBy(() -> categoryFinder.assertHost(CategoryTestBuilder.aCategory().build(), 3L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.FORBIDDEN));
    }
}