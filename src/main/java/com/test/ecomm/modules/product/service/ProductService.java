package com.test.ecomm.modules.product.service;

import com.test.ecomm.common.dto.PageResponse;
import com.test.ecomm.modules.product.dto.ProductFilterRequest;
import com.test.ecomm.modules.product.dto.ProductRequest;
import com.test.ecomm.modules.product.dto.ProductResponse;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
    ProductResponse addProduct(Long categoryId, @Valid ProductRequest productRequest);
    ProductResponse updateProduct(Long productId, @Valid ProductRequest productRequest);
    ProductResponse deleteProduct(Long productId);
    ProductResponse getProductById(Long productId);
    ProductResponse updateProductImage(Long productId, MultipartFile image);
    PageResponse<ProductResponse> getAllProducts(ProductFilterRequest request);
}