package com.example.tts_in_spring.notification;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class NotificationCleanUpService {
    private final NotificationRepository notificationRepository;

    @Scheduled(cron = "0 0 3 * * *") // 3AM Every day
    @Transactional
    public void deleteOldNotifications() {
        Instant cutoff = Instant.now().minus(30, ChronoUnit.DAYS);

        notificationRepository.deleteByDateCreatedBefore(cutoff);
    }
}