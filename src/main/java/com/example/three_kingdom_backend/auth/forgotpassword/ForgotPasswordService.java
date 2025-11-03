package com.example.three_kingdom_backend.auth.forgotpassword;

import com.example.three_kingdom_backend.mail.MailService;
import com.example.three_kingdom_backend.user.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class ForgotPasswordService {
    private final UserRepository userRepository;
    private final PasswordResetOTPRepository passwordResetOTPRepository;
    private final MailService mailService;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private final SecureRandom random = new SecureRandom();

    private String generateOtp6() {
        int n = random.nextInt(1_000_000);
        return String.format("%06d", n);
    }

    private String generateTempPassword(int len) {
        String dict = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789@#$%";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(dict.charAt(random.nextInt(dict.length())));
        return sb.toString();
    }

    @Transactional
    public void requestOtp(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email does not exist"));

        var otp = generateOtp6();
        var codeHash = encoder.encode(otp);

        var entity = new PasswordResetOTP();
        entity.setUser(user);
        entity.setCodeHash(codeHash);
        entity.setExpiresAt(Instant.now().plus(10, ChronoUnit.MINUTES));
        passwordResetOTPRepository.save(entity);

        mailService.send(user.getEmail(),
                "Password Reset Verification Code",
                "Your OTP code: " + otp + " (expires in 10 minutes).");
    }

    @Transactional
    public void verifyAndReset(String email, String codeInput) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email does not exist"));

        var candidates = passwordResetOTPRepository.findActive(user, Instant.now());
        if (candidates.isEmpty())
            throw new IllegalStateException("No valid code or code has expired");

        var otp = candidates.get(0);
        if (otp.getAttempts() >= otp.getMaxAttempts())
            throw new IllegalStateException("You have exceeded the maximum attempts, please request a new code");

        boolean ok = encoder.matches(codeInput, otp.getCodeHash());
        otp.setAttempts(otp.getAttempts() + 1);

        if (!ok) {
            passwordResetOTPRepository.save(otp);
            throw new IllegalArgumentException("Invalid code");
        }

        otp.setUsed(true);
        passwordResetOTPRepository.save(otp);

        var tempPwd = generateTempPassword(14);
        user.setPassword(encoder.encode(tempPwd));
        userRepository.save(user);

        mailService.send(user.getEmail(),
                "Temporary Password",
                "Your temporary password is: " + tempPwd +
                        "\nPlease login and change your password immediately in settings.");
    }
}
