package com.example.tts_in_spring.service;

import com.example.tts_in_spring.builder.CategoryTestBuilder;
import com.example.tts_in_spring.builder.TournamentTestBuilder;
import com.example.tts_in_spring.builder.UserTestBuilder;
import com.example.tts_in_spring.dto.category.CategoryRequest;
import com.example.tts_in_spring.dto.category.CategoryResponse;
import com.example.tts_in_spring.dto.category.CategoryResponseLite;
import com.example.tts_in_spring.dto.tournament.TournamentResponseLite;
import com.example.tts_in_spring.mapper.CategoryMapper;
import com.example.tts_in_spring.model.Category;
import com.example.tts_in_spring.model.Tournament;
import com.example.tts_in_spring.model.User;
import com.example.tts_in_spring.repository.CategoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void mockAuthenticatedUser(User user) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    private Tournament buildTournament() {
        Tournament tournament = new Tournament();
        tournament.setId(10L);
        return tournament;
    }

    private CategoryResponse buildCategoryResponse() {
        return new CategoryResponse(
                100L,
                "Mens Singles",
                false,
                false,
                new TournamentResponseLite(10L, "Test Tournament", "SIGN_UP", false),
                null,
                null
        );
    }

    private CategoryRequest buildCategoryRequest() {
        CategoryRequest r = new CategoryRequest();
        r.setName("Mens singles");
        r.setDoubles(false);
        r.setTournament(buildTournament());

        return r;
    }

    @Test
    void getAllCategories_returnsMappedList() {
        Category category = CategoryTestBuilder.aCategory().build();
        CategoryResponse response = buildCategoryResponse();

        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(categoryMapper.toResponse(category)).thenReturn(response);

        List<CategoryResponse> result = categoryService.getAllCategories();

        assertThat(result).containsExactly(response);
    }

    @Test
    void getCategoryById_withPlayer_returnsMappedResponse() {
        User currentUser = UserTestBuilder.aUser().withId(2L).build();
        mockAuthenticatedUser(currentUser);

        User host = UserTestBuilder.aUser().build();
        Tournament tournament = TournamentTestBuilder.aTournament().withHost(host).build();
        Category category = CategoryTestBuilder.aCategory().withTournament(tournament).build();
        CategoryResponse response = buildCategoryResponse();

        when(categoryRepository.findById(100L)).thenReturn(Optional.of(category));
        when(categoryMapper.toResponse(category)).thenReturn(response);

        Object result = categoryService.getCategoryById(100L);

        assertThat(result)
                .isInstanceOf(CategoryResponse.class)
                .isEqualTo(response);
    }

    @Test
    void getCategoryById_withHost_returnsMappedResponse() {
        User host = UserTestBuilder.aUser().build();
        mockAuthenticatedUser(host);

        Tournament tournament = TournamentTestBuilder.aTournament().withHost(host).build();
        Category category = CategoryTestBuilder.aCategory().withTournament(tournament).build();
        CategoryResponse response = buildCategoryResponse();

        when(categoryRepository.findById(100L)).thenReturn(Optional.of(category));
        when(categoryMapper.toResponse(category)).thenReturn(response);

        Object result = categoryService.getCategoryById(100L);

        assertThat(result)
                .isInstanceOf(CategoryResponse.class)
                .isEqualTo(response);
    }

    @Test
    void getCategoryById_whenNotFound_throws404() {
        mockAuthenticatedUser(UserTestBuilder.aUser().build());

        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategoryById(999L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.NOT_FOUND)
                );
    }

    @Test
    void getCategoryById_whenNotAuthorised_throws403() {
        User outsider = UserTestBuilder.aUser().withId(3L).build();
        mockAuthenticatedUser(outsider);

        User host = UserTestBuilder.aUser().build();
        Tournament tournament = TournamentTestBuilder.aTournament().withHost(host).build();
        Category category = CategoryTestBuilder.aCategory().withTournament(tournament).build();

        when(categoryRepository.findById(100L)).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> categoryService.getCategoryById(100L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.FORBIDDEN)
                );
    }

    @Test
    void createCategory_savesAndReturnsMappedLite() {
        CategoryRequest request = buildCategoryRequest();

        User host = UserTestBuilder.aUser().build();
        Tournament tournament = TournamentTestBuilder.aTournament().withHost(host).build();
        Category saved = CategoryTestBuilder.aCategory().withTournament(tournament).build();
        CategoryResponseLite lite = new CategoryResponseLite(
                100L,
                "Mens Singles",
                false,
                false
        );

        when(categoryRepository.save(any(Category.class))).thenReturn(saved);
        when(categoryMapper.toResponseLite(saved)).thenReturn(lite);

        assertThat(categoryService.createCategory(request)).isEqualTo(lite);
        verify(categoryRepository).save(any(Category.class));
    }
}
