package com.test.ecomm.modules.category.service.impl;

import com.test.ecomm.common.constants.AppConstants;
import com.test.ecomm.common.dto.PageResponse;
import com.test.ecomm.common.exception.BadRequestException;
import com.test.ecomm.common.exception.ResourceNotFoundException;
import com.test.ecomm.modules.category.dto.CategoryFilterRequest;
import com.test.ecomm.modules.category.dto.CategoryRequest;
import com.test.ecomm.modules.category.dto.CategoryResponse;
import com.test.ecomm.modules.category.entity.Category;
import com.test.ecomm.modules.category.mapper.CategoryMapper;
import com.test.ecomm.modules.category.repository.CategoryRepository;
import com.test.ecomm.modules.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        if (categoryRepository.existsByCategoryName(categoryRequest.getCategoryName()))
            throw new BadRequestException("Bu adda kateqoriya artıq mövcuddur!");
        Category category = categoryMapper.toEntity(categoryRequest);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(CategoryRequest categoryRequest, Long categoryId) {
        if (categoryRepository.existsByCategoryNameAndCategoryIdNot(categoryRequest.getCategoryName(), categoryId))
            throw new BadRequestException("Bu adda kateqoriya artıq mövcuddur!");
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("category", "categoryId", categoryId));

        categoryMapper.updateEntity(categoryRequest, category);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryResponse deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("category", "categoryId", categoryId));

        CategoryResponse response = categoryMapper.toResponse(category);
        categoryRepository.delete(category);
        return response;
    }

    @Override
    public PageResponse<CategoryResponse> getAllCategories(CategoryFilterRequest request) {
        Sort sort = request.getSortOrder().equalsIgnoreCase(AppConstants.SORT_ASC)
                ? Sort.by(request.getSortBy()).ascending()
                : Sort.by(request.getSortBy()).descending();

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), sort);
        Page<Category> page = categoryRepository.findAll(pageable);

        List<CategoryResponse> content = page.getContent().stream()
                .map(categoryMapper::toResponse)
                .toList();

        return PageResponse.<CategoryResponse>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .lastPage(page.isLast())
                .build();
    }
}