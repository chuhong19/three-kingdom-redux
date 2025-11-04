package com.example.three_kingdom_backend.config.security;

import com.example.three_kingdom_backend.config.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        String requestPath = request.getRequestURI();
        String requestMethod = request.getMethod();

        logger.info("JWT Filter - Request: " + requestMethod + " " + requestPath);

        if (requestPath.startsWith("/api/auth/forgot-password") ||
                requestPath.startsWith("/api/auth/register") ||
                requestPath.startsWith("/api/auth/login") ||
                requestPath.startsWith("/api/auth/refresh")) {
            logger.debug("JWT Filter - Skipping auth endpoints");
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("JWT Filter - No Authorization header or invalid format. Header: " +
                    (authHeader != null ? authHeader.substring(0, Math.min(20, authHeader.length())) + "..." : "null"));
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        logger.debug("JWT Filter - Token received (length: " + jwt.length() + ")");

        try {
            username = jwtService.extractUsername(jwt);
            logger.info("JWT Filter - Extracted username: " + username);

            if (username == null) {
                logger.warn("JWT Filter - Username is null after extraction");
                filterChain.doFilter(request, response);
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                logger.debug("JWT Filter - Authentication already set, skipping");
                filterChain.doFilter(request, response);
                return;
            }

            logger.debug("JWT Filter - Loading user details for: " + username);
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            logger.debug("JWT Filter - User details loaded: " + userDetails.getUsername());

            boolean isValidToken = jwtService.validateToken(jwt, userDetails);
            boolean isRefresh = jwtService.isRefreshToken(jwt);

            logger.info("JWT Filter - Token validation - isValid: " + isValidToken + ", isRefresh: " + isRefresh);

            if (isValidToken && !isRefresh) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info("JWT Filter - Authentication set successfully for user: " + username);
            } else {
                logger.warn("JWT Filter - Token validation failed for user: " + username +
                        " - isValidToken: " + isValidToken + ", isRefresh: " + isRefresh);
            }
        } catch (Exception e) {
            logger.error("JWT Filter - Exception during JWT validation: " + e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }
}
