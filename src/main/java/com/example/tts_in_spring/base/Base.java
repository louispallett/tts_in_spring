package com.example.tts_in_spring.base;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@MappedSuperclass
@Getter
@Setter
public abstract class Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private Instant dateCreated;

    @Column(nullable = false)
    private boolean active;

    @PrePersist
    protected void onCreate() {
        this.dateCreated = Instant.now();
        this.active = true;
    }
}