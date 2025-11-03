package com.example.three_kingdom_backend.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtService Tests")
class JwtServiceTest {

    private JwtService jwtService;
    private String secret = "your-256-bit-secret-key-must-be-at-least-32-characters-long-for-hs256-algorithm-please-change-in-production";
    private Long expiration = 86400000L;
    private Long refreshExpiration = 604800000L;

    private org.springframework.security.core.userdetails.UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", secret);
        ReflectionTestUtils.setField(jwtService, "expiration", expiration);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", refreshExpiration);

        userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("testuser")
                .password("password")
                .authorities(new java.util.ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Generate token successfully")
    void testGenerateToken_Success() {
        String token = jwtService.generateToken(userDetails);

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }

    @Test
    @DisplayName("Generate refresh token successfully")
    void testGenerateRefreshToken_Success() {
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken).isNotEmpty();
    }

    @Test
    @DisplayName("Extract username from token")
    void testExtractUsername_Success() {
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);

        assertThat(username).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Extract expiration from token")
    void testExtractExpiration_Success() {
        String token = jwtService.generateToken(userDetails);
        Date expirationDate = jwtService.extractExpiration(token);

        assertThat(expirationDate).isNotNull();
        assertThat(expirationDate.after(new Date())).isTrue();
    }

    @Test
    @DisplayName("Validate token with correct user details")
    void testValidateToken_ValidToken() {
        String token = jwtService.generateToken(userDetails);
        Boolean isValid = jwtService.validateToken(token, userDetails);

        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Validate token fails with wrong user details")
    void testValidateToken_WrongUser() {
        String token = jwtService.generateToken(userDetails);

        org.springframework.security.core.userdetails.UserDetails wrongUser = org.springframework.security.core.userdetails.User
                .builder()
                .username("wronguser")
                .password("password")
                .authorities(new java.util.ArrayList<>())
                .build();

        Boolean isValid = jwtService.validateToken(token, wrongUser);

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Check if token is refresh token")
    void testIsRefreshToken_Success() {
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        Boolean isRefresh = jwtService.isRefreshToken(refreshToken);

        assertThat(isRefresh).isTrue();
    }

    @Test
    @DisplayName("Check if access token is not refresh token")
    void testIsRefreshToken_AccessToken() {
        String accessToken = jwtService.generateToken(userDetails);
        Boolean isRefresh = jwtService.isRefreshToken(accessToken);

        assertThat(isRefresh).isFalse();
    }

    @Test
    @DisplayName("Check if expired token is detected")
    void testIsTokenExpired_ExpiredToken() throws InterruptedException {
        // Create a token with very short expiration (1 second)
        JwtService shortExpirationService = new JwtService();
        ReflectionTestUtils.setField(shortExpirationService, "secret", secret);
        ReflectionTestUtils.setField(shortExpirationService, "expiration", 1000L);
        ReflectionTestUtils.setField(shortExpirationService, "refreshExpiration", refreshExpiration);

        String token = shortExpirationService.generateToken(userDetails);

        // Wait for token to expire
        Thread.sleep(1100);

        Boolean isExpired = shortExpirationService.isTokenExpired(token);
        assertThat(isExpired).isTrue();
    }

    @Test
    @DisplayName("Check if valid token is not expired")
    void testIsTokenExpired_ValidToken() {
        String token = jwtService.generateToken(userDetails);
        Boolean isExpired = jwtService.isTokenExpired(token);

        assertThat(isExpired).isFalse();
    }

    @Test
    @DisplayName("Extract claim from token")
    void testExtractClaim_Success() {
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractClaim(token, Claims::getSubject);

        assertThat(username).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Handle invalid token gracefully")
    void testInvalidToken_HandlesGracefully() {
        String invalidToken = "invalid.token.here";

        Boolean isExpired = jwtService.isTokenExpired(invalidToken);
        assertThat(isExpired).isTrue();

        Boolean isRefresh = jwtService.isRefreshToken(invalidToken);
        assertThat(isRefresh).isFalse();
    }
}
