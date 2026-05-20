package com.test.ecomm.modules.payment.service.impl;

import com.test.ecomm.common.dto.ApiResponse;
import com.test.ecomm.common.dto.PageResponse;
import com.test.ecomm.common.exception.BadRequestException;
import com.test.ecomm.common.exception.ResourceNotFoundException;
import com.test.ecomm.common.util.SecurityUtils;
import com.test.ecomm.modules.order.entity.Order;
import com.test.ecomm.modules.order.entity.OrderStatus;
import com.test.ecomm.modules.order.repository.OrderRepository;
import com.test.ecomm.modules.payment.dto.PaymentIntentResponse;
import com.test.ecomm.modules.payment.dto.PaymentRequest;
import com.test.ecomm.modules.payment.dto.PaymentResponse;
import com.test.ecomm.modules.payment.entity.Payment;
import com.test.ecomm.modules.payment.entity.PaymentMethod;
import com.test.ecomm.modules.payment.entity.PaymentStatus;
import com.test.ecomm.modules.payment.repository.PaymentRepository;
import com.test.ecomm.modules.payment.service.PaymentGatewayService;
import com.test.ecomm.modules.payment.service.PaymentService;
import com.test.ecomm.modules.user.entity.User;
import com.test.ecomm.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final List<PaymentGatewayService> gatewayServices;

    private PaymentGatewayService getGateway(PaymentMethod method) {
        return gatewayServices.stream()
                .filter(g -> g.getSupportedMethod() == method)
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Bu ödəniş üsulu sistemdə aktiv deyil: " + method));
    }

    @Override
    @Transactional
    public ApiResponse<PaymentIntentResponse> createPaymentIntent(PaymentRequest paymentRequest) {
        User currentUser = getCurrentUser();

        // --- DƏQİQLƏŞDİRİLDİ: "id" yerinə "orderId" yazıldı ---
        Order order = orderRepository.findById(paymentRequest.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderId", paymentRequest.getOrderId()));

        if (!order.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new BadRequestException("Bu sifariş üçün ödəniş etmək icazəniz yoxdur.");
        }

        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getTotalAmount())
                .paymentMethod(paymentRequest.getPaymentMethod())
                .paymentStatus(PaymentStatus.PENDING)
                .build();
        Payment savedPayment = paymentRepository.save(payment);

        PaymentGatewayService gateway = getGateway(paymentRequest.getPaymentMethod());
        PaymentIntentResponse response = gateway.createPaymentIntent(savedPayment);

        savedPayment.setGatewayTransactionId(response.getPaymentIntentId());
        savedPayment.setGatewayClientSecret(response.getClientSecret());
        paymentRepository.save(savedPayment);

        return ApiResponse.success(response, "Ödəniş başladıldı");
    }

    @Override
    @Transactional
    public ApiResponse<PaymentResponse> confirmPayment(String paymentIntentId) {
        // --- DƏQİQLƏŞDİRİLDİ: "transactionId" yerinə entity sahəsi olan "gatewayTransactionId" yazıldı ---
        Payment payment = paymentRepository.findByGatewayTransactionId(paymentIntentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "gatewayTransactionId", paymentIntentId));

        if (payment.getPaymentStatus() == PaymentStatus.SUCCEEDED) {
            throw new BadRequestException("Bu ödəniş artıq uğurla tamamlanıb");
        }

        PaymentGatewayService gateway = getGateway(payment.getPaymentMethod());
        gateway.confirm(payment, paymentIntentId);

        payment.setPaymentStatus(PaymentStatus.SUCCEEDED);
        payment.getOrder().setOrderStatus(OrderStatus.CONFIRMED);
        orderRepository.save(payment.getOrder());

        return ApiResponse.success(mapToResponse(paymentRepository.save(payment)), "Ödəniş təsdiqləndi");
    }

    @Override
    @Transactional
    public ApiResponse<PaymentResponse> refundPayment(Long paymentId) {
        // --- DƏQİQLƏŞDİRİLDİ: "id" yerinə "paymentId" yazıldı ---
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "paymentId", paymentId));

        if (payment.getPaymentStatus() != PaymentStatus.SUCCEEDED) {
            throw new BadRequestException("Yalnız uğurlu ödənişlər geri qaytarıla bilər");
        }

        PaymentGatewayService gateway = getGateway(payment.getPaymentMethod());
        gateway.refund(payment);

        payment.setPaymentStatus(PaymentStatus.REFUNDED);
        payment.getOrder().setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(payment.getOrder());

        return ApiResponse.success(mapToResponse(paymentRepository.save(payment)), "Ödəniş geri qaytarıldı");
    }

    @Override
    public ApiResponse<PaymentResponse> getPaymentByOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderId", orderId));
        Payment payment = paymentRepository.findByOrder(order)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "orderId", orderId));

        return ApiResponse.success(mapToResponse(payment), "Ödəniş məlumatı tapıldı");
    }

    @Override
    public ApiResponse<PageResponse<PaymentResponse>> getAllPayments(Pageable pageable) {
        Page<Payment> paymentPage = paymentRepository.findAll(pageable);

        List<PaymentResponse> content = paymentPage.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        PageResponse<PaymentResponse> pageResponse = PageResponse.<PaymentResponse>builder()
                .content(content)
                .pageNumber(paymentPage.getNumber())
                .pageSize(paymentPage.getSize())
                .totalElements(paymentPage.getTotalElements())
                .totalPages(paymentPage.getTotalPages())
                .lastPage(paymentPage.isLast())
                .build();

        return ApiResponse.success(pageResponse, "Bütün ödənişlər gətirildi");
    }

    // --- Köməkçi metodlar --------------

    private User getCurrentUser() {
        return userRepository.findByEmail(SecurityUtils.getCurrentUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", SecurityUtils.getCurrentUserEmail()));
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrder().getOrderId())
                .paymentStatus(payment.getPaymentStatus())
                .paymentMethod(payment.getPaymentMethod())
                .amount(payment.getAmount())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}