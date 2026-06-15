package com.skyworld.survey.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil            jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        log.debug(">>> JwtAuthFilter: {} {}", request.getMethod(), request.getRequestURI());
        log.debug(">>> Authorization header: {}", authHeader != null ? "present" : "MISSING");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug(">>> No Bearer token — skipping auth");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt   = authHeader.substring(7);
            final String email = jwtUtil.extractEmail(jwt);
            log.debug(">>> Extracted email from token: {}", email);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                log.debug(">>> Loaded user: {} | authorities: {}", userDetails.getUsername(), userDetails.getAuthorities());

                boolean valid = jwtUtil.isTokenValid(jwt, userDetails);
                log.debug(">>> Token valid: {}", valid);

                if (valid) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug(">>> Authentication set successfully for: {}", email);
                }
            }
        } catch (Exception e) {
            log.error(">>> JwtAuthFilter error: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }
}