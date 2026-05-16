package com.test.ecomm.modules.category.mapper;


import com.test.ecomm.modules.category.dto.CategoryRequest;
import com.test.ecomm.modules.category.dto.CategoryResponse;
import com.test.ecomm.modules.category.entity.Category;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponse toResponse(Category category);

    @Mapping(target = "categoryId", ignore=true)
    @Mapping(target = "products", ignore = true)
    Category toEntity(CategoryRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "products" , ignore = true)
    void updateEntity(CategoryRequest request, @MappingTarget Category category);

}
