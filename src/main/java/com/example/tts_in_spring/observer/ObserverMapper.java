package com.example.tts_in_spring.observer;

import com.example.tts_in_spring.observer.dto.ObserverRequest;
import com.example.tts_in_spring.observer.dto.ObserverResponse;
import com.example.tts_in_spring.observer.dto.ObserverResponseLite;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ObserverMapper {
    @Mapping(target = "name", source = "user.fullName")
    ObserverResponse toResponse(Observer observer);

    @Mapping(target = "name", source = "user.fullName")
    ObserverResponseLite toResponseLite(Observer observer);

    @Mapping(target = "id", ignore = true)
    Observer toEntity(ObserverRequest observerRequest);
}
