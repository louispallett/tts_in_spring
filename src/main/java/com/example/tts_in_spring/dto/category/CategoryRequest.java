package com.example.tts_in_spring.dto.category;

import com.example.tts_in_spring.model.Tournament;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Locked boolean must not be null")
    private boolean locked;

    @NotNull(message = "Doubles boolean must not be null")
    private boolean doubles;

    @NotNull(message = "Tournament must not be null")
    private Tournament tournament;
}
