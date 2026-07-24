package com.example.tts_in_spring.observer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ObserverRepository extends JpaRepository<Observer, Long> {
    Optional<Observer> findByUserIdAndTournamentId(Long userId, Long tournamentId);
}
