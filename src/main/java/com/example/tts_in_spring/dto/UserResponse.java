package com.example.tts_in_spring.dto;

import com.example.tts_in_spring.model.User;

import java.util.List;

public class UserResponse {
    public Long id;
    public String firstName;
    public String lastName;
    public String email;
    public String mobCode;
    public String mobile;
    public List<TournamentResponse> tournaments;

    public UserResponse(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.mobCode = user.getMobCode();
        this.mobile = user.getMobile();
    }
}