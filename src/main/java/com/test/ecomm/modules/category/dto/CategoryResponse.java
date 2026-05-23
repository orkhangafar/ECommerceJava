package com.test.ecomm.modules.category.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    private Long categoryId;
    private String categoryName;
    private Long parentId;

    @Builder.Default
    private List<CategoryResponse> children = new ArrayList<>();
}
