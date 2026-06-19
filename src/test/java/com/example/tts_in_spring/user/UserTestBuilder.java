package com.example.tts_in_spring.user;

/* UserTestBuilder
For id values, use:
- 1L (default) for host or standard user test without tournament
- 2L for authorized user who is not host (if tournament present)
- 3L for other/unauthorized user
- 9L for non-existent user
 */

import com.example.tts_in_spring.player.Player;

import java.util.List;

public class UserTestBuilder {
    private Long id = 1L;
    private String firstName = "John";
    private String lastName = "Doe";
    private String email = "john.doe@example.com";
    private String mobCode = "+44";
    private String mobile = "1234567890";
    private String password = "Hello123!";
    private List<Player> players = null;

    public static UserTestBuilder aUser() {
        return new UserTestBuilder();
    }

    public UserTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public UserTestBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserTestBuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public UserTestBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public UserTestBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public UserTestBuilder withPlayers(List<Player> players) {
        this.players = players;
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
        user.setPassword(password);

        return user;
    }
}
