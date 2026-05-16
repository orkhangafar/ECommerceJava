package com.test.ecomm.modules.product.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "{product.name.not_blank}")
    @Size(min = 3, message = "{product.name.size}")
    private String productName;

    @NotBlank(message = "{product.description.not_blank}")
    @Size(min = 10, message = "{product.description.size}")
    private String description;

    @NotNull(message = "{product.quantity.not_null}")
    @Min(value = 1, message = "{product.quantity.min}")
    private Integer quantity;

    @NotNull(message = "{product.price.not_null}")
    @DecimalMin(value = "0.01", message = "{product.price.min}")
    private BigDecimal price;

    @DecimalMin(value = "0.0", message = "{product.discount.min}")
    @DecimalMax(value = "100.0", message = "{product.discount.max}")
    private BigDecimal discount = BigDecimal.ZERO;

    private String image;
}
