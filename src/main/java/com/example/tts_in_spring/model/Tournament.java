package com.example.tts_in_spring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tournaments", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Tournament extends Base {
   @Column(nullable = false)
   private String name;

   @Column(nullable = false)
   private String stage;

   @ManyToOne
   @JoinColumn(name = "host_id", nullable = false)
   private User host;

   @Column(nullable = false)
   private String code;

   @Column(nullable = false)
   private boolean showMobile;

   @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
   private List<Category> categories = new ArrayList<>();

   @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
   private List<Player> players = new ArrayList<>();
}