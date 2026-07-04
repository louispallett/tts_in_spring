package com.example.tts_in_spring.team.dto;

import com.example.tts_in_spring.player.dto.PlayerResponse;

import java.util.List;

public record TeamsRequest(List<List<PlayerResponse>> teams) {}
