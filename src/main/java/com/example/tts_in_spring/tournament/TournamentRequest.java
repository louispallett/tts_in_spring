package com.example.tts_in_spring.tournament;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TournamentRequest (
    @NotBlank(message = "Name is required")
    String name,
    @NotNull(message = "Show Mobile boolean must not be null")
    boolean showMobile,
    @NotNull(message = "men_singles boolean must not be null")
    boolean men_singles,
    @NotNull(message = "women_singles boolean must not be null")
    boolean women_singles,
    @NotNull(message = "men_doubles boolean must not be null")
    boolean men_doubles,
    @NotNull(message = "women_doubles boolean must not be null")
    boolean women_doubles,
    @NotNull(message = "mix_doubles boolean must not be null")
    boolean mix_doubles
) {}

