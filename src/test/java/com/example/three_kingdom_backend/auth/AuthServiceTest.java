package com.example.three_kingdom_backend.auth;

import com.example.three_kingdom_backend.user.User;
import com.example.three_kingdom_backend.user.UserService;
import com.example.three_kingdom_backend.util.response.StandardResponse;
import com.example.three_kingdom_backend.config.security.JwtService;
import com.example.three_kingdom_backend.config.security.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
    }

    @Test
    @DisplayName("Register user successfully")
    void testRegister_Success() {
        StandardResponse<String> expectedResponse = StandardResponse.createMessage("200", "User created successfully");
        when(userService.createUser(any(User.class))).thenReturn(expectedResponse);

        StandardResponse<String> response = authService.register(registerRequest);

        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("200");
        assertThat(response.getMessage()).isEqualTo("User created successfully");

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    @DisplayName("Register fails when username already exists")
    void testRegister_UsernameAlreadyExists() {
        StandardResponse<String> expectedResponse = StandardResponse.createMessage("400", "Username already exists");
        when(userService.createUser(any(User.class))).thenReturn(expectedResponse);

        StandardResponse<String> response = authService.register(registerRequest);

        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("400");
        assertThat(response.getMessage()).isEqualTo("Username already exists");

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    @DisplayName("Register fails when email already exists")
    void testRegister_EmailAlreadyExists() {
        StandardResponse<String> expectedResponse = StandardResponse.createMessage("400", "Email already exists");
        when(userService.createUser(any(User.class))).thenReturn(expectedResponse);

        StandardResponse<String> response = authService.register(registerRequest);

        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("400");
        assertThat(response.getMessage()).isEqualTo("Email already exists");

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    @DisplayName("Register delegates to UserService correctly")
    void testRegister_DelegatesToUserService() {
        StandardResponse<String> expectedResponse = StandardResponse.createMessage("200", "User created successfully");
        when(userService.createUser(any(User.class))).thenReturn(expectedResponse);

        authService.register(registerRequest);

        verify(userService, times(1)).createUser(argThat(user -> user.getUsername().equals("testuser") &&
                user.getEmail().equals("test@example.com")));
    }
}
