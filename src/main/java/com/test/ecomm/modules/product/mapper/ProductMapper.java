package com.test.ecomm.modules.product.mapper;

import com.test.ecomm.modules.product.dto.ProductRequest;
import com.test.ecomm.modules.product.dto.ProductResponse;
import com.test.ecomm.modules.product.entity.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "category.categoryId", target = "categoryId")
    @Mapping(source = "category.categoryName", target = "categoryName")
    ProductResponse toResponse(Product product);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "specialPrice", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "productId", ignore = true)
    Product toEntity(ProductRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "specialPrice", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "productId", ignore = true)
    void updateEntity(ProductRequest request, @MappingTarget Product product);
}
