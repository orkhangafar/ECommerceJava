package com.test.ecomm.modules.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequest {

    @NotNull
    private Long productId;

    @NotNull
    @Min(value = 1, message = "{cart.item.quantity.min}")
    private Integer quantity;
}
