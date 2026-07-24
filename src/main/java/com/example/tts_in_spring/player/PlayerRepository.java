package com.example.tts_in_spring.player;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByUserIdAndCategoryId(Long userId, Long categoryId);
    boolean existsByUserIdAndCategory_TournamentId(Long userId, Long tournamentId);
}