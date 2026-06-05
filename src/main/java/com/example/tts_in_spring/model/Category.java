package com.example.tts_in_spring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "categories", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Category extends Base {
    @Column(nullable = false)
    private String name;

    @Column(name = "locked", nullable = false)
    private boolean locked;

    @Column(name = "doubles", nullable = false)
    private boolean doubles;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Player> players;

    public Category(String name, Tournament tournament, Boolean doubles) {
        this.name = name;
        this.tournament = tournament;
        this.locked = false;
        this.doubles = doubles;
    }

    @PrePersist
    protected void onCreate() {
        super.onCreate();
        this.locked = false;
    }
}
