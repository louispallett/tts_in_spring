package com.example.tts_in_spring.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "player", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Player extends Base {
    @Column(name = "male", nullable = false)
    private Boolean male;

    @Column(name = "seeded", nullable = false)
    private Boolean seeded;

    @Column(name = "rank", nullable = false)
    private int rank;

    @PrePersist
    protected void onCreate() {
        super.onCreate();
        this.seeded = false;
        this.rank = 0;
    }

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}