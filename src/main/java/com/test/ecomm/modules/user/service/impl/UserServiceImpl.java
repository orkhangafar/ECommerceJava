package com.test.ecomm.modules.user.service.impl;

import com.test.ecomm.common.constants.AppConstants;
import com.test.ecomm.common.dto.ApiResponse;
import com.test.ecomm.common.dto.PageResponse;
import com.test.ecomm.common.exception.BadRequestException;
import com.test.ecomm.common.exception.ResourceNotFoundException;
import com.test.ecomm.common.util.SecurityUtils;
import com.test.ecomm.modules.user.dto.DeactivateRequest;
import com.test.ecomm.modules.user.dto.UpdateUserRequest;
import com.test.ecomm.modules.user.dto.UserFilterRequest;
import com.test.ecomm.modules.user.dto.UserResponse;
import com.test.ecomm.modules.user.entity.AppRole;
import com.test.ecomm.modules.user.entity.Role;
import com.test.ecomm.modules.user.entity.User;
import com.test.ecomm.modules.user.repository.RoleRepository;
import com.test.ecomm.modules.user.repository.UserRepository;
import com.test.ecomm.modules.user.repository.specification.UserSpecifications;
import com.test.ecomm.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public ApiResponse<UserResponse> getMe() {
        return ApiResponse.success(mapToResponse(getCurrentUser()), "İstifadəçi məlumatları.");
    }

    @Override
    @Transactional
    public ApiResponse<UserResponse> updateMe(UpdateUserRequest request) {
        User user = getCurrentUser();
        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null && !request.getLastName().isBlank()) {
            user.setLastName(request.getLastName());
        }
        userRepository.save(user);
        return ApiResponse.success(mapToResponse(user), "Profil yeniləndi.");
    }

    @Override
    @Transactional
    public ApiResponse<String> deactivateMe(DeactivateRequest request) {
        User user = getCurrentUser();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Təqdim olunan şifrə yanlışdır. Hesab deaktiv edilə bilməz.");
        }
        user.setEnabled(false);
        userRepository.save(user);
        return ApiResponse.success(null, "Hesabınız uğurla deaktiv edildi.");
    }

    @Override
    public ApiResponse<PageResponse<UserResponse>> getAllUsers(UserFilterRequest request) {
        Sort sort = request.getSortOrder().equalsIgnoreCase(AppConstants.SORT_ASC)
                ? Sort.by(request.getSortBy()).ascending()
                : Sort.by(request.getSortBy()).descending();

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), sort);

        Specification<User> spec = Specification
                .where(UserSpecifications.hasName(request.getName()))
                .and(UserSpecifications.hasEmail(request.getEmail()))
                .and(UserSpecifications.hasRole(request.getRole()));

        Page<User> page = userRepository.findAll(spec, pageable);

        List<UserResponse> users = page.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        PageResponse<UserResponse> pageResponse = PageResponse.<UserResponse>builder()
                .content(users)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .lastPage(page.isLast())
                .build();

        return ApiResponse.success(pageResponse, "Bütün istifadəçilər.");
    }

    @Override
    public ApiResponse<UserResponse> getUserById(Long userId) {
        return ApiResponse.success(mapToResponse(findUserById(userId)), "İstifadəçi məlumatları.");
    }

    @Override
    @Transactional
    public ApiResponse<Long> deleteUser(Long userId) {
        userRepository.delete(findUserById(userId));
        return ApiResponse.success(userId, "İstifadəçi silindi.");
    }

    @Override
    @Transactional
    public ApiResponse<?> assignRole(Long userId, String roleName) {
        User user = findUserById(userId);
        Role role = roleRepository.findByRoleName(AppRole.valueOf(roleName))
                .orElseThrow(() -> new BadRequestException("Rol tapılmadı: " + roleName));

        user.getRoles().add(role);
        userRepository.save(user);
        return ApiResponse.success(mapToResponse(user), "Rol uğurla təyin edildi");
    }

    //Köməkçi metodlar-----------------

    private User getCurrentUser() {
        return findUserByEmail(SecurityUtils.getCurrentUserEmail());
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .enabled(user.isEnabled())
                .roles(user.getRoles().stream()
                        .map(r -> r.getRoleName().name())
                        .toList())
                .build();
    }
}