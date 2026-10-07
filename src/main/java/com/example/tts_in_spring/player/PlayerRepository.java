package com.example.tts_in_spring.player;

import com.example.tts_in_spring.tournament.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByUserIdAndCategoryId(Long userId, Long categoryId);
    boolean existsByUserIdAndCategory_TournamentId(Long userId, Long tournamentId);
    @Query("""
        select distinct t
            from Player p
                join p.category c
                    join c.tournament t
                        where p.user.id = :userId
    """)
    List<Tournament> findPlayingTournaments(@Param("userId") Long userId);
}