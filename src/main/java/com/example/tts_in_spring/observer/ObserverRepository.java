package com.example.tts_in_spring.observer;

import com.example.tts_in_spring.tournament.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ObserverRepository extends JpaRepository<Observer, Long> {
    Optional<Observer> findByUserIdAndTournamentId(Long userId, Long tournamentId);
    @Query("""
        select distinct t
            from Observer o
                join o.tournament t
                    where o.user.id = :userId
    """)
    List<Tournament> findObservingTournaments(@Param("userId") Long userId);
}
