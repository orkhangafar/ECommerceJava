package com.test.ecomm.modules.payment.dto;

import com.test.ecomm.modules.payment.entity.PaymentMethod;
import com.test.ecomm.modules.payment.entity.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private Long paymentId;
    private Long orderId;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    private LocalDateTime createdAt;
}
