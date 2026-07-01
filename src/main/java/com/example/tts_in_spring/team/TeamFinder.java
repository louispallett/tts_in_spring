package com.example.tts_in_spring.team;

import com.example.tts_in_spring.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamFinder {
    private final TeamRepository teamRepository;

    public Team getTeamOrThrow(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team " + id + " not found"));
    }
}
