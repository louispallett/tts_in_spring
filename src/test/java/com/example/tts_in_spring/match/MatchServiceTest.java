package com.example.tts_in_spring.match;

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
                State.SCHEDULED,
                Instant.now(),
                false,
                new CategoryResponseLite(100L, "Mens Singles", false, false),
                null,
                List.of(),
                List.of()
        );
    }

    private MatchResponseLite buildMatchResponseLite(Instant deadline) {
        return new MatchResponseLite(
                100000L,
                "",
                State.SCHEDULED,
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
    void updateDeadline_whenHost_savesAndReturnsMappedLite() {
        Match match = MatchTestBuilder.aMatch().build();

        MatchUpdateDeadlineRequest request = new MatchUpdateDeadlineRequest(Instant.MIN);

        MatchResponseLite lite = buildMatchResponseLite(Instant.MIN);

        when(matchFinder.getMatchOrThrow(match.getId())).thenReturn(match);
        when(matchMapper.toResponseLite(match)).thenReturn(lite);

        assertThat(matchService.updateDeadline(
                match.getId(),
                request,
                match.getCategory().getTournament().getHost().getId())
        ).isEqualTo(lite);

        verify(matchMapper).updateDeadlineEntity(request, match);
        verify(matchMapper).toResponseLite(match);
    }

    @Test
    void deleteMatch_whenHost_deletesMatch() {
        Match match = MatchTestBuilder.aMatch().build();

        when(matchFinder.getMatchOrThrow(match.getId())).thenReturn(match);

        matchService.delete(match.getId(), match.getCategory().getTournament().getHost().getId());

        verify(matchRepository).delete(match);
    }
}
