package com.test.ecomm.modules.product.service.impl;

import com.test.ecomm.common.dto.PageResponse;
import com.test.ecomm.common.exception.ResourceNotFoundException;
import com.test.ecomm.common.util.SecurityUtils;
import com.test.ecomm.modules.category.entity.Category;
import com.test.ecomm.modules.category.repository.CategoryRepository;
import com.test.ecomm.modules.product.dto.ProductFilterRequest;
import com.test.ecomm.modules.product.dto.ProductRequest;
import com.test.ecomm.modules.product.dto.ProductResponse;
import com.test.ecomm.modules.product.entity.Product;
import com.test.ecomm.modules.product.mapper.ProductMapper;
import com.test.ecomm.modules.product.repository.ProductRepository;
import com.test.ecomm.modules.product.repository.specification.ProductSpecifications;
import com.test.ecomm.modules.product.service.ProductService;
import com.test.ecomm.infrastructure.file.FileService;
import com.test.ecomm.modules.user.entity.User;
import com.test.ecomm.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ProductMapper productMapper;
    private final FileService fileService;

    @Value("${project.image}")
    private String path;

    @Override
    @Transactional
    public ProductResponse addProduct(Long categoryId, ProductRequest request) {

        String email = SecurityUtils.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        Product product = productMapper.toEntity(request);

        product.setCategory(category);
        product.setCreatedBy(user);

        if (product.getImage() == null || product.getImage().isBlank()) {
            product.setImage("default.png");
        }
        product.setSpecialPrice(
                calculateSpecialPrice(product.getPrice(), product.getDiscount())
        );
        Product saved = productRepository.save(product);
        log.info("Product created: {} by {}", saved.getProductId(), email);
        return productMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long productId, ProductRequest request) {
        Product product = findProduct(productId);
        checkOwnership(product);

        productMapper.updateEntity(request, product);
        product.setSpecialPrice(calculateSpecialPrice(product.getPrice(), product.getDiscount()));

        Product updated = productRepository.save(product);
        return productMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public ProductResponse deleteProduct(Long productId) {
        Product product = findProduct(productId);
        checkOwnership(product);
        fileService.deleteImage(path, product.getImage());
        ProductResponse response = productMapper.toResponse(product);
        productRepository.delete(product);
        return response;
    }

    @Override
    public ProductResponse getProductById(Long productId) {
        return productMapper.toResponse(findProduct(productId));
    }

    @Override
    @Transactional
    public ProductResponse updateProductImage(Long productId, MultipartFile image) {
        Product product = findProduct(productId);
        checkOwnership(product);
        fileService.deleteImage(path, product.getImage());
        String fileName = fileService.uploadImage(path, image);
        product.setImage(fileName);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    public PageResponse<ProductResponse> getAllProducts(ProductFilterRequest request) {
        Sort sort = request.getSortOrder().equalsIgnoreCase("asc")
                ? Sort.by(request.getSortBy()).ascending()
                : Sort.by(request.getSortBy()).descending();

        Pageable pageable = PageRequest.of(
                request.getPageNumber(),
                request.getPageSize(),
                sort
        );

        Specification<Product> spec = Specification
                .where(ProductSpecifications.hasName(request.getName()))
                .and(ProductSpecifications.hasPriceBetween(request.getMinPrice(), request.getMaxPrice()))
                .and(ProductSpecifications.hasCategory(request.getCategoryId()));

        Page<Product> page = productRepository.findAll(spec, pageable);

        List<ProductResponse> content = page.getContent()
                .stream()
                .map(productMapper::toResponse)
                .toList();
        return PageResponse.<ProductResponse>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .lastPage(page.isLast())
                .build();
    }

    // Köməkçi metodlar--------------

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    private void checkOwnership(Product product) {

        String email = SecurityUtils.getCurrentUserEmail();

        boolean isAdmin = SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) return;

        if (product.getCreatedBy() == null ||
                !product.getCreatedBy().getEmail().equals(email)) {
            log.error("İcazəsiz giriş: {} məhsula müdaxilə etmək cəhdi {}", email, product.getProductId());
            throw new AccessDeniedException("Siz yalnız öz məhsullarınızı idarə edə bilərsiniz");
        }
    }

    private BigDecimal calculateSpecialPrice(BigDecimal price, BigDecimal discount) {
        if (discount == null || discount.compareTo(BigDecimal.ZERO) <= 0) {
            return price;
        }
        BigDecimal discountAmount = price.multiply(discount)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return price.subtract(discountAmount);
    }
}