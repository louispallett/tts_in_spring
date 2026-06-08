package com.example.tts_in_spring.repository;

import com.example.tts_in_spring.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
}
