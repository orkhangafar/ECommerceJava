package com.test.ecomm.modules.user.controller;

import com.test.ecomm.common.dto.ApiResponse;
import com.test.ecomm.common.dto.PageResponse;
import com.test.ecomm.modules.user.dto.DeactivateRequest;
import com.test.ecomm.modules.user.dto.UpdateUserRequest;
import com.test.ecomm.modules.user.dto.UserFilterRequest;
import com.test.ecomm.modules.user.dto.UserResponse;
import com.test.ecomm.modules.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // İstifadəçi--------------------------

    @GetMapping("/users/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe() {
        return ResponseEntity.ok(userService.getMe());
    }

    @PatchMapping("/users/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateMe(
            @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateMe(request));
    }

    @PatchMapping("/users/me/deactivate")
    public ResponseEntity<ApiResponse<String>> deactivateMe(
            @Valid @RequestBody DeactivateRequest request) {
        return ResponseEntity.ok(userService.deactivateMe(request));
    }

    // Admin--------------------

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUsers(
            @Valid UserFilterRequest request) {
        return ResponseEntity.ok(userService.getAllUsers(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Long>> deleteUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(userService.deleteUser(userId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<ApiResponse<?>> assignRole(
            @PathVariable Long userId,
            @RequestParam String roleName) {
        return ResponseEntity.ok(userService.assignRole(userId,roleName));
    }
}
