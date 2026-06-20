package com.example.tts_in_spring.match;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MatchServiceTest {
    @InjectMocks
    private MatchService matchService;

    @Test
    void getAllMatches_returnsMappedResponse() {

    }

    @Test
    void getAllMatches_whenEmpty_returnsEmptyList() {

    }

    @Test
    void getMatchById_whenHost_returnsMappedResponse() {

    }

    @Test
    void getMatchById_whenParticipant_returnsMappedResponse() {

    }

    @Test
    void getMatchById_whenNotAuthorized_returns403() {

    }

    @Test
    void getMatchById_whenEmpty_returns404() {

    }

    @Test
    void createMatch_whenHost_savesAndReturnsMappedLite() {

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
