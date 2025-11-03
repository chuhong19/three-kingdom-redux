package com.example.three_kingdom_backend.user;

import com.example.three_kingdom_backend.util.response.StandardResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
    }

    @Test
    @DisplayName("Create user successfully")
    void testCreateUser_Success() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        StandardResponse<String> response = userService.createUser(user);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("200");
        assertThat(response.getMessage()).isEqualTo("User created successfully");
        assertThat(response.getData()).isNull();

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Create user fails when username already exists")
    void testCreateUser_UsernameAlreadyExists() {
        // Given
        User existingUser = new User();
        existingUser.setUsername("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(existingUser));

        // When
        StandardResponse<String> response = userService.createUser(user);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("400");
        assertThat(response.getMessage()).isEqualTo("Username already exists");
        assertThat(response.getData()).isNull();

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Create user fails when email already exists")
    void testCreateUser_EmailAlreadyExists() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        User existingUser = new User();
        existingUser.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));

        // When
        StandardResponse<String> response = userService.createUser(user);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("400");
        assertThat(response.getMessage()).isEqualTo("Email already exists");
        assertThat(response.getData()).isNull();

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Get user by username successfully")
    void testGetUserByUsername_Success() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // When
        User result = userService.getUserByUsername("testuser");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getPassword()).isEqualTo("password123");

        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("Get user by username throws exception when user not found")
    void testGetUserByUsername_UserNotFound() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.getUserByUsername("nonexistent"));

        assertThat(exception.getMessage()).isEqualTo("User not found");
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    @DisplayName("Verify save is called with correct user entity")
    void testCreateUser_VerifySaveCalled() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        userService.createUser(user);

        // Then
        verify(userRepository, times(1)).save(argThat(u -> u.getUsername().equals("testuser") &&
                u.getEmail().equals("test@example.com") &&
                u.getPassword().equals("encodedPassword")));
        verify(passwordEncoder, times(1)).encode("password123");
    }
}
