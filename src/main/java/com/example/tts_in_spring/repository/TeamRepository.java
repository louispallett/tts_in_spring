package com.example.tts_in_spring.repository;

import com.example.tts_in_spring.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
