package com.example.tts_in_spring.category;

import com.example.tts_in_spring.base.Base;
import com.example.tts_in_spring.match.Match;
import com.example.tts_in_spring.player.Player;
import com.example.tts_in_spring.tournament.Tournament;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Category extends Base {
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "locked", nullable = false)
    private boolean locked;

    @Column(name = "doubles", nullable = false)
    private boolean doubles;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Player> players = new ArrayList<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Match> matches = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        super.onCreate();
        this.locked = false;
    }
}
