package com.test.ecomm.modules.user.service;

import com.test.ecomm.common.dto.ApiResponse;
import com.test.ecomm.common.dto.PageResponse;
import com.test.ecomm.modules.user.dto.DeactivateRequest;
import com.test.ecomm.modules.user.dto.UpdateUserRequest;
import com.test.ecomm.modules.user.dto.UserFilterRequest;
import com.test.ecomm.modules.user.dto.UserResponse;

public interface UserService {
    ApiResponse<UserResponse> getMe();
    ApiResponse<UserResponse> updateMe(UpdateUserRequest request);
    ApiResponse<String> deactivateMe(DeactivateRequest request);
    ApiResponse<PageResponse<UserResponse>> getAllUsers(UserFilterRequest request);
    ApiResponse<UserResponse> getUserById(Long userId);
    ApiResponse<Long> deleteUser(Long userId);
    ApiResponse<?> assignRole(Long userId, String roleName);
}