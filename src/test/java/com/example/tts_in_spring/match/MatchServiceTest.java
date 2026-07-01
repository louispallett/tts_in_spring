package com.example.tts_in_spring.match;

import com.example.tts_in_spring.category.Category;
import com.example.tts_in_spring.category.CategoryFinder;
import com.example.tts_in_spring.category.dto.CategoryResponseLite;
import com.example.tts_in_spring.category.CategoryTestBuilder;
import com.example.tts_in_spring.exception.ForbiddenException;
import com.example.tts_in_spring.match.dto.*;
import com.example.tts_in_spring.participant.Participant;
import com.example.tts_in_spring.participant.ParticipantTestBuilder;
import com.example.tts_in_spring.player.Player;
import com.example.tts_in_spring.player.PlayerTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class MatchServiceTest {
    @Mock
    private MatchRepository matchRepository;

    @Mock
    private MatchMapper matchMapper;

    @Mock
    private MatchFinder matchFinder;

    @Mock
    private CategoryFinder categoryFinder;

    @InjectMocks
    private MatchService matchService;

    private MatchResponse buildMatchResponse() {
        return new MatchResponse(
                100000L,
                "",
                "SCHEDULED",
                Instant.now(),
                false,
                new CategoryResponseLite(100L, "Mens Singles", false, false),
                null,
                List.of(),
                List.of()
        );
    }

    private MatchResponseLite buildMatchResponseLite(String state, Instant deadline) {
        return new MatchResponseLite(
                100000L,
                "",
                state,
                deadline,
                false
        );
    }

    private MatchRequest buildMatchRequest(Instant deadline) {
        return new MatchRequest(
                "",
                deadline,
                false,
                CategoryTestBuilder.aCategory().build().getId(),
                null
        );
    }

    @Test
    void getAllMatches_returnsMappedResponse() {
        Match match = MatchTestBuilder.aMatch().build();
        MatchResponse response = buildMatchResponse();

        when(matchRepository.findAll()).thenReturn(List.of(match));
        when(matchMapper.toResponse(match)).thenReturn(response);

        List<MatchResponse> result = matchService.getAllMatches();

        assertThat(result).containsExactly(response);
    }

    @Test
    void getAllMatches_whenEmpty_returnsEmptyList() {
        when(matchRepository.findAll()).thenReturn(List.of());

        assertThat(matchService.getAllMatches()).isEmpty();
    }

    @Test 
    void getMatchById_whenHost_returnsMappedResponse() {
        Match match = MatchTestBuilder.aMatch().build();
        MatchResponse response = buildMatchResponse();
        Long hostId = match.getCategory().getTournament().getHost().getId();

        when(matchFinder.getMatchOrThrow(match.getId())).thenReturn(match);
        when(matchFinder.isHost(match, hostId)).thenReturn(true);
        when(matchMapper.toResponse(match)).thenReturn(response);

        assertThat(matchService.getMatchById(match.getId(), hostId)).isEqualTo(response);
    }


    @Test
    void getMatchById_whenParticipant_returnsMappedResponse() {
        Match match = MatchTestBuilder.aMatch().build();
        MatchResponse response = buildMatchResponse();
        Player player = PlayerTestBuilder.aPlayer().build();
        Participant participant = ParticipantTestBuilder.aParticipant().withPlayer(player).build();
        match.getParticipants().add(participant);

        when(matchFinder.getMatchOrThrow(match.getId())).thenReturn(match);
        when(matchFinder.isParticipant(match, player.getUser().getId())).thenReturn(true);
        when(matchMapper.toResponse(match)).thenReturn(response);

        assertThat(matchService.getMatchById(match.getId(), player.getUser().getId())).isEqualTo(response);
    }

    @Test
    void getMatchById_whenNotAuthorized_returns403() {
        Match match = MatchTestBuilder.aMatch().build();

        when(matchFinder.getMatchOrThrow(match.getId())).thenReturn(match);

        assertThatThrownBy(() -> matchService.getMatchById(match.getId(), 3L))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void createMatch_whenHost_savesAndReturnsMapped() {
        Instant deadline = Instant.now();
        MatchRequest request = buildMatchRequest(deadline);

        Match saved = MatchTestBuilder.aMatch().build();
        MatchResponse response = new MatchResponse(
            100000L,
            "",
            "SCHEDULED",
            deadline,
            false,
                new CategoryResponseLite(
                        100L,
                        "Mens Singles",
                        false,
                        false
                ),
                new MatchResponseLite(
                        100001L,
                        "",
                        "SCHEDULED",
                        deadline,
                        false
                ),
                List.of(),
                List.of()
        );

        when(categoryFinder.getCategoryOrThrow(request.categoryId())).thenReturn(saved.getCategory());
        when(matchMapper.toEntity(request)).thenReturn(saved);
        when(matchRepository.save(any(Match.class))).thenReturn(saved);
        when(matchMapper.toResponse(saved)).thenReturn(response);

        assertThat(matchService.createMatch(request, saved.getCategory().getTournament().getHost().getId()));
    }

    @Test
    void createMatch_whenNotHost_returns403() {
        MatchRequest request = buildMatchRequest(Instant.now());
        Match match = MatchTestBuilder.aMatch().build();

        when(categoryFinder.getCategoryOrThrow(request.categoryId())).thenReturn(match.getCategory());

        assertThatThrownBy(() -> matchService.createMatch(request, 3L))
                .isInstanceOf(ForbiddenException.class);

        verify(matchRepository, never()).save(any());
        verifyNoInteractions(matchMapper);
    }

    @Test
    void calculateNumberOfRounds_returnsExpected() {
        assertThat(matchService.calculateNumberOfRounds(8)).isEqualTo(3);
        assertThat(matchService.calculateNumberOfRounds(10)).isEqualTo(4);
        assertThat(matchService.calculateNumberOfRounds(16)).isEqualTo(4);
        assertThat(matchService.calculateNumberOfRounds(20)).isEqualTo(5);
        assertThat(matchService.calculateNumberOfRounds(256)).isEqualTo(8);
    }

    @Test
    void getNextPowerOfTwo_returnsExpected() {
        assertThat(matchService.getNextPowerOfTwo(4)).isEqualTo(4);
        assertThat(matchService.getNextPowerOfTwo(5)).isEqualTo(8);
        assertThat(matchService.getNextPowerOfTwo(29)).isEqualTo(32);
    }

    @Test
    void calculateByes_returnsExpected() {
        assertThat(matchService.calculateByes(16)).isEqualTo(0);
        assertThat(matchService.calculateByes(19)).isEqualTo(13);
        assertThat(matchService.calculateByes(31)).isEqualTo(1);
    }

    @Test
    void roundLoopLimit_returnsExpected() {
        assertThat(matchService.roundLoopLimit(16, 16, 4)).isEqualTo(4);
        assertThat(matchService.roundLoopLimit(1, 31, 5)).isEqualTo(4);
    }

    @Test
    void splitIntoFours_returnsExpected() {
        List<Match> matches = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            matches.add(MatchTestBuilder.aMatch().build());
        }

        assertThat(matchService.splitIntoFours(matches)).hasSize(4);
        assertThat(matchService.splitIntoFours(matches).getFirst()).hasSize(4);
    }

    @Test
    void reorderGroups_returnsExpected() {
        List<Match> matches = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            matches.add(MatchTestBuilder.aMatch().build());
        }
        List<List<Match>> splitIntoFours = matchService.splitIntoFours(matches);
        List<List<Match>> result = matchService.reorderGroups(splitIntoFours);

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
        List<List<Match>> splitIntoFours = matchService.splitIntoFours(matches);
        List<List<Match>> reorderGroups = matchService.reorderGroups(splitIntoFours);

        List<Match> result = matchService.reorderArray(reorderGroups);

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
    void generateAndSaveMatches_whenNoQual_buildsCorrectStructure() {
        Category category = CategoryTestBuilder.aCategory().build();

        List<List<Match>> result = matchService.generateMatches(category, 8);

        assertThat(result).hasSize(matchService.calculateNumberOfRounds(8));

        assertThat(result.get(0)).hasSize(1);

        assertThat(result.get(1)).hasSize(2);
        assertThat(result.get(2)).hasSize(4);
    }

    @Test
    void generateAndSaveMatches_whenQual_buildsCorrectStructure() {
        Category category = CategoryTestBuilder.aCategory().build();

        int numOfParticipants = 24;

        List<List<Match>> result = matchService.generateMatches(category, numOfParticipants);

        assertThat(result).hasSize(matchService.calculateNumberOfRounds(numOfParticipants) - 1);

        assertThat(result.get(0)).hasSize(1);

        assertThat(result.get(1)).hasSize(2);
        assertThat(result.get(2)).hasSize(4);
        assertThat(result.get(3)).hasSize(8);
    }

    @Test
    void submitScore_whenHost_savesAndReturnsMappedLite() {
        Match match = MatchTestBuilder.aMatch().build();

        MatchSubmitScoreRequest request = new MatchSubmitScoreRequest("SCORE_DONE");

        Match updatedMatch = MatchTestBuilder.aMatch().build();
        updatedMatch.setState("SCORE_DONE");
        MatchResponseLite lite = buildMatchResponseLite("SCORE_DONE", Instant.now());

        when(matchFinder.getMatchOrThrow(match.getId())).thenReturn(match);
        when(matchFinder.isHost(match, match.getCategory().getTournament().getHost().getId())).thenReturn(true);
        when(matchRepository.save(any(Match.class))).thenReturn(updatedMatch);
        when(matchMapper.toResponseLite(updatedMatch)).thenReturn(lite);

        assertThat(matchService.submitScore(match.getId(), request, match.getCategory().getTournament().getHost().getId()));

        verify(matchMapper).submitScoreEntity(request, match);
        verify(matchRepository).save(match);
        verify(matchMapper).toResponseLite(updatedMatch);
    }

    @Test
    void submitScore_whenParticipant_savesAndReturnsMappedLite() {
        Match match = MatchTestBuilder.aMatch().build();
        Player player = PlayerTestBuilder.aPlayer().build();
        Participant participant = ParticipantTestBuilder.aParticipant().withPlayer(player).build();
        match.getParticipants().add(participant);

        MatchSubmitScoreRequest request = new MatchSubmitScoreRequest("SCORE_DONE");

        Match updatedMatch = MatchTestBuilder.aMatch().build();
        updatedMatch.setState("SCORE_DONE");
        MatchResponseLite lite = buildMatchResponseLite("SCORE_DONE", Instant.now());

        when(matchFinder.getMatchOrThrow(match.getId())).thenReturn(match);
        when(matchFinder.isParticipant(match, player.getUser().getId())).thenReturn(true);
        when(matchRepository.save(any(Match.class))).thenReturn(updatedMatch);
        when(matchMapper.toResponseLite(updatedMatch)).thenReturn(lite);

        assertThat(matchService.submitScore(match.getId(), request, participant.getPlayer().getUser().getId()));

        verify(matchMapper).submitScoreEntity(request, match);
        verify(matchRepository).save(match);
        verify(matchMapper).toResponseLite(updatedMatch);
    }

    @Test
    void submitScore_whenNotAuthorized_returns403() {
        Match match = MatchTestBuilder.aMatch().build();

        MatchSubmitScoreRequest request = new MatchSubmitScoreRequest("SCORE_DONE");

        when(matchFinder.getMatchOrThrow(match.getId())).thenReturn(match);

        assertThatThrownBy(() -> matchService.submitScore(match.getId(), request, 3L))
                .isInstanceOf(ForbiddenException.class);
        verify(matchRepository, never()).save(any());
        verifyNoInteractions(matchMapper);
    }

    @Test
    void updateDeadline_whenHost_savesAndReturnsMappedLite() {
        Match match = MatchTestBuilder.aMatch().build();

        MatchUpdateDeadlineRequest request = new MatchUpdateDeadlineRequest(Instant.MIN);

        Match updatedMatch = MatchTestBuilder.aMatch().build();
        updatedMatch.setDeadline(Instant.MIN);
        MatchResponseLite lite = buildMatchResponseLite("SCHEDULED", Instant.MIN);

        when(matchFinder.getMatchOrThrow(match.getId())).thenReturn(match);
        when(matchRepository.save(any(Match.class))).thenReturn(updatedMatch);
        when(matchMapper.toResponseLite(updatedMatch)).thenReturn(lite);

        assertThat(matchService.updateDeadline(match.getId(), request, match.getCategory().getTournament().getHost().getId()));

        verify(matchMapper).updateDeadlineEntity(request, match);
        verify(matchRepository).save(match);
        verify(matchMapper).toResponseLite(updatedMatch);
    }

    @Test
    void deleteMatch_whenHost_deletesMatch() {
        Match match = MatchTestBuilder.aMatch().build();

        when(matchFinder.getMatchOrThrow(match.getId())).thenReturn(match);

        matchService.delete(match.getId(), match.getCategory().getTournament().getHost().getId());

        verify(matchRepository).delete(match);
    }
}
