package com.example.tts_in_spring.user;

import com.example.tts_in_spring.base.Base;
import com.example.tts_in_spring.player.Player;
import com.example.tts_in_spring.tournament.Tournament;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends Base {
    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    // Access.WRITE_ONLY ensures that we don't pass this over in GET mappings.
    // Even though the password is hashed, it's still best not to expose it.
    // This is an INCREDIBLY useful feature
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(nullable = false)
    private String mobCode;

    @Column(nullable = false)
    private String mobile;

    @Column
    private Boolean deleted;

    @Column
    private Instant deletedAt;

    @OneToMany(mappedBy = "host", fetch = FetchType.LAZY)
    private List<Tournament> tournaments = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Player> players = new ArrayList<>();
}