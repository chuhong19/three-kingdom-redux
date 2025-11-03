package com.example.three_kingdom_backend.auth.forgotpassword;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth/forgot-password")
@RequiredArgsConstructor
public class ForgotPasswordController {
    private final ForgotPasswordService service;

    @PostMapping("/request")
    public ResponseEntity<?> request(@Valid @RequestBody ForgotPasswordRequest request) {
        service.requestOtp(request.getEmail());
        return ResponseEntity.ok(Map.of("message", "OTP sent if email exists"));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@Valid @RequestBody VerifyOTPRequest request) {
        service.verifyAndReset(request.getEmail(), request.getCode());
        return ResponseEntity.ok(Map.of("message", "Temporary password sent to email"));
    }
}
