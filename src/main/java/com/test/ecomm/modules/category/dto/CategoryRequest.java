package com.test.ecomm.modules.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequest {

    @NotBlank(message = "{category.name.notblank}")
    @Size(min = 3, max = 25, message = "{category.name.size}")
    private String categoryName;
}
