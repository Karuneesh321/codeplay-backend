package com.codeplay.config;

import com.codeplay.security.ClerkJwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ClerkJwtFilter clerkJwtFilter;

    // Reads CORS_ALLOWED_ORIGINS from Railway env variable
    // Falls back to localhost for local development
   @Value("${CORS_ALLOWED_ORIGINS:http://localhost:3000}")
private String corsAllowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/favicon.ico", "/favicon.png").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/quiz/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/puzzle/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/user/leaderboard").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(clerkJwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Build allowed origins list — always include localhost + any from env var
        List<String> origins = new ArrayList<>();
        origins.add("http://localhost:3000");
        origins.add("http://localhost:5173");

        // Parse comma-separated origins from CORS_ALLOWED_ORIGINS env variable
        if (corsAllowedOrigins != null && !corsAllowedOrigins.isBlank()) {
            Arrays.stream(corsAllowedOrigins.split(","))
                  .map(String::trim)
                  .filter(s -> !s.isBlank())
                  .forEach(origins::add);
        }

        config.setAllowedOrigins(origins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
