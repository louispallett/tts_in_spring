package com.example.tts_in_spring.model;

import jakarta.persistence.*;
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
    private boolean male;

    @Column(name = "seeded", nullable = false)
    private boolean seeded;

    @Column(name = "rank", nullable = false)
    private int rank;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;
}