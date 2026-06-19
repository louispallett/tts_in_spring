package com.example.tts_in_spring.category;

import com.example.tts_in_spring.player.Player;
import com.example.tts_in_spring.player.PlayerTestBuilder;
import com.example.tts_in_spring.tournament.TournamentService;
import com.example.tts_in_spring.tournament.TournamentTestBuilder;
import com.example.tts_in_spring.user.UserTestBuilder;
import com.example.tts_in_spring.tournament.TournamentResponseLite;
import com.example.tts_in_spring.tournament.Tournament;
import com.example.tts_in_spring.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
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

    @Mock
    private TournamentService tournamentService;

    @InjectMocks
    private CategoryService categoryService;

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
        return new CategoryRequest("Men's Singles", TournamentTestBuilder.aTournament().build().getId());
    }

    private Category buildCategoryWithTournamentAndPlayers(Tournament tournament, User user) {
        Player player = PlayerTestBuilder.aPlayer().withUser(user).build();

        Category category = CategoryTestBuilder.aCategory().withTournament(tournament).build();
        category.setPlayers(List.of(player));

        return category;
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
    void getAllCategories_whenEmpty_returnsEmptyList() {
        when(categoryRepository.findAll()).thenReturn(List.of());

        assertThat(categoryService.getAllCategories()).isEmpty();
    }

    @Test
    void getCategoryById_withPlayer_returnsMappedResponse() {
        User currentUser = UserTestBuilder.aUser().build();

        Tournament tournament = TournamentTestBuilder.aTournament().build();
        Category category = buildCategoryWithTournamentAndPlayers(tournament, currentUser);
        CategoryResponse response = buildCategoryResponse();

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(categoryMapper.toResponse(category)).thenReturn(response);

        assertThat(categoryService.getCategoryById(category.getId(), currentUser.getId())).isEqualTo(response);
    }

    @Test
    void getCategoryById_withHost_returnsMappedResponse() {
        Category category = CategoryTestBuilder.aCategory().build();
        CategoryResponse response = buildCategoryResponse();

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(categoryMapper.toResponse(category)).thenReturn(response);

        assertThat(categoryService.getCategoryById(category.getId(), category.getTournament().getHost().getId())).isEqualTo(response);
    }

    @Test
    void getCategoryById_whenNotFound_throws404() {
        User user = UserTestBuilder.aUser().build();
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategoryById(999L, user.getId()))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.NOT_FOUND)
                );
    }

    @Test
    void getCategoryById_whenNotAuthorised_throws403() {
        User outsider = UserTestBuilder.aUser().withId(3L).build();

        Category category = CategoryTestBuilder.aCategory().build();

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> categoryService.getCategoryById(category.getId(), outsider.getId()))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.FORBIDDEN)
                );
    }

    @Test
    void createCategory_savesAndReturnsMappedLite() {
        CategoryRequest request = buildCategoryRequest();

        Category saved = CategoryTestBuilder.aCategory().build();
        CategoryResponseLite lite = new CategoryResponseLite(
                100L,
                "Mens Singles",
                false,
                false
        );

        when(tournamentService.getTournamentOrThrow(request.getTournamentId())).thenReturn(saved.getTournament());
        when(categoryMapper.toEntity(request)).thenReturn(saved);
        when(categoryRepository.save(any(Category.class))).thenReturn(saved);
        when(categoryMapper.toResponseLite(saved)).thenReturn(lite);

        assertThat(categoryService.createCategory(request, saved.getTournament().getHost().getId())).isEqualTo(lite);
    }

    @Test
    void updateCategoryLocked_whenHost_returnsMappedLite() {
        Category category = CategoryTestBuilder.aCategory().build();

        CategoryLockedUpdateRequest request = new CategoryLockedUpdateRequest();
        request.setLocked(true);

        Category updatedCategory = CategoryTestBuilder.aCategory().build();
        updatedCategory.setLocked(true);
        CategoryResponseLite lite = new CategoryResponseLite(
                100L,
                "Men's Singles",
                true,
                false
        );

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);
        when(categoryMapper.toResponseLite(updatedCategory)).thenReturn(lite);

        CategoryResponseLite result = categoryService.updateLocked(category.getId(), request, category.getTournament().getHost().getId());
        assertThat(result).isEqualTo(lite);

        verify(categoryMapper).updateLockedEntity(request, category);
        verify(categoryRepository).save(category);
        verify(categoryMapper).toResponseLite(updatedCategory);
    }

    @Test
    void updateCategoryLocked_whenNotHost_throws403() {
        Category category = CategoryTestBuilder.aCategory().build();

        CategoryLockedUpdateRequest request = new CategoryLockedUpdateRequest();
        request.setLocked(true);

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> categoryService.updateLocked(category.getId(), request, 3L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.FORBIDDEN)
                );
        verify(categoryRepository, never()).save(any());
        verifyNoInteractions(categoryMapper);
    }

    @Test
    void updateCategoryLocked_whenNotFound_throws404() {
        CategoryLockedUpdateRequest request = new CategoryLockedUpdateRequest();
        request.setLocked(true);

        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.updateLocked(999L, request, 1L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.NOT_FOUND));
        verify(categoryRepository, never()).save(any());
    }
}
