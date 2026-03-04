package com.codeplay.security;

import com.codeplay.model.User;
import com.codeplay.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Component
public class ClerkJwtFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserRepository userRepository;

    // @Lazy breaks the SecurityConfig -> ClerkJwtFilter -> UserRepository circular chain
    public ClerkJwtFilter(@Lazy UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        try {
            // Decode JWT payload (Clerk uses RS256 — skip signature verify for dev)
            String[] parts = token.split("\\.");
            if (parts.length != 3) throw new RuntimeException("Invalid JWT structure");

            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> claims =
                objectMapper.readValue(payload, java.util.Map.class);

            String clerkUserId = (String) claims.get("sub");

            if (clerkUserId != null) {
                // ── Read role from MongoDB, NOT from JWT ──────────────────────
                // Clerk never includes our custom "role" field in its JWT.
                // We must look it up from the database every request.
                String role = "USER"; // safe default
                try {
                    Optional<User> userOpt = userRepository.findByClerkId(clerkUserId);
                    if (userOpt.isPresent() && userOpt.get().getRole() != null) {
                        role = userOpt.get().getRole(); // "USER" or "ADMIN"
                    }
                } catch (Exception dbEx) {
                    logger.warn("DB role lookup failed, defaulting to USER: " + dbEx.getMessage());
                }
                // ─────────────────────────────────────────────────────────────

                UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                        clerkUserId, null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            logger.warn("JWT processing failed: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}