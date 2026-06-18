package com.example.tts_in_spring.tournament;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TournamentShowMobileUpdateRequest {
    @NotNull(message = "Show Mobile boolean must not be null")
    private boolean showMobile;
}
