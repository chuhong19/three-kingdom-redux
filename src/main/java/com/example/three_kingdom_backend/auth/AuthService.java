package com.example.three_kingdom_backend.auth;

import com.example.three_kingdom_backend.user.UserEntity;
import com.example.three_kingdom_backend.user.UserRepository;
import com.example.three_kingdom_backend.user.UserService;
import com.example.three_kingdom_backend.util.response.StandardResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuthService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserService userService,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RefreshTokenRepository refreshTokenRepository,
            CustomUserDetailsService userDetailsService,
            AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
    }

    public StandardResponse<String> register(UserEntity data) {
        return userService.createUser(data);
    }

    public StandardResponse<AuthResponse> login(LoginRequest loginRequest) {
        try {
            // Authenticate user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            // Load user details
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
            UserEntity user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Generate tokens
            String accessToken = jwtService.generateToken(userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails);

            // Save refresh token
            saveRefreshToken(user, refreshToken);

            AuthResponse authResponse = new AuthResponse(accessToken, refreshToken, "Bearer");
            return StandardResponse.create("200", authResponse);
        } catch (org.springframework.security.core.AuthenticationException e) {
            return StandardResponse.createMessage("401", "Invalid username or password");
        } catch (Exception e) {
            return StandardResponse.createMessage("500", "Login failed: " + e.getMessage());
        }
    }

    @Transactional
    public StandardResponse<AuthResponse> refreshToken(RefreshTokenRequest request) {
        try {
            String refreshTokenString = request.getRefreshToken();

            // Validate refresh token format
            if (!jwtService.isRefreshToken(refreshTokenString)) {
                return StandardResponse.createMessage("401", "Invalid refresh token");
            }

            // Find refresh token in database
            RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenString)
                    .orElseThrow(() -> new RuntimeException("Refresh token not found"));

            // Check if token is revoked
            if (refreshToken.getRevoked()) {
                return StandardResponse.createMessage("401", "Refresh token has been revoked");
            }

            // Check if token is expired
            if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
                refreshTokenRepository.delete(refreshToken);
                return StandardResponse.createMessage("401", "Refresh token has expired");
            }

            // Load user details
            UserDetails userDetails = userDetailsService.loadUserByUsername(refreshToken.getUser().getUsername());

            // Generate new access token
            String newAccessToken = jwtService.generateToken(userDetails);

            // Optionally rotate refresh token (generate new one)
            String newRefreshToken = jwtService.generateRefreshToken(userDetails);

            // Revoke old refresh token and save new one
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
            saveRefreshToken(refreshToken.getUser(), newRefreshToken);

            AuthResponse authResponse = new AuthResponse(newAccessToken, newRefreshToken, "Bearer");
            return StandardResponse.create("200", authResponse);
        } catch (RuntimeException e) {
            return StandardResponse.createMessage("401", "Invalid refresh token");
        } catch (Exception e) {
            return StandardResponse.createMessage("500", "Token refresh failed: " + e.getMessage());
        }
    }

    @Transactional
    public StandardResponse<String> logout(String refreshToken) {
        try {
            RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                    .orElseThrow(() -> new RuntimeException("Refresh token not found"));

            token.setRevoked(true);
            refreshTokenRepository.save(token);

            return StandardResponse.createMessage("200", "Logout successful");
        } catch (Exception e) {
            return StandardResponse.createMessage("400", "Logout failed: " + e.getMessage());
        }
    }

    private void saveRefreshToken(UserEntity user, String token) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(token);
        refreshToken.setExpiryDate(Instant.now().plusMillis(604800000)); // 7 days
        refreshToken.setRevoked(false);
        refreshTokenRepository.save(refreshToken);
    }
}
