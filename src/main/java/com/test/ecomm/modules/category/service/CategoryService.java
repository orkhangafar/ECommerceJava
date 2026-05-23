package com.test.ecomm.modules.category.service;

import com.test.ecomm.common.dto.PageResponse;
import com.test.ecomm.modules.category.dto.CategoryFilterRequest;
import com.test.ecomm.modules.category.dto.CategoryRequest;
import com.test.ecomm.modules.category.dto.CategoryResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(@Valid CategoryRequest categoryRequest);
    CategoryResponse updateCategory(@Valid CategoryRequest categoryRequest, Long categoryId);
    CategoryResponse deleteCategory(Long categoryId);
    PageResponse<CategoryResponse> getAllCategories(CategoryFilterRequest request);
    List<CategoryResponse> getCategoryTree();
}