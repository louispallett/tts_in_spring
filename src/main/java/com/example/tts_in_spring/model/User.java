package com.example.tts_in_spring.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users", schema = "public")
@Getter
@Setter
@NoArgsConstructor
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

    public User(String firstName, String lastName, String email, String password, String mobCode, String mobile) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email.toLowerCase();
        this.password = password;
        this.mobCode = mobCode;
        this.mobile = mobile;
    }

    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }
}