package com.example.tts_in_spring.participant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ParticipantServiceTest {
    @InjectMocks
    private ParticipantService participantService;

    @Test
    void getAllParticipants_returnsMappedResponse() {

    }

    @Test
    void getAllParticipants_whenEmpty_returnsEmptyList() {

    }

    @Test
    void getParticipantById_whenHost_returnsMappedResponse() {

    }

    @Test
    void getParticipantById_whenParticipant_returnsMappedResponse() {

    }

    @Test
    void getParticipantById_whenNotAuthorized_returns403() {

    }

    @Test
    void getParticipantById_whenEmpty_returns404() {

    }

    @Test
    void createParticipant_whenHost_savesAndReturnsMappedLite() {

    }

    @Test
    void createParticipant_whenNotHost_returns403() {

    }

    @Test
    void updateResultText_whenHost_savesAndReturnsMappedLite() {

    }

    @Test
    void updateResultText_whenParticipantOfMatch_savesAndReturnsMappedLite() {

    }

    @Test
    void updateResultText_whenNotAuthorized_returns403() {

    }

    @Test
    void updateResultText_whenNotFound_returns404() {

    }

    @Test
    void updateIsWinner_whenHost_savesAndReturnsMappedLite() {

    }

    @Test
    void updateIsWinner_whenParticipantOfMatch_savesAndReturnsMappedLite() {

    }

    @Test
    void updateIsWinner_whenNotAuthorized_returns403() {

    }

    @Test
    void updateIsWinner_whenNotFound_returns404() {

    }

    @Test
    void updateStatus_whenHost_savesAndReturnsMappedLite() {

    }

    @Test
    void updateStatus_whenParticipantOfMatch_savesAndReturnsMappedLite() {

    }

    @Test
    void updateStatus_whenNotAuthorized_returns403() {

    }

    @Test
    void updateStatus_whenNotFound_returns404() {

    }
}
