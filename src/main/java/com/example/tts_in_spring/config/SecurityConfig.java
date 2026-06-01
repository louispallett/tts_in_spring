package com.example.tts_in_spring.config;

import com.example.tts_in_spring.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for APIs (safe if you’re not using cookies/sessions for auth)
                .csrf(csrf -> csrf.disable())
                // Define endpoint access rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/user/create",
                                "/api/user",
                                "/api/user/{id}",
                                "/api/user/email/{email}",
                                "/api/user/login",
                                "/api/tournament/create",
                                "/api/tournament",
                                "/api/tournament/{id}",
                                "/api/category/create",
                                "/api/category",
                                "/api/category/{id}",
                                "/api/player/create",
                                "/api/player",
                                "/api/player/{id}"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
