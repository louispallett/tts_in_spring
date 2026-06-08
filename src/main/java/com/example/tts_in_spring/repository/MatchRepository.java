package com.example.tts_in_spring.repository;

import com.example.tts_in_spring.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long> {
}
