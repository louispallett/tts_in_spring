package com.example.tts_in_spring.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findCategoryByTournamentIdAndName(Long tournamentId, Type name);
}