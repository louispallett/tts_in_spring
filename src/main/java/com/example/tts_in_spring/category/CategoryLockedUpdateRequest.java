package com.example.tts_in_spring.category;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryLockedUpdateRequest {
    @NotNull(message = "Locked boolean cannot be null")
    private boolean locked;
}
