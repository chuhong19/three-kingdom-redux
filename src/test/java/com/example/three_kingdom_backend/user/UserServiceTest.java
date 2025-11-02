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

    @InjectMocks
    private UserService userService;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setUsername("testuser");
        userEntity.setEmail("test@example.com");
        userEntity.setPassword("password123");
    }

    @Test
    @DisplayName("Create user successfully")
    void testCreateUser_Success() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // When
        StandardResponse<String> response = userService.createUser(userEntity);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("200");
        assertThat(response.getMessage()).isEqualTo("User created successfully");
        assertThat(response.getData()).isNull();

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    @DisplayName("Create user fails when username already exists")
    void testCreateUser_UsernameAlreadyExists() {
        // Given
        UserEntity existingUser = new UserEntity();
        existingUser.setUsername("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(existingUser));

        // When
        StandardResponse<String> response = userService.createUser(userEntity);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("400");
        assertThat(response.getMessage()).isEqualTo("Username already exists");
        assertThat(response.getData()).isNull();

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Create user fails when email already exists")
    void testCreateUser_EmailAlreadyExists() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        UserEntity existingUser = new UserEntity();
        existingUser.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));

        // When
        StandardResponse<String> response = userService.createUser(userEntity);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("400");
        assertThat(response.getMessage()).isEqualTo("Email already exists");
        assertThat(response.getData()).isNull();

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Get user by username successfully")
    void testGetUserByUsername_Success() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(userEntity));

        // When
        UserEntity result = userService.getUserByUsername("testuser");

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
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // When
        userService.createUser(userEntity);

        // Then
        verify(userRepository, times(1)).save(argThat(user -> user.getUsername().equals("testuser") &&
                user.getEmail().equals("test@example.com") &&
                user.getPassword().equals("password123")));
    }
}
