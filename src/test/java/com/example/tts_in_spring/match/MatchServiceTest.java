package com.example.tts_in_spring.match;

import com.example.tts_in_spring.category.CategoryResponseLite;
import com.example.tts_in_spring.category.CategoryService;
import com.example.tts_in_spring.category.CategoryTestBuilder;
import com.example.tts_in_spring.participant.Participant;
import com.example.tts_in_spring.participant.ParticipantTestBuilder;
import com.example.tts_in_spring.player.Player;
import com.example.tts_in_spring.player.PlayerTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

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
    private CategoryService categoryService;

    @InjectMocks
    private MatchService matchService;

    private MatchResponse buildMatchResponse() {
        return new MatchResponse(
                100000L,
                "",
                "SCHEDULED",
                Instant.now(),
                0,
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
                0,
                false
        );
    }

    private MatchRequest buildMatchRequest(Instant deadline) {
        MatchRequest r = new MatchRequest();
        r.setTournamentRoundText("");
        r.setDeadline(deadline);
        r.setQualifyingMatch(false);
        r.setCategoryId(CategoryTestBuilder.aCategory().build().getId());

        return r;
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

        when(matchRepository.findById(match.getId())).thenReturn(Optional.of(match));
        when(matchMapper.toResponse(match)).thenReturn(response);

        assertThat(matchService.getMatchById(match.getId(), match.getCategory().getTournament().getHost().getId())).isEqualTo(response);
    }


    @Test
    void getMatchById_whenParticipant_returnsMappedResponse() {
        Match match = MatchTestBuilder.aMatch().build();
        MatchResponse response = buildMatchResponse();
        Player player = PlayerTestBuilder.aPlayer().build();
        Participant participant = ParticipantTestBuilder.aParticipant().withPlayer(player).build();
        match.getParticipants().add(participant);

        when(matchRepository.findById(match.getId())).thenReturn(Optional.of(match));
        when(matchMapper.toResponse(match)).thenReturn(response);

        assertThat(matchService.getMatchById(match.getId(), player.getUser().getId())).isEqualTo(response);
    }

    @Test
    void getMatchById_whenNotAuthorized_returns403() {
        Match match = MatchTestBuilder.aMatch().build();

        when(matchRepository.findById(match.getId())).thenReturn(Optional.of(match));

        assertThatThrownBy(() -> matchService.getMatchById(match.getId(), 3L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.FORBIDDEN)
                );
    }

    @Test
    void getMatchById_whenEmpty_returns404() {
      when(matchRepository.findById(999999L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> matchService.getMatchById(999999L, 1L))
              .isInstanceOf(ResponseStatusException.class)
              .satisfies(ex -> 
                    assertThat(((ResponseStatusException) ex).getStatusCode())
                            .isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void createMatch_whenHost_savesAndReturnsMappedLite() {
        Instant deadline = Instant.now();
        MatchRequest request = buildMatchRequest(deadline);

        Match saved = MatchTestBuilder.aMatch().build();
        MatchResponseLite lite = new MatchResponseLite(
            100000L,
            "",
            "SCHEDULED",
            deadline,
            0,
            false
        );

        when(categoryService.getCategoryOrThrow(request.getCategoryId())).thenReturn(saved.getCategory());
        when(matchMapper.toEntity(request)).thenReturn(saved);
        when(matchRepository.save(any(Match.class))).thenReturn(saved);
        when(matchMapper.toResponseLite(saved)).thenReturn(lite);

        assertThat(matchService.createMatch(request, saved.getCategory().getTournament().getHost().getId()));
    }

    @Test
    void createMatch_whenNotHost_returns403() {
        MatchRequest request = buildMatchRequest(Instant.now());
        Match match = MatchTestBuilder.aMatch().build();

        when(categoryService.getCategoryOrThrow(request.getCategoryId())).thenReturn(match.getCategory());

        assertThatThrownBy(() -> matchService.createMatch(request, 3L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.FORBIDDEN)
                );

        verify(matchRepository, never()).save(any());
        verifyNoInteractions(matchMapper);
    }

    @Test
    void submitScore_whenHost_savesAndReturnsMappedLite() {
        Match match = MatchTestBuilder.aMatch().build();

        MatchSubmitScoreRequest request = new MatchSubmitScoreRequest();
        request.setState("SCORE_DONE");

        Match updatedMatch = MatchTestBuilder.aMatch().build();
        updatedMatch.setState("SCORE_DONE");
        MatchResponseLite lite = buildMatchResponseLite("SCORE_DONE", Instant.now());

        when(matchRepository.findById(match.getId())).thenReturn(Optional.of(match));
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

        MatchSubmitScoreRequest request = new MatchSubmitScoreRequest();
        request.setState("SCORE_DONE");

        Match updatedMatch = MatchTestBuilder.aMatch().build();
        updatedMatch.setState("SCORE_DONE");
        MatchResponseLite lite = buildMatchResponseLite("SCORE_DONE", Instant.now());

        when(matchRepository.findById(match.getId())).thenReturn(Optional.of(match));
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

        MatchSubmitScoreRequest request = new MatchSubmitScoreRequest();
        request.setState("SCORE_DONE");

        when(matchRepository.findById(match.getId())).thenReturn(Optional.of(match));

        assertThatThrownBy(() -> matchService.submitScore(match.getId(), request, 3L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.FORBIDDEN)
                );
        verify(matchRepository, never()).save(any());
        verifyNoInteractions(matchMapper);
    }

    @Test
    void submitScore_whenMatchMissing_returns404() {
        MatchSubmitScoreRequest request = new MatchSubmitScoreRequest();
        request.setState("SCORE_DONE");

        when(matchRepository.findById(999999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> matchService.submitScore(999999L, request, 1L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void updateDeadline_whenHost_savesAndReturnsMappedLite() {
        Match match = MatchTestBuilder.aMatch().build();

        MatchUpdateDeadlineRequest request = new MatchUpdateDeadlineRequest();
        request.deadline = Instant.MIN;

        Match updatedMatch = MatchTestBuilder.aMatch().build();
        updatedMatch.setDeadline(Instant.MIN);
        MatchResponseLite lite = buildMatchResponseLite("SCHEDULED", Instant.MIN);

        when(matchRepository.findById(match.getId())).thenReturn(Optional.of(match));
        when(matchRepository.save(any(Match.class))).thenReturn(updatedMatch);
        when(matchMapper.toResponseLite(updatedMatch)).thenReturn(lite);

        assertThat(matchService.updateDeadline(match.getId(), request, match.getCategory().getTournament().getHost().getId()));

        verify(matchMapper).updateDeadlineEntity(request, match);
        verify(matchRepository).save(match);
        verify(matchMapper).toResponseLite(updatedMatch);
    }

    @Test
    void updateDeadline_whenNotHost_returns403() {
        Match match = MatchTestBuilder.aMatch().build();

        MatchUpdateDeadlineRequest request = new MatchUpdateDeadlineRequest();
        request.deadline = Instant.MIN;

        when(matchRepository.findById(match.getId())).thenReturn(Optional.of(match));

        assertThatThrownBy(() -> matchService.updateDeadline(match.getId(), request, 3L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.FORBIDDEN)
                );
        verify(matchRepository, never()).save(any());
        verifyNoInteractions(matchMapper);
    }

    @Test
    void updateDeadline_whenMatchMissing_returns404() {
        MatchUpdateDeadlineRequest request = new MatchUpdateDeadlineRequest();
        request.deadline = Instant.MIN;

        when(matchRepository.findById(999999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> matchService.updateDeadline(999999L, request, 1L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void deleteMatch_whenHost_deletesMatch() {
        Match match = MatchTestBuilder.aMatch().build();

        when(matchRepository.findById(match.getId())).thenReturn(Optional.of(match));

        matchService.delete(match.getId(), match.getCategory().getTournament().getHost().getId());

        verify(matchRepository).delete(match);
    }

    @Test
    void deleteMatch_whenNotHost_throws403() {
        Match match = MatchTestBuilder.aMatch().build();

        when(matchRepository.findById(match.getId())).thenReturn(Optional.of(match));

        assertThatThrownBy(() -> matchService.delete(match.getId(), 3L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.FORBIDDEN)
                );
        verify(matchRepository, never()).save(any());
    }

    @Test
    void deleteMatch_whenNotFound_throws404() {
        when(matchRepository.findById(999999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> matchService.delete(999999L, 1L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex ->
                        assertThat(((ResponseStatusException) ex).getStatusCode())
                                .isEqualTo(HttpStatus.NOT_FOUND)
                );
        verify(matchRepository, never()).save(any());
    }
}
