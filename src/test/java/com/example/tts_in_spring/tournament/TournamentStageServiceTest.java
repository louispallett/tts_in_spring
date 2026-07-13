package com.example.tts_in_spring.tournament;

import com.example.tts_in_spring.category.Category;
import com.example.tts_in_spring.category.CategoryTestBuilder;
import com.example.tts_in_spring.player.PlayerTestBuilder;
import com.example.tts_in_spring.player.Player;
import com.example.tts_in_spring.tournament.dto.ValidateResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TournamentStageServiceTest {
    @Mock
    private TournamentFinder tournamentFinder;

    @InjectMocks
    private TournamentStageService tournamentStageService;

    Category buildCategoryToValidate(int playerN, int maleN, boolean doubles, String name) {
        Category category = CategoryTestBuilder.aCategory().build();
        category.setName(name);
        category.setDoubles(doubles);

        for (long i = 1L; i <= maleN; i++) {
            Player player = PlayerTestBuilder.aPlayer().withId(i).build();
            category.getPlayers().add(player);
        }

        for (long i = maleN + 1; i <= playerN; i++) {
            Player player = PlayerTestBuilder.aPlayer().withId(i).build();
            player.setMale(false);
            category.getPlayers().add(player);
        }

        return category;
    }

    @Test
    void validate_whenValidAllCategories_returnsValidResponse() {
        Tournament tournament = TournamentTestBuilder.aTournament().build();
        Category maleSingles = buildCategoryToValidate(4, 4, false, "Mens Singles");
        Category femaleSingles = buildCategoryToValidate(4, 0, false, "Womens Singles");
        Category maleDoubles = buildCategoryToValidate(8, 8, true, "Mens Doubles");
        Category femaleDoubles = buildCategoryToValidate(8, 0, true, "Womens Doubles");
        Category mixDoubles = buildCategoryToValidate(16, 8, true, "Mixed Doubles");
        tournament.setCategories(List.of(maleSingles, femaleSingles, maleDoubles, femaleDoubles, mixDoubles));

        ValidateResponse response = new ValidateResponse(true, true, true, true);

        when(tournamentFinder.getTournamentOrThrow(tournament.getId())).thenReturn(tournament);

        assertThat(tournamentStageService.validate(tournament.getId(), tournament.getHost().getId())).isEqualTo(response);
    }

    @Test
    void validate_whenInvalidAllCategories_returnsInvalidResponse() {
        Tournament tournament = TournamentTestBuilder.aTournament().build();
        Category maleSingles = buildCategoryToValidate(9, 9, false, "Mens Singles");
        Category femaleSingles = buildCategoryToValidate(3, 0, false, "Womens Singles");
        Category maleDoubles = buildCategoryToValidate(9, 9, true, "Mens Doubles");
        Category femaleDoubles = buildCategoryToValidate(6, 0, true, "Womens Doubles");
        Category mixDoubles = buildCategoryToValidate(18, 8, true, "Mixed Doubles");
        tournament.setCategories(List.of(maleSingles, femaleSingles, maleDoubles, femaleDoubles, mixDoubles));

        ValidateResponse response = new ValidateResponse(false, false, false, false);

        when(tournamentFinder.getTournamentOrThrow(tournament.getId())).thenReturn(tournament);

        assertThat(tournamentStageService.validate(tournament.getId(), tournament.getHost().getId())).isEqualTo(response);
    }

    @Test
    void validate_whenValidSingleCategory_returnsValidResponse() {
        Tournament tournament = TournamentTestBuilder.aTournament().build();
        Category maleSingles = buildCategoryToValidate(9, 9, false, "Mens Singles");
        tournament.getCategories().add(maleSingles);

        ValidateResponse response = new ValidateResponse(true, true, true, true);

        when(tournamentFinder.getTournamentOrThrow(tournament.getId())).thenReturn(tournament);

        assertThat(tournamentStageService.validate(tournament.getId(), tournament.getHost().getId())).isEqualTo(response);
    }

    @Test
    void validate_whenInvalidSingleCategory_returnsInvalidResponse() {
        Tournament tournament = TournamentTestBuilder.aTournament().build();
        Category mixDoubles = buildCategoryToValidate(18, 8, true, "Mixed Doubles");
        tournament.getCategories().add(mixDoubles);

        ValidateResponse response = new ValidateResponse(true, true, true, false);

        when(tournamentFinder.getTournamentOrThrow(tournament.getId())).thenReturn(tournament);

        assertThat(tournamentStageService.validate(tournament.getId(), tournament.getHost().getId())).isEqualTo(response);
    }
}
