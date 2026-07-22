package com.example.tts_in_spring.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    void deleteAllByUserId(Long id);
    void deleteByDateCreatedBefore(Instant cutoff);
}
