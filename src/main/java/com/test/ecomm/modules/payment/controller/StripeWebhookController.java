package com.test.ecomm.modules.payment.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.test.ecomm.modules.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class StripeWebhookController {

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    private final PaymentService paymentService;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.warn("Webhook imza xətası: {}", e.getMessage());
            return ResponseEntity.badRequest().body("İmza doğrulaması uğursuz oldu");
        }

        switch (event.getType()) {
            case "payment_intent.succeeded" -> {
                event.getDataObjectDeserializer()
                        .getObject()
                        .ifPresent(obj -> {
                            PaymentIntent intent = (PaymentIntent) obj;
                            paymentService.confirmPayment(intent.getId());
                        });
            }
            case "payment_intent.payment_failed" -> {
                log.warn("Ödəniş uğursuz oldu: {}", event.getId());
            }
            default -> log.info("Bilinməyən event: {}", event.getType());
        }

        return ResponseEntity.ok("Webhook qəbul edildi");
    }
}
