package com.test.ecomm.modules.payment.service;

import com.test.ecomm.common.dto.ApiResponse;
import com.test.ecomm.common.dto.PageResponse;
import com.test.ecomm.modules.payment.dto.PaymentIntentResponse;
import com.test.ecomm.modules.payment.dto.PaymentRequest;
import com.test.ecomm.modules.payment.dto.PaymentResponse;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    ApiResponse<PaymentIntentResponse> createPaymentIntent(PaymentRequest paymentRequest);
    ApiResponse<PaymentResponse> confirmPayment(String paymentIntentId);
    ApiResponse<PaymentResponse> refundPayment(Long paymentId);
    ApiResponse<PaymentResponse> getPaymentByOrder(Long orderId);
    ApiResponse<PageResponse<PaymentResponse>> getAllPayments(Pageable pageable);
}
