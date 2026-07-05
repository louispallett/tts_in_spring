package com.example.tts_in_spring.user;

import java.util.List;

public class BuildUser {
    public static User buildUser() {
        return new User (
                "John",
                "Doe",
                "john.doe@example.com",
                "secret",
                "44",
                "123456789",
                false,
                null,
                null,
                List.of(),
                List.of()
        );
    }
}
