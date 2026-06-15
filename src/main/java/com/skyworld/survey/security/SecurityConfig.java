package com.skyworld.survey.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter      jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // ── Public endpoints ──────────────────────────────
                        .requestMatchers("/api/auth/**").permitAll()

                        // ── Survey read — both roles ──────────────────────
                        // No trailing slash — matches exactly /api/surveys
                        .requestMatchers(HttpMethod.GET, "/api/surveys").hasAnyRole("ADMIN", "USER")
                        // With ID — matches /api/surveys/1, /api/surveys/2 etc.
                        .requestMatchers(HttpMethod.GET, "/api/surveys/{id}").hasAnyRole("ADMIN", "USER")

                        // ── Survey write — ADMIN only ─────────────────────
                        .requestMatchers(HttpMethod.POST,   "/api/surveys").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/surveys/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/surveys/{id}").hasRole("ADMIN")

                        // ── Questions read — both roles ───────────────────
                        .requestMatchers(HttpMethod.GET,
                                "/api/surveys/{id}/questions").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET,
                                "/api/surveys/{id}/questions/{qid}").hasAnyRole("ADMIN", "USER")

                        // ── Questions write — ADMIN only ──────────────────
                        .requestMatchers(HttpMethod.POST,
                                "/api/surveys/{id}/questions").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/surveys/{id}/questions/{qid}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/surveys/{id}/questions/{qid}").hasRole("ADMIN")

                        // ── Submit response — both roles ──────────────────
                        .requestMatchers(HttpMethod.POST,
                                "/api/surveys/{id}/responses").hasAnyRole("ADMIN", "USER")

                        // ── View responses + certificates — ADMIN only ────
                        .requestMatchers(HttpMethod.GET,
                                "/api/surveys/{id}/responses").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,
                                "/api/certificates/{id}").hasRole("ADMIN")

                        // ── Everything else requires authentication ────────
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}