package com.example.three_kingdom_backend.auth.forgotpassword;

import com.example.three_kingdom_backend.mail.MailService;
import com.example.three_kingdom_backend.user.User;
import com.example.three_kingdom_backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ForgotPasswordService Tests")
class ForgotPasswordServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetOTPRepository passwordResetOTPRepository;

    @Mock
    private MailService mailService;

    @InjectMocks
    private ForgotPasswordService forgotPasswordService;

    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    private User user;

    @BeforeEach
    void setUp() {
        passwordEncoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
    }

    @Test
    @DisplayName("Request OTP successfully")
    void testRequestOtp_Success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordResetOTPRepository.save(any(PasswordResetOTP.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        forgotPasswordService.requestOtp("test@example.com");

        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(passwordResetOTPRepository, times(1)).save(any(PasswordResetOTP.class));
        verify(mailService, times(1)).send(eq("test@example.com"), anyString(), anyString());
    }

    @Test
    @DisplayName("Request OTP fails when email does not exist")
    void testRequestOtp_EmailNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            forgotPasswordService.requestOtp("nonexistent@example.com");
        });

        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
        verify(passwordResetOTPRepository, never()).save(any(PasswordResetOTP.class));
        verify(mailService, never()).send(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Verify OTP and reset password successfully")
    void testVerifyAndReset_Success() {
        String validCode = "123456";
        String codeHash = passwordEncoder.encode(validCode);

        PasswordResetOTP otp = new PasswordResetOTP();
        otp.setId(1L);
        otp.setUser(user);
        otp.setCodeHash(codeHash);
        otp.setExpiresAt(Instant.now().plusSeconds(600));
        otp.setAttempts(0);
        otp.setMaxAttempts(5);
        otp.setUsed(false);

        List<PasswordResetOTP> activeOtps = new ArrayList<>();
        activeOtps.add(otp);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordResetOTPRepository.findActive(eq(user), any(Instant.class))).thenReturn(activeOtps);
        when(passwordResetOTPRepository.save(any(PasswordResetOTP.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        forgotPasswordService.verifyAndReset("test@example.com", validCode);

        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(passwordResetOTPRepository, times(1)).findActive(eq(user), any(Instant.class));
        verify(passwordResetOTPRepository, times(1)).save(any(PasswordResetOTP.class));
        verify(userRepository, times(1)).save(any(User.class));
        verify(mailService, times(1)).send(eq("test@example.com"), anyString(), anyString());
        assertThat(otp.isUsed()).isTrue();
    }

    @Test
    @DisplayName("Verify OTP fails when email does not exist")
    void testVerifyAndReset_EmailNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            forgotPasswordService.verifyAndReset("nonexistent@example.com", "123456");
        });

        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
        verify(passwordResetOTPRepository, never()).findActive(any(), any());
    }

    @Test
    @DisplayName("Verify OTP fails when no valid code exists")
    void testVerifyAndReset_NoValidCode() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordResetOTPRepository.findActive(eq(user), any(Instant.class))).thenReturn(new ArrayList<>());

        assertThrows(IllegalStateException.class, () -> {
            forgotPasswordService.verifyAndReset("test@example.com", "123456");
        });

        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(passwordResetOTPRepository, times(1)).findActive(eq(user), any(Instant.class));
    }

    @Test
    @DisplayName("Verify OTP fails when maximum attempts exceeded")
    void testVerifyAndReset_MaxAttemptsExceeded() {
        PasswordResetOTP otp = new PasswordResetOTP();
        otp.setId(1L);
        otp.setUser(user);
        otp.setCodeHash("encodedCodeHash");
        otp.setExpiresAt(Instant.now().plusSeconds(600));
        otp.setAttempts(5);
        otp.setMaxAttempts(5);
        otp.setUsed(false);

        List<PasswordResetOTP> activeOtps = new ArrayList<>();
        activeOtps.add(otp);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordResetOTPRepository.findActive(eq(user), any(Instant.class))).thenReturn(activeOtps);

        assertThrows(IllegalStateException.class, () -> {
            forgotPasswordService.verifyAndReset("test@example.com", "123456");
        });

        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(passwordResetOTPRepository, times(1)).findActive(eq(user), any(Instant.class));
        verify(passwordResetOTPRepository, never()).save(any(PasswordResetOTP.class));
    }

    @Test
    @DisplayName("Verify OTP fails when code is invalid")
    void testVerifyAndReset_InvalidCode() {
        String invalidCode = "000000";
        String validCode = "123456";
        String codeHash = passwordEncoder.encode(validCode);

        PasswordResetOTP otp = new PasswordResetOTP();
        otp.setId(1L);
        otp.setUser(user);
        otp.setCodeHash(codeHash);
        otp.setExpiresAt(Instant.now().plusSeconds(600));
        otp.setAttempts(0);
        otp.setMaxAttempts(5);
        otp.setUsed(false);

        List<PasswordResetOTP> activeOtps = new ArrayList<>();
        activeOtps.add(otp);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordResetOTPRepository.findActive(eq(user), any(Instant.class))).thenReturn(activeOtps);
        when(passwordResetOTPRepository.save(any(PasswordResetOTP.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertThrows(IllegalArgumentException.class, () -> {
            forgotPasswordService.verifyAndReset("test@example.com", invalidCode);
        });

        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(passwordResetOTPRepository, times(1)).findActive(eq(user), any(Instant.class));
        verify(passwordResetOTPRepository, times(1)).save(any(PasswordResetOTP.class));
        verify(userRepository, never()).save(any(User.class));
        verify(mailService, never()).send(anyString(), anyString(), anyString());
        assertThat(otp.getAttempts()).isEqualTo(1);
    }
}
