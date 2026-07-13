package com.example.tts_in_spring.category;

import com.example.tts_in_spring.base.Base;
import com.example.tts_in_spring.match.Match;
import com.example.tts_in_spring.player.Player;
import com.example.tts_in_spring.team.Team;
import com.example.tts_in_spring.tournament.Tournament;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Category extends Base {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type name;

    @Column(nullable = false)
    private boolean locked;

    @Column(nullable = false)
    private boolean doubles;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Player> players = new ArrayList<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Team> teams = new ArrayList<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Match> matches = new ArrayList<>();
}
