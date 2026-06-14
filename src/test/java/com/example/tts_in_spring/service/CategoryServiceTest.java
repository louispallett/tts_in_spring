package com.example.tts_in_spring.service;

import com.example.tts_in_spring.dto.category.CategoryRequest;
import com.example.tts_in_spring.dto.category.CategoryResponse;
import com.example.tts_in_spring.dto.category.CategoryResponseLite;
import com.example.tts_in_spring.dto.tournament.TournamentResponseLite;
import com.example.tts_in_spring.mapper.CategoryMapper;
import com.example.tts_in_spring.model.Category;
import com.example.tts_in_spring.model.Player;
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

    private User buildUser(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private Category buildCategoryWithPlayer(User playerUser) {
        Player player = new Player();
        player.setUser(playerUser);

        Category category = new Category();
        category.setPlayers(List.of(player));

        return category;
    }

    private Category buildCategoryWithHost(User host) {
        Tournament tournament = new Tournament();
        tournament.setHost(host);

        Category category = new Category();
        category.setTournament(tournament);

        return category;
    }

    private Category buildCategory() {
        Tournament tournament = new Tournament();
        Category category = new Category();
        category.setId(100L);
        category.setTournament(tournament);
        category.setName("Mens Singles");
        category.setDoubles(false);
        category.setLocked(false);

        return category;
    }

    private CategoryResponse buildCategoryResponse() {
        return new CategoryResponse(
                100L,
                "Mens Singles",
                false,
                false,
                new TournamentResponseLite(10L, "Test Tournament", false),
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
        Category category = new Category();
        CategoryResponse response = buildCategoryResponse();

        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(categoryMapper.toResponse(category)).thenReturn(response);

        List<CategoryResponse> result = categoryService.getAllCategories();

        assertThat(result).containsExactly(response);
    }

    // In the tests below we create a scenario - a user tries to access this information, etc.
    // Scenario: player from category
    @Test
    void getCategoryById_withPlayer_returnsMappedResponse() {
        User currentUser = buildUser(2L);
        mockAuthenticatedUser(currentUser);

        Category category = buildCategoryWithPlayer(currentUser);
        CategoryResponse response = buildCategoryResponse();

        when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));
        when(categoryMapper.toResponse(category)).thenReturn(response);

        Object result = categoryService.getCategoryById(10L);

        assertThat(result)
                .isInstanceOf(CategoryResponse.class)
                .isEqualTo(response);
    }

    // Scenario: Host of tournament of category
    @Test
    void getCategoryById_withHost_returnsMappedResponse() {
        User host = buildUser(2L);
        mockAuthenticatedUser(host);

        Category category = buildCategoryWithHost(host);
        CategoryResponse response = buildCategoryResponse();

        when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));
        when(categoryMapper.toResponse(category)).thenReturn(response);

        Object result = categoryService.getCategoryById(10L);

        assertThat(result)
                .isInstanceOf(CategoryResponse.class)
                .isEqualTo(response);
    }

    @Test
    void getCategoryById_whenNotFound_throws404() {
        mockAuthenticatedUser(buildUser(1L));

        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategoryById(999L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.NOT_FOUND)
                );

    }

    // Scenario: User not in category or host of tournament
    @Test
    void getCategoryById_whenNotAuthorised_throws403() {
        User outsider = buildUser(3L);
        mockAuthenticatedUser(outsider);

        Category category = buildCategory();

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

        Category saved = new Category();
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
