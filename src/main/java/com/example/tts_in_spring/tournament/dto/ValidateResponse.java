package com.example.tts_in_spring.tournament.dto;

public record ValidateResponse(
        boolean doublesHaveEightPlayers,
        boolean doublesHaveEvenPlayers,
        boolean singlesHaveFourPlayers,
        boolean mixedHasEqualMaleAndFemale
) {}
