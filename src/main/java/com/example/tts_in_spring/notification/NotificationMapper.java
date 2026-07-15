package com.example.tts_in_spring.notification;

import com.example.tts_in_spring.notification.dto.NotificationResponse;
import com.example.tts_in_spring.notification.dto.NotificationRequest;
import com.example.tts_in_spring.notification.dto.NotificationResponseLite;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationResponse toResponse(Notification notification);

    NotificationResponseLite toResponseLite(Notification notification);

    @Mapping(target = "id", ignore = true)
    Notification toEntity(NotificationRequest request);
}
