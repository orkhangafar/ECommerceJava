package com.test.ecomm.modules.payment.service;

import com.test.ecomm.modules.payment.dto.PaymentIntentResponse;
import com.test.ecomm.modules.payment.entity.Payment;
import com.test.ecomm.modules.payment.entity.PaymentMethod;

public interface PaymentGatewayService {
    PaymentIntentResponse createPaymentIntent(Payment payment);
    void refund(Payment payment);
    void confirm(Payment payment, String transactionId);
    PaymentMethod getSupportedMethod();
}
