package com.example.tts_in_spring.tournament;

import com.example.tts_in_spring.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TournamentRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Show Mobile boolean must not be null")
    private boolean showMobile;

    @NotNull(message = "men_singles boolean must not be null")
    boolean men_singles;

    @NotNull(message = "women_singles boolean must not be null")
    boolean women_singles;

    @NotNull(message = "men_doubles boolean must not be null")
    boolean men_doubles;

    @NotNull(message = "women_doubles boolean must not be null")
    boolean women_doubles;

    @NotNull(message = "mix_doubles boolean must not be null")
    boolean mix_doubles;

    @Null(message = "Stage must be null")
    private String stage;

    @Null(message = "Host must be null")
    private User host;

    @Null(message = "Code must be null")
    private String code;
}

