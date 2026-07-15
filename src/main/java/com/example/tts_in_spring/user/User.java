package com.example.tts_in_spring.user;

import com.example.tts_in_spring.base.Base;
import com.example.tts_in_spring.notification.Notification;
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

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(nullable = false)
    private boolean receivesEmails = true;

    @Column(nullable = false)
    private Boolean deleted = false;

    @Column
    private Instant deletedAt;

    @OneToMany(mappedBy = "host", fetch = FetchType.LAZY)
    private List<Tournament> tournaments = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Player> players = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Notification> notifications = new ArrayList<>();

    // @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    // private List<Observer> observers = new ArrayList<>();

    public String getFullName() {
        return firstName + " " + lastName;
    }
}