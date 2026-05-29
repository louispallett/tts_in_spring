package com.example.tts_in_spring.repository;

import com.example.tts_in_spring.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {
}
