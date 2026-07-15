package com.example.tts_in_spring.notification;

import com.example.tts_in_spring.exception.ForbiddenException;
import com.example.tts_in_spring.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationFinder {
    private final NotificationRepository notificationRepository;

    public Notification getNotificationOrThrow(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification " + id + " not found"));
    }

    public void assertUser(Notification notification, Long userId) {
        if (!notification.getUser().getId().equals(userId)) {
            throw new ForbiddenException(
                    "Not user of notification " + notification.getId()
            );
        }
    }
}
