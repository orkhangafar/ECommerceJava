package com.test.ecomm.modules.auth.service.impl;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import com.test.ecomm.modules.auth.entity.TokenType;
import com.test.ecomm.modules.auth.entity.VerificationToken;
import com.test.ecomm.modules.auth.repository.VerificationTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.test.ecomm.common.dto.ApiResponse;
import com.test.ecomm.common.exception.BadRequestException;
import com.test.ecomm.infrastructure.email.EmailService;
import com.test.ecomm.infrastructure.security.JwtUtils;
import com.test.ecomm.infrastructure.security.UserDetailsImpl;
import com.test.ecomm.modules.auth.dto.AuthResponse;
import com.test.ecomm.modules.auth.dto.LoginRequest;
import com.test.ecomm.modules.auth.dto.SignupRequest;
import com.test.ecomm.modules.auth.entity.RefreshToken;
import com.test.ecomm.modules.auth.repository.RefreshTokenRepository;
import com.test.ecomm.modules.auth.service.AuthService;
import com.test.ecomm.modules.user.entity.AppRole;
import com.test.ecomm.modules.user.entity.Role;
import com.test.ecomm.modules.user.entity.User;
import com.test.ecomm.modules.user.repository.RoleRepository;
import com.test.ecomm.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    @Transactional
    public ApiResponse<?> registerUser(SignupRequest signupRequest) {
        if (!signupRequest.getPassword().equals(signupRequest.getConfirmPassword())) {
            return ApiResponse.error(400, "Daxil edilən şifrələr bir-biri ilə üst-üstə düşmür", null);
        }
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ApiResponse.error(400, "Bu email artıq sistemdə var!", null);
        }
        User user = User.builder()
                .firstName(signupRequest.getFirstName())
                .lastName(signupRequest.getLastName())
                .email(signupRequest.getEmail())
                .password(encoder.encode(signupRequest.getPassword()))
                .enabled(false)
                .build();
        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("ROLE_USER tapılmadı"));
        user.setRoles(Set.of(userRole));
        userRepository.save(user);

        String code = String.format("%06d", RANDOM.nextInt(1000000));
        verificationTokenRepository.save(VerificationToken.builder()
                .user(user)
                .code(code)
                .type(TokenType.REGISTER)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .build());

        try {
            emailService.sendVerificationEmail(user.getEmail(), code);
        } catch (Exception e) {
            log.error("Email göndərmə xətası: ", e);
            throw new BadRequestException("Email göndərilməsi uğursuz oldu. Zəhmət olmasa yenidən cəhd edin.");
        }

        return ApiResponse.success(null, "Qeydiyyat uğurludur! Kodu emailinizdən yoxlayın.");
    }

    @Override
    @Transactional
    public ApiResponse<?> verifyCode(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("İstifadəçi tapılmadı"));
        VerificationToken token = verificationTokenRepository
                .findByUserAndType(user, TokenType.REGISTER)
                .orElseThrow(() -> new BadRequestException("Kod tapılmadı və ya vaxtı bitib!"));
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            verificationTokenRepository.delete(token);
            return ApiResponse.error(400, "Kodun vaxtı bitib!", null);
        }

        checkCode(token, code);

        user.setEnabled(true);
        userRepository.save(user);
        verificationTokenRepository.delete(token);
        return ApiResponse.success(null, "Hesabınız aktivləşdirildi!");
    }

    @Override
    @Transactional
    public ApiResponse<?> resendVerificationCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("İstifadəçi tapılmadı"));
        if (user.isEnabled()) {
            return ApiResponse.error(400, "Hesabınız artıq aktivdir!", null);
        }
        verificationTokenRepository.findByUserAndType(user, TokenType.REGISTER)
                .ifPresent(token -> {
                    if (token.getExpiryDate().isAfter(LocalDateTime.now())) {
                        throw new BadRequestException("Mövcud kodun vaxtı hələ bitməyib. Zəhmət olmasa gözləyin.");
                    }
                    verificationTokenRepository.delete(token);
                });

        String code = String.format("%06d", RANDOM.nextInt(1000000));
        verificationTokenRepository.save(VerificationToken.builder()
                .user(user)
                .code(code)
                .type(TokenType.REGISTER)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .build());

        try {
            emailService.sendVerificationEmail(user.getEmail(), code);
        } catch (Exception e) {
            log.error("Email göndərmə xətası: ", e);
            throw new BadRequestException("Email göndərilməsi uğursuz oldu. Zəhmət olmasa yenidən cəhd edin.");
        }
        return ApiResponse.success(null, "Yeni kod emailinizə göndərildi!");
    }

    @Override
    @Transactional
    public ApiResponse<?> login(LoginRequest loginRequest, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getEmail())
                .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı"));

        verificationTokenRepository.findByUserAndType(user, TokenType.LOGIN_2FA)
                .ifPresent(verificationTokenRepository::delete);

        String code = String.format("%06d", RANDOM.nextInt(1000000));
        verificationTokenRepository.save(VerificationToken.builder()
                .user(user)
                .code(code)
                .type(TokenType.LOGIN_2FA)
                .expiryDate(LocalDateTime.now().plusMinutes(5))
                .build());

        try {
            emailService.sendLoginOtpEmail(user.getEmail(), code);
        } catch (Exception e) {
            log.error("Email göndərmə xətası: ", e);
            throw new BadRequestException("Email göndərilməsi uğursuz oldu. Zəhmət olmasa yenidən cəhd edin.");
        }

        return ApiResponse.success(null, "Doğrulama kodu emailinizə göndərildi!");
    }

    @Override
    @Transactional
    public ApiResponse<AuthResponse> verifyLoginOtp(String email, String code, HttpServletResponse response) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("İstifadəçi tapılmadı"));
        VerificationToken token = verificationTokenRepository
                .findByUserAndType(user, TokenType.LOGIN_2FA)
                .orElseThrow(() -> new BadRequestException("Kod tapılmadı!"));
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            verificationTokenRepository.delete(token);
            return ApiResponse.error(401, "Kodun vaxtı bitib. Yenidən daxil olun.", null);
        }

        checkCode(token, code);

        verificationTokenRepository.delete(token);

        var accessCookie = jwtUtils.generateAccessCookie(user.getEmail());
        var refreshCookie = jwtUtils.generateRefreshCookie(user.getEmail());

        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.save(RefreshToken.builder()
                .user(user)
                .token(refreshCookie.getValue())
                .expiryDate(Instant.now().plusMillis(7 * 24 * 60 * 60 * 1000L))
                .build());
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        List<String> roles = user.getRoles().stream()
                .map(role -> role.getRoleName().name())
                .toList();
        return ApiResponse.success(
                AuthResponse.builder()
                        .id(user.getUserId())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .roles(roles)
                        .build(),
                "Uğurla daxil oldunuz!"
        );
    }

    @Override
    @Transactional
    public ApiResponse<?> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtUtils.getRefreshTokenFromCookie(request);
        if (refreshToken != null) {
            refreshTokenRepository.deleteByToken(refreshToken);
        }
        response.addHeader(HttpHeaders.SET_COOKIE, jwtUtils.clearAccessCookie().toString());
        response.addHeader(HttpHeaders.SET_COOKIE, jwtUtils.clearRefreshCookie().toString());
        SecurityContextHolder.clearContext();
        return ApiResponse.success(null, "Uğurla çıxış etdiniz!");
    }

    @Override
    @Transactional
    public ApiResponse<AuthResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtUtils.getRefreshTokenFromCookie(request);
        if (refreshToken == null || !jwtUtils.validateToken(refreshToken)) {
            return ApiResponse.error(401, "Refresh Token etibarsızdır. Yenidən daxil olun.", null);
        }
        RefreshToken dbToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BadRequestException("Token etibarsızdır və ya artıq istifadə edilib!"));
        if (dbToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(dbToken);
            return ApiResponse.error(401, "Seansın vaxtı bitib. Yenidən daxil olun.", null);
        }
        String email = dbToken.getUser().getEmail();
        User user = dbToken.getUser();

        var newAccessCookie = jwtUtils.generateAccessCookie(email);
        response.addHeader(HttpHeaders.SET_COOKIE, newAccessCookie.toString());

        List<String> roles = user.getRoles().stream()
                .map(role -> role.getRoleName().name())
                .toList();

        AuthResponse authResponse = AuthResponse.builder()
                .id(user.getUserId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(roles)
                .build();

        return ApiResponse.success(authResponse, "Token yeniləndi!");
    }

    @Override
    @Transactional
    public ApiResponse<?> forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Bu email ilə istifadəçi tapılmadı"));

        if (!user.isEnabled()) {
            return ApiResponse.error(400, "Hesabınız aktiv deyil!", null);
        }

        verificationTokenRepository.findByUserAndType(user, TokenType.RESET_PASSWORD)
                .ifPresent(verificationTokenRepository::delete);

        String token = UUID.randomUUID().toString();
        verificationTokenRepository.save(VerificationToken.builder()
                .user(user)
                .code(token)
                .type(TokenType.RESET_PASSWORD)
                .expiryDate(LocalDateTime.now().plusMinutes(30))
                .build());

        try {
            emailService.sendPasswordResetEmail(user.getEmail(), token);
        } catch (Exception e) {
            log.error("Email göndərmə xətası: ", e);
            throw new BadRequestException("Email göndərilməsi uğursuz oldu. Zəhmət olmasa yenidən cəhd edin.");
        }

        return ApiResponse.success(null, "Şifrə sıfırlama linki emailinizə göndərildi!");
    }

    @Override
    @Transactional
    public ApiResponse<?> resetPassword(String token, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            return ApiResponse.error(400, "Daxil edilən şifrələr bir-biri ilə üst-üstə düşmür", null);
        }

        VerificationToken verificationToken = verificationTokenRepository
                .findByCodeAndType(token, TokenType.RESET_PASSWORD)
                .orElseThrow(() -> new BadRequestException("Token etibarsızdır!"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            verificationTokenRepository.delete(verificationToken);
            return ApiResponse.error(400, "Tokenin vaxtı bitib. Yenidən cəhd edin.", null);
        }

        User user = verificationToken.getUser();
        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);

        return ApiResponse.success(null, "Şifrəniz uğurla dəyişdirildi!");
    }

    //Köməkçi metodlar--------------------

    private void checkCode(VerificationToken token, String code) {
        if (token.getBlockedUntil() != null && token.getBlockedUntil().isAfter(LocalDateTime.now())) {
            throw new BadRequestException("Çox sayda yanlış cəhd. 15 dəqiqə sonra yenidən cəhd edin.");
        }

        if (!token.getCode().equals(code)) {
            token.setAttempts(token.getAttempts() + 1);
            if (token.getAttempts() >= 3) {
                token.setBlockedUntil(LocalDateTime.now().plusMinutes(15));
            }
            verificationTokenRepository.save(token);
            throw new BadRequestException("Kod yanlışdır!");
        }
    }
}


