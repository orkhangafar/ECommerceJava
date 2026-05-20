package com.test.ecomm.modules.payment.repository;

import com.test.ecomm.modules.order.entity.Order;
import com.test.ecomm.modules.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {
    Optional<Payment> findByOrder(Order order);
    Optional<Payment> findByGatewayTransactionId(String gatewayTransactionId);
}
