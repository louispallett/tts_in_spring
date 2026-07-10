package com.example.tts_in_spring.match;

import com.example.tts_in_spring.category.Category;
import com.example.tts_in_spring.category.CategoryFinder;
import com.example.tts_in_spring.category.CategoryTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class MatchGenerationServiceTest {
    @Mock
    private MatchRepository matchRepository;

    @Mock
    private MatchMapper matchMapper;

    @Mock
    private MatchFinder matchFinder;

    @Mock
    private CategoryFinder categoryFinder;

    @InjectMocks
    private MatchGenerationService matchGenerationService;

    @Test
    void calculateNumberOfRounds_returnsExpected() {
        assertThat(matchGenerationService.calculateNumberOfRounds(8)).isEqualTo(3);
        assertThat(matchGenerationService.calculateNumberOfRounds(10)).isEqualTo(4);
        assertThat(matchGenerationService.calculateNumberOfRounds(16)).isEqualTo(4);
        assertThat(matchGenerationService.calculateNumberOfRounds(20)).isEqualTo(5);
        assertThat(matchGenerationService.calculateNumberOfRounds(256)).isEqualTo(8);
    }

    @Test
    void getNextPowerOfTwo_returnsExpected() {
        assertThat(matchGenerationService.getNextPowerOfTwo(4)).isEqualTo(4);
        assertThat(matchGenerationService.getNextPowerOfTwo(5)).isEqualTo(8);
        assertThat(matchGenerationService.getNextPowerOfTwo(29)).isEqualTo(32);
    }

    @Test
    void calculateByes_returnsExpected() {
        assertThat(matchGenerationService.calculateByes(16)).isEqualTo(0);
        assertThat(matchGenerationService.calculateByes(19)).isEqualTo(13);
        assertThat(matchGenerationService.calculateByes(31)).isEqualTo(1);
    }

    @Test
    void roundLoopLimit_returnsExpected() {
        assertThat(matchGenerationService.roundLoopLimit(16, 16, 4)).isEqualTo(4);
        assertThat(matchGenerationService.roundLoopLimit(1, 31, 5)).isEqualTo(4);
    }

    @Test
    void splitIntoFours_returnsExpected() {
        List<Match> matches = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            matches.add(MatchTestBuilder.aMatch().build());
        }

        assertThat(matchGenerationService.splitIntoFours(matches)).hasSize(4);
        assertThat(matchGenerationService.splitIntoFours(matches).getFirst()).hasSize(4);
    }

    @Test
    void reorderGroups_returnsExpected() {
        List<Match> matches = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            matches.add(MatchTestBuilder.aMatch().build());
        }
        List<List<Match>> splitIntoFours = matchGenerationService.splitIntoFours(matches);
        List<List<Match>> result = matchGenerationService.reorderGroups(splitIntoFours);

        assertThat(result).hasSize(4);
        assertThat(result.getFirst()).hasSize(4);
        assertThat(result.getFirst().getFirst()).isEqualTo(matches.getFirst());
        assertThat(result.getFirst().getLast()).isEqualTo(matches.get(3));
        assertThat(result.get(1).getFirst()).isEqualTo(matches.get(12));
        assertThat(result.get(1).getLast()).isEqualTo(matches.get(15));
        assertThat(result.get(2).getFirst()).isEqualTo(matches.get(8));
        assertThat(result.get(2).getLast()).isEqualTo(matches.get(11));
        assertThat(result.getLast().getFirst()).isEqualTo(matches.get(4));
        assertThat(result.getLast().getLast()).isEqualTo(matches.get(7));
    }

    @Test
    void reorderArray_returnsExpected() {
        List<Match> matches = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            matches.add(MatchTestBuilder.aMatch().build());
        }
        List<List<Match>> splitIntoFours = matchGenerationService.splitIntoFours(matches);
        List<List<Match>> reorderGroups = matchGenerationService.reorderGroups(splitIntoFours);

        List<Match> result = matchGenerationService.reorderArray(reorderGroups);

        assertThat(result).hasSize(16);
        assertThat(result.getFirst()).isEqualTo(matches.getFirst());
        assertThat(result.get(1)).isEqualTo(matches.get(12));
        assertThat(result.get(2)).isEqualTo(matches.get(8));
        assertThat(result.get(3)).isEqualTo(matches.get(4));
        assertThat(result.get(4)).isEqualTo(matches.get(3));
        assertThat(result.get(5)).isEqualTo(matches.get(15));
        assertThat(result.get(6)).isEqualTo(matches.get(11));
        assertThat(result.get(7)).isEqualTo(matches.get(7));
        assertThat(result.get(8)).isEqualTo(matches.get(2));
        assertThat(result.get(9)).isEqualTo(matches.get(14));
        assertThat(result.get(10)).isEqualTo(matches.get(10));
        assertThat(result.get(11)).isEqualTo(matches.get(6));
        assertThat(result.get(12)).isEqualTo(matches.get(1));
        assertThat(result.get(13)).isEqualTo(matches.get(13));
        assertThat(result.get(14)).isEqualTo(matches.get(9));
        assertThat(result.get(15)).isEqualTo(matches.get(5));
    }

    @Test
    void generateMatches_whenNoQual_buildsCorrectStructure() {
        Category category = CategoryTestBuilder.aCategory().build();

        List<List<Match>> result = matchGenerationService.generateMatches(category, 8);

        assertThat(result).hasSize(matchGenerationService.calculateNumberOfRounds(8));

        assertThat(result.get(0)).hasSize(1);

        assertThat(result.get(1)).hasSize(2);
        assertThat(result.get(2)).hasSize(4);
    }

    @Test
    void generateMatches_whenQual_buildsCorrectStructure() {
        Category category = CategoryTestBuilder.aCategory().build();

        int numOfParticipants = 24;

        List<List<Match>> result = matchGenerationService.generateMatches(category, numOfParticipants);

        assertThat(result).hasSize(matchGenerationService.calculateNumberOfRounds(numOfParticipants) - 1);

        assertThat(result.get(0)).hasSize(1);

        assertThat(result.get(1)).hasSize(2);
        assertThat(result.get(2)).hasSize(4);
        assertThat(result.get(3)).hasSize(8);
    }
}
