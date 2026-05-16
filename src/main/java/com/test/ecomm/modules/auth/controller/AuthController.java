package com.test.ecomm.modules.auth.controller;

import com.test.ecomm.common.dto.ApiResponse;
import com.test.ecomm.modules.auth.dto.AuthResponse;
import com.test.ecomm.modules.auth.dto.LoginRequest;
import com.test.ecomm.modules.auth.dto.SignupRequest;
import com.test.ecomm.modules.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<?>> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        ApiResponse<?> response = authService.registerUser(signupRequest);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<?>> verifyCode(
            @RequestParam String email,
            @RequestParam String code) {

        ApiResponse<?> response = authService.verifyCode(email, code);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/resend")
    public ResponseEntity<ApiResponse<?>> resendVerificationCode(@RequestParam String email) {
        ApiResponse<?> response = authService.resendVerificationCode(email);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        ApiResponse<?> apiResponse = authService.login(loginRequest, response);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    @PostMapping("/login/verify")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyLoginOtp(
            @RequestParam String email,
            @RequestParam String code,
            HttpServletResponse response) {
        ApiResponse<AuthResponse> apiResponse = authService.verifyLoginOtp(email, code, response);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(
            HttpServletRequest request,
            HttpServletResponse response) {
        ApiResponse<?> apiResponse = authService.logout(request, response);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) {
        ApiResponse<AuthResponse> apiResponse = authService.refreshToken(request, response);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<?>> forgotPassword(
            @RequestParam String email) {
        ApiResponse<?> response = authService.forgotPassword(email);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<?>> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword) {
        ApiResponse<?> response = authService.resetPassword(token, newPassword, confirmPassword);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}

