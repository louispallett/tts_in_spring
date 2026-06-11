package com.example.tts_in_spring.mapper;

import com.example.tts_in_spring.dto.Tournament.TournamentResponse;
import com.example.tts_in_spring.dto.User.UserLiteResponse;
import com.example.tts_in_spring.dto.User.UserResponse;
import com.example.tts_in_spring.model.User;

import java.util.List;
import java.util.Optional;

public class UserMapper {
    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getMobCode(),
                user.getMobile(),
                mapTournaments(user)
        );
    }

    public UserLiteResponse toLiteResponse(User user) {
        return new UserLiteResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName()
        );
    }

    private List<TournamentResponse> mapTournaments(User user) {
        return Optional.ofNullable(user.getTournaments())
                .orElse(List.of())
                .stream()
                .map(TournamentResponse::new)
                .toList();
    }
}
