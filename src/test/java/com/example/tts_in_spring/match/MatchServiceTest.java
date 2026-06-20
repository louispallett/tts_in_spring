package com.example.tts_in_spring.match;

import com.example.tts_in_spring.category.CategoryResponseLite;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MatchServiceTest {
    @Mock
    private MatchRepository matchRepository;

    @Mock
    private MatchMapper matchMapper;

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

    private MatchRequest buildMatchRequest(Instant deadline) {
        MatchRequest r = new MatchRequest();
        r.setTournamentRoundText("");
        r.setDeadline(deadline);
        r.setQualifyingMatch(false);
        r.setCategoryId(CategoryTestBuilder.aCategory.build().getId());
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
        Match match = MatchTestBuilder.aMatch.build();
        MatchResponse response = buildMatchResponse();

        when(matchRepository.findById(match.getId())).thenReturn(Optional.of(match));
        when(matchMapper.toResponse(match)).thenReturn(response);

        assertThat(matchService.getMatchById(match.getId(), match.getCategory().getTournament().getHost().getId())).isEqualTo(response);
    }


    @Test
    void getMatchById_whenParticipant_returnsMappedResponse() {
        Match match = MatchTestBuilder.aMatch.build();
        MatchResponse response = buildMatchResponse();
        User user = UserTestBuilder.aUser.withId(2L).build();
        Participant participant = ParticipantTestBuilder.aParticipant.withUser(user).build();
        match.getParticipants().add(participant);

        when(matchRepository.findById(match.getId())).thenReturn(Optional.of(match));
        when(matchMapper.toResponse(match)).thenReturn(response);

        assertThat(matchService.getMatchById(match.id(), user.getId())).isEqualTo(response);
    }

    @Test
    void getMatchById_whenNotAuthorized_returns403() {
        Match match = MatchTestBuilder.aMatch.build();

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
      when(matchRepository.findById(999999L)).thenReturn(Optional.of(empty()));

      assertThatThrownBy(() -> matchService.getMatchById(999999L, 1L))
              .isInstanceOf(ResponseStatusException.class)
              .satisfies(ex -> 
                    assertThat(((ResponseStatusException) ex).getStatusCode())
                            .isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    void createMatch_whenHost_savesAndReturnsMappedLite() {
        Instant deadline = Instant.now();
        MatchRequest request = buildMatchRequest(Instant deadline);

        Match saved = MatchTestBuilder.aMatch.build();
        MatchResponseLite lite = new MatchResponseLite(
            100000L,
            "",
            "SCHEDULED",
            deadline,
            0,
            false
        )

    }

    @Test
    void createMatch_whenNotHost_returns403() {

    }

    @Test
    void updateState_whenHost_savesAndReturnsMappedLite() {

    }

    @Test
    void updateState_whenParticipant_savesAndReturnsMappedLite() {

    }

    @Test
    void updateState_whenNotAuthorized_returns403() {

    }

    @Test
    void updateState_whenMatchMissing_returns404() {

    }

    @Test
    void updateDeadline_whenHost_savesAndReturnsMappedLite() {

    }

    @Test
    void updateDeadline_whenNotHost_returns403() {

    }

    @Test
    void updateDeadline_whenMatchMissing_returns404() {

    }
}
