package com.test.ecomm.modules.auth.service;

import com.test.ecomm.common.dto.ApiResponse;
import com.test.ecomm.modules.auth.dto.AuthResponse;
import com.test.ecomm.modules.auth.dto.LoginRequest;
import com.test.ecomm.modules.auth.dto.SignupRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    ApiResponse<?> registerUser(SignupRequest signupRequest);
    ApiResponse<?> verifyCode(String email, String code);
    ApiResponse<?> resendVerificationCode(String email);
    ApiResponse<?> login(LoginRequest loginRequest, HttpServletResponse response);
    ApiResponse<AuthResponse> verifyLoginOtp(String email, String code, HttpServletResponse response);
    ApiResponse<?> logout(HttpServletRequest request,HttpServletResponse response);
    ApiResponse<AuthResponse> refreshToken(HttpServletRequest request, HttpServletResponse response);
    ApiResponse<?> forgotPassword(String email);
    ApiResponse<?> resetPassword(String token, String newPassword, String confirmPassword);


}
