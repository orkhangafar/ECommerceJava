package com.test.ecomm.modules.payment.service;

import com.test.ecomm.modules.payment.dto.PaymentIntentResponse;
import com.test.ecomm.modules.payment.entity.Payment;
import com.test.ecomm.modules.payment.entity.PaymentMethod;

public interface PaymentGatewayService {
    PaymentIntentResponse createPaymentIntent(Payment payment);
    void refund(Payment payment);

    // Ödəniş sistemlərindən gələn təsdiqi (Callback/Webhook və ya Id ilə) emal etmək üçün
    void confirm(Payment payment, String transactionId);

    // Spring-in lazımi servisi avtomatik seçə bilməsi üçün müəyyənləşdirici metod
    PaymentMethod getSupportedMethod();
}
