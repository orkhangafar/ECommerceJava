package com.test.ecomm.infrastructure.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendVerificationEmail(String to, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("E-Commerce | Hesab Təsdiqləmə Kodu");

            String htmlContent = "<h1>Xoş gəldiniz!</h1>" +
                    "<p>Qeydiyyatınızı tamamlamaq üçün təsdiqləmə kodunuz:</p>" +
                    "<h2 style='color: #2e6c80;'>" + code + "</h2>" +
                    "<p>Bu kod 15 dəqiqə ərzində keçərlidir.</p>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Email göndərilməsi zamanı xəta: " + e.getMessage());
        }
    }

    @Override
    public void sendPasswordResetEmail(String to, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("E-Commerce | Şifrə Sıfırlama");

            String htmlContent = "<h1>Şifrə Sıfırlama</h1>" +
                    "<p>Şifrənizi sıfırlamaq üçün aşağıdakı token-i istifadə edin:</p>" +
                    "<h2 style='color: #2e6c80;'>" + token + "</h2>" +
                    "<p>Bu token 30 dəqiqə ərzində keçərlidir.</p>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Email göndərilməsi zamanı xəta: " + e.getMessage());
        }
    }

    @Override
    public void sendLoginOtpEmail(String to, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("E-Commerce | Giriş Doğrulama Kodu");

            String htmlContent = "<h1>Giriş Doğrulaması</h1>" +
                    "<p>Hesabınıza giriş üçün doğrulama kodunuz:</p>" +
                    "<h2 style='color: #2e6c80;'>" + code + "</h2>" +
                    "<p>Bu kod 5 dəqiqə ərzində keçərlidir.</p>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Email göndərilməsi zamanı xəta: " + e.getMessage());
        }
    }
}
