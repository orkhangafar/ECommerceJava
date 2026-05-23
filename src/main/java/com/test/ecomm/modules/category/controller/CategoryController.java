package com.test.ecomm.modules.category.controller;

import com.test.ecomm.common.dto.ApiResponse;
import com.test.ecomm.common.dto.PageResponse;
import com.test.ecomm.modules.category.dto.CategoryFilterRequest;
import com.test.ecomm.modules.category.dto.CategoryRequest;
import com.test.ecomm.modules.category.dto.CategoryResponse;
import com.test.ecomm.modules.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CategoryRequest categoryRequest) {

        CategoryResponse created = categoryService.createCategory(categoryRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Kateqoriya uğurla yaradıldı"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @Valid @RequestBody CategoryRequest categoryRequest,
            @PathVariable Long categoryId) {

        CategoryResponse updated = categoryService.updateCategory(categoryRequest, categoryId);
        return ResponseEntity.ok(ApiResponse.success(updated, "Kateqoriya uğurla yeniləndi"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> deleteCategory(
            @PathVariable Long categoryId) {

        CategoryResponse deleted = categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success(deleted, "Kateqoriya uğurla silindi"));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<PageResponse<CategoryResponse>>> getAllCategories(
            @Valid CategoryFilterRequest request) {

        PageResponse<CategoryResponse> response = categoryService.getAllCategories(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Kateqoriyalar uğurla gətirildi"));
    }

    @GetMapping("/categories/tree")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategoryTree() {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getCategoryTree(), "Kateqoriya ağacı"));
    }
}