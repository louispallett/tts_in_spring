package com.example.tts_in_spring.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "categories", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category extends Base {
    @Column(unique = true, nullable = false)
    @NotBlank(message = "Name is mandatory")
    @Length(max = 20, message = "Cannot be longer than 20 characters")
    private String name;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;
}
