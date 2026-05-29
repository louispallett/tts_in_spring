package com.example.tts_in_spring.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Entity
@Table(name = "tournaments", schema = "public")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Tournament extends Base {
   @Column(nullable = false)
   private String name;

   private enum Stage {
      SIGN_UP,
      DRAW,
      PLAY,
      FINISHED
   }

   @Enumerated(EnumType.STRING)
   @Column(nullable = false)
   private Stage stage;

   @ManyToOne
   @JoinColumn(name = "host_id")
   private User host;

   @Column(nullable = false)
   private String code;

   @Column(nullable = false)
   private Boolean showMobile;

   // NOTE: In terms of an application, it makes far more sense to allow the front end to handle the creation of the categories
   // after the tournament is finished.
   @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
   private List<Category> categories = new ArrayList<>();

   private static String generateRandomString(int len) {
      Random random = new Random();
      StringBuilder sb = new StringBuilder(len);

      for (int i = 0; i < len; i++) {
         char c = (char) ('a' + random.nextInt(26));
         sb.append(c);
      }

      return sb.toString();
   }

   public void setCode(String name) {
      String[] words = name.split("\\s+");

      String firstWord = words[0];

      if (firstWord.length() < 8) {
         this.code = firstWord + "_" + generateRandomString(8);
      } else {
         String truncatedWord = firstWord.substring(0, 8);
         this.code = truncatedWord + "_" + generateRandomString(8);
      }
   }

   @PrePersist
   protected  void onCreate() {
      super.onCreate();
      this.stage = Stage.SIGN_UP;
   }
}