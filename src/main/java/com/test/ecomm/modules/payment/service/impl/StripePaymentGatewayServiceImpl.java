package com.test.ecomm.modules.payment.service.impl;

import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import com.test.ecomm.common.exception.BadRequestException;
import com.test.ecomm.modules.payment.dto.PaymentIntentResponse;
import com.test.ecomm.modules.payment.entity.Payment;
import com.test.ecomm.modules.payment.entity.PaymentMethod;
import com.test.ecomm.modules.payment.service.PaymentGatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StripePaymentGatewayServiceImpl implements PaymentGatewayService {

    private final StripeClient stripeClient;

    @Override
    public PaymentIntentResponse createPaymentIntent(Payment payment) {
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(payment.getAmount()
                            .multiply(new java.math.BigDecimal("100"))
                            .longValue())
                    .setCurrency("azn")
                    .setAutomaticPaymentMethods(PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                            .setEnabled(true)
                            .build()
                    )
                    .build();
            PaymentIntent intent = stripeClient.v1().paymentIntents().create(params);

            return PaymentIntentResponse.builder()
                    .clientSecret(intent.getClientSecret())
                    .paymentIntentId(intent.getId())
                    .orderId(payment.getOrder().getOrderId())
                    .build();
        } catch (StripeException e) {
            throw new BadRequestException("Stripe xətası: " + e.getMessage());
        }
    }

    @Override
    public void refund(Payment payment) {
        try {
            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(payment.getGatewayTransactionId())
                    .build();
            stripeClient.v1().refunds().create(params);
        } catch (StripeException e) {
            throw new BadRequestException("Geri ödəniş xətası: " + e.getMessage());
        }
    }

    @Override
    public void confirm(Payment payment, String transactionId) {
        try {
            // Stripe-dan ödənişin statusunu yoxlayırıq
            PaymentIntent intent = stripeClient.v1().paymentIntents().retrieve(transactionId);

            // Əgər ödəniş uğurlu deyilsə, xəta atırıq
            if (!"succeeded".equals(intent.getStatus())) {
                throw new BadRequestException("Stripe-da ödəniş hələ tamamlanmayıb. Status: " + intent.getStatus());
            }
        } catch (StripeException e) {
            throw new BadRequestException("Stripe təsdiqləmə xətası: " + e.getMessage());
        }
    }

    // --- DƏYİŞİLƏN HİSSƏ: Bu servisin STRIPE metodu üçün işlədiyini bəyan edirik ---
    @Override
    public PaymentMethod getSupportedMethod() {
        return PaymentMethod.STRIPE;
    }
}
