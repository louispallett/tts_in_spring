package com.example.tts_in_spring.category;

import com.example.tts_in_spring.category.dto.CategoryLockedUpdateRequest;
import com.example.tts_in_spring.category.dto.CategoryRequest;
import com.example.tts_in_spring.category.dto.CategoryResponse;
import com.example.tts_in_spring.category.dto.CategoryResponseLite;
import com.example.tts_in_spring.player.Player;
import com.example.tts_in_spring.player.PlayerTestBuilder;
import com.example.tts_in_spring.tournament.TournamentFinder;
import com.example.tts_in_spring.tournament.TournamentTestBuilder;
import com.example.tts_in_spring.user.UserTestBuilder;
import com.example.tts_in_spring.tournament.dto.TournamentResponseLite;
import com.example.tts_in_spring.tournament.Tournament;
import com.example.tts_in_spring.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private CategoryFinder categoryFinder;

    @Mock
    private TournamentFinder tournamentFinder;

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
        return new CategoryRequest(Type.MEN_SINGLES, TournamentTestBuilder.aTournament().build().getId());
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

        when(categoryFinder.getCategoryOrThrow(category.getId())).thenReturn(category);
        when(categoryMapper.toResponse(category)).thenReturn(response);

        assertThat(categoryService.getCategoryById(category.getId(), currentUser.getId())).isEqualTo(response);
    }

    @Test
    void getCategoryById_withHost_returnsMappedResponse() {
        Category category = CategoryTestBuilder.aCategory().build();
        CategoryResponse response = buildCategoryResponse();

        when(categoryFinder.getCategoryOrThrow(category.getId())).thenReturn(category);
        when(categoryMapper.toResponse(category)).thenReturn(response);

        assertThat(categoryService.getCategoryById(category.getId(), category.getTournament().getHost().getId())).isEqualTo(response);
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

        when(tournamentFinder.getTournamentOrThrow(request.tournamentId())).thenReturn(saved.getTournament());
        when(categoryMapper.toEntity(request)).thenReturn(saved);
        when(categoryRepository.save(any(Category.class))).thenReturn(saved);
        when(categoryMapper.toResponseLite(saved)).thenReturn(lite);

        assertThat(categoryService.createCategory(request, saved.getTournament().getHost().getId())).isEqualTo(lite);
    }

    @Test
    void updateCategoryLocked_whenHost_returnsMappedLite() {
        Category category = CategoryTestBuilder.aCategory().build();

        CategoryLockedUpdateRequest request = new CategoryLockedUpdateRequest(true);

        Category updatedCategory = CategoryTestBuilder.aCategory().build();
        updatedCategory.setLocked(true);
        CategoryResponseLite lite = new CategoryResponseLite(
                100L,
                "Men's Singles",
                true,
                false
        );

        when(categoryFinder.getCategoryOrThrow(category.getId())).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);
        when(categoryMapper.toResponseLite(updatedCategory)).thenReturn(lite);

        CategoryResponseLite result = categoryService.updateLocked(category.getId(), request, category.getTournament().getHost().getId());
        assertThat(result).isEqualTo(lite);

        verify(categoryMapper).updateLockedEntity(request, category);
        verify(categoryRepository).save(category);
        verify(categoryMapper).toResponseLite(updatedCategory);
    }
}
