package com.example.tts_in_spring.category;

import lombok.Getter;

@Getter
public enum Type {
    MEN_SINGLES("Men's Singles"),
    WOMEN_SINGLES("Women's Singles"),
    MEN_DOUBLES("Men's Doubles"),
    WOMEN_DOUBLES("Women's Doubles"),
    MIXED_DOUBLES("Mixed Doubles");

    private final String displayName;

    Type(String displayName) {
        this.displayName = displayName;
    }

}
