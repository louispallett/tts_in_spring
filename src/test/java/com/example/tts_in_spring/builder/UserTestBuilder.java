package com.example.tts_in_spring.builder;

import com.example.tts_in_spring.model.User;

public class UserTestBuilder {
    private final Long id = 1L;
    private final String firstName = "John";
    private final String lastName = "Doe";
    private String email = "john.doe@example.com";
    private final String mobCode = "+44";
    private final String mobile = "1234567890";

    public static UserTestBuilder aUser() {
        return new UserTestBuilder();
    }

    public UserTestBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public User build() {
        User user = new User();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setMobCode(mobCode);
        user.setMobile(mobile);

        return user;
    }
}
