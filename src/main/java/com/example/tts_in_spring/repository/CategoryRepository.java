package com.example.tts_in_spring.repository;

import com.example.tts_in_spring.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}