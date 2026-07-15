package com.example.tts_in_spring.user;

import java.util.List;

public class BuildUser {
    public static User buildUser() {
        return new User (
                "John",
                "Doe",
                "john.doe@example.com",
                "secret",
                false,
                false,
                null,
                List.of(),
                List.of(),
                List.of()
        );
    }
}
