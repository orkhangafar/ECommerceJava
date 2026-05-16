package com.test.ecomm.modules.order.dto;

import com.test.ecomm.modules.order.entity.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long orderId;
    private String addressTitle;
    private String addressCity;
    private String addressStreet;
    private OrderStatus orderStatus;
    private BigDecimal totalAmount;
    private String note;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;
}
