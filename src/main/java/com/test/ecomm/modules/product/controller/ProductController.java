package com.test.ecomm.modules.product.controller;

import com.test.ecomm.common.dto.ApiResponse;
import com.test.ecomm.common.dto.PageResponse;
import com.test.ecomm.modules.product.dto.ProductFilterRequest;
import com.test.ecomm.modules.product.dto.ProductRequest;
import com.test.ecomm.modules.product.dto.ProductResponse;
import com.test.ecomm.modules.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @PostMapping("/categories/{categoryId}/products")
    public ResponseEntity<ApiResponse<ProductResponse>> addProduct(
            @PathVariable Long categoryId,
            @Valid @RequestBody ProductRequest productRequest) {
        ProductResponse response = productService.addProduct(categoryId, productRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Məhsul uğurla əlavə edildi"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @PutMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductRequest productRequest) {
        ProductResponse response = productService.updateProduct(productId, productRequest);
        return ResponseEntity.ok(ApiResponse.success(response, "Məhsul məlumatları uğurla yeniləndi"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> deleteProduct(
            @PathVariable Long productId) {
        ProductResponse response = productService.deleteProduct(productId);
        return ResponseEntity.ok(ApiResponse.success(response, "Məhsul sistemdən uğurla silindi."));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @PathVariable Long productId) {
        ProductResponse response = productService.getProductById(productId);
        return ResponseEntity.ok(ApiResponse.success(response, "Məhsul uğurla gətirildi"));
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getAllProducts(
            @Valid ProductFilterRequest request) {
        PageResponse<ProductResponse> response = productService.getAllProducts(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Məhsullar uğurla gətirildi"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @PatchMapping("/products/{productId}/image")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProductImage(
            @PathVariable Long productId,
            @RequestParam("image") MultipartFile image) {
        ProductResponse response = productService.updateProductImage(productId, image);
        return ResponseEntity.ok(ApiResponse.success(response, "Məhsulun şəkli uğurla yeniləndi"));
    }
}