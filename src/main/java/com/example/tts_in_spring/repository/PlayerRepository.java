package com.example.tts_in_spring.repository;

import com.example.tts_in_spring.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {
}