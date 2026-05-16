package com.test.ecomm.modules.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {

    @NotNull(message = "{order.address.not_null}")
    private Long addressId;

    private String note;
}
