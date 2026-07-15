package com.example.tts_in_spring.match.dto;

import java.util.List;

public record MatchUpdateDeadlinesByRoundRequest(
        List<DeadlineByRoundRequest> rounds
) {}
