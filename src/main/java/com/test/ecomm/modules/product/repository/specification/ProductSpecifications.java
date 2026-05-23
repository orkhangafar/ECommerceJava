package com.test.ecomm.modules.product.repository.specification;

import com.test.ecomm.modules.category.entity.Category;
import com.test.ecomm.modules.category.repository.CategoryRepository;
import com.test.ecomm.modules.product.entity.Product;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;
import java.util.Optional;

public class ProductSpecifications {

    public static Specification<Product> hasName(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("productName")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Product> hasPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            if (minPrice == null && maxPrice == null) {
                return cb.conjunction();
            }
            if (minPrice != null && maxPrice != null) {
                return cb.between(root.get("price"), minPrice, maxPrice);
            }
            if (minPrice != null) {
                return cb.greaterThanOrEqualTo(root.get("price"), minPrice);
            }
            return cb.lessThanOrEqualTo(root.get("price"), maxPrice);
        };
    }

    public static Specification<Product> hasCategory(Long categoryId, CategoryRepository categoryRepository) {
        return (root, query, cb) -> {
            if (categoryId == null) {
                return cb.conjunction();
            }
            Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

            if (categoryOpt.isEmpty()) {
                return cb.conjunction();
            }
            String categoryPath = categoryOpt.get().getPath();
            Join<Product, Category> categoryJoin = root.join("category");
            return cb.like(categoryJoin.get("path"), categoryPath + "%");
        };
    }
}