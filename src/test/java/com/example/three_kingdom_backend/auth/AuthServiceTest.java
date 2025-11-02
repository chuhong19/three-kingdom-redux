package com.example.three_kingdom_backend.auth;

import com.example.three_kingdom_backend.user.UserEntity;
import com.example.three_kingdom_backend.user.UserService;
import com.example.three_kingdom_backend.util.response.StandardResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthService authService;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setUsername("testuser");
        userEntity.setEmail("test@example.com");
        userEntity.setPassword("password123");
    }

    @Test
    @DisplayName("Register user successfully")
    void testRegister_Success() {
        // Given
        StandardResponse<String> expectedResponse = StandardResponse.createMessage("200", "User created successfully");
        when(userService.createUser(userEntity)).thenReturn(expectedResponse);

        // When
        StandardResponse<String> response = authService.register(userEntity);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("200");
        assertThat(response.getMessage()).isEqualTo("User created successfully");

        verify(userService, times(1)).createUser(userEntity);
    }

    @Test
    @DisplayName("Register fails when username already exists")
    void testRegister_UsernameAlreadyExists() {
        // Given
        StandardResponse<String> expectedResponse = StandardResponse.createMessage("400", "Username already exists");
        when(userService.createUser(userEntity)).thenReturn(expectedResponse);

        // When
        StandardResponse<String> response = authService.register(userEntity);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("400");
        assertThat(response.getMessage()).isEqualTo("Username already exists");

        verify(userService, times(1)).createUser(userEntity);
    }

    @Test
    @DisplayName("Register fails when email already exists")
    void testRegister_EmailAlreadyExists() {
        // Given
        StandardResponse<String> expectedResponse = StandardResponse.createMessage("400", "Email already exists");
        when(userService.createUser(userEntity)).thenReturn(expectedResponse);

        // When
        StandardResponse<String> response = authService.register(userEntity);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("400");
        assertThat(response.getMessage()).isEqualTo("Email already exists");

        verify(userService, times(1)).createUser(userEntity);
    }

    @Test
    @DisplayName("Register delegates to UserService correctly")
    void testRegister_DelegatesToUserService() {
        // Given
        StandardResponse<String> expectedResponse = StandardResponse.createMessage("200", "User created successfully");
        when(userService.createUser(any(UserEntity.class))).thenReturn(expectedResponse);

        // When
        authService.register(userEntity);

        // Then
        verify(userService, times(1)).createUser(argThat(user -> user.getUsername().equals("testuser") &&
                user.getEmail().equals("test@example.com")));
    }

    @Test
    @DisplayName("Register returns same response from UserService")
    void testRegister_ReturnsSameResponse() {
        // Given
        StandardResponse<String> userServiceResponse = StandardResponse.createMessage("200",
                "User created successfully");
        when(userService.createUser(userEntity)).thenReturn(userServiceResponse);

        // When
        StandardResponse<String> authServiceResponse = authService.register(userEntity);

        // Then
        assertThat(authServiceResponse).isSameAs(userServiceResponse);
    }
}
