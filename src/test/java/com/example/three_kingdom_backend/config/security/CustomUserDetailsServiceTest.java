package com.example.three_kingdom_backend.config.security;

import com.example.three_kingdom_backend.user.User;
import com.example.three_kingdom_backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailsService Tests")
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
    }

    @Test
    @DisplayName("Load user by username successfully")
    void testLoadUserByUsername_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("testuser");
        assertThat(userDetails.getPassword()).isEqualTo("encodedPassword");
        assertThat(userDetails.getAuthorities()).isNotNull();
        assertThat(userDetails.getAuthorities().isEmpty()).isTrue();

        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("Load user by username throws exception when user not found")
    void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("nonexistent"));

        assertThat(exception.getMessage()).isEqualTo("User not found: nonexistent");
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    @DisplayName("Load user by username returns user with empty authorities")
    void testLoadUserByUsername_EmptyAuthorities() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");

        assertThat(userDetails.getAuthorities()).isNotNull();
        assertThat(userDetails.getAuthorities().size()).isEqualTo(0);

        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("Load user by username uses correct password from user entity")
    void testLoadUserByUsername_PasswordMapping() {
        user.setPassword("testEncodedPassword123");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");

        assertThat(userDetails.getPassword()).isEqualTo("testEncodedPassword123");

        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("Load user by username uses correct username from user entity")
    void testLoadUserByUsername_UsernameMapping() {
        user.setUsername("differentuser");
        when(userRepository.findByUsername("differentuser")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("differentuser");

        assertThat(userDetails.getUsername()).isEqualTo("differentuser");

        verify(userRepository, times(1)).findByUsername("differentuser");
    }
}
