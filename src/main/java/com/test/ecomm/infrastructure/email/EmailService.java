package com.test.ecomm.infrastructure.email;

public interface EmailService {
    void sendVerificationEmail(String to, String code);
    void sendPasswordResetEmail(String email, String token);
    void sendLoginOtpEmail(String email, String code);
}
