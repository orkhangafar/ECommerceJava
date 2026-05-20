package com.test.ecomm.modules.order.repository;

import com.test.ecomm.modules.order.entity.Order;
import com.test.ecomm.modules.order.entity.OrderStatus;
import com.test.ecomm.modules.product.entity.Product;
import com.test.ecomm.modules.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    Page<Order> findByUser(User user, Pageable pageable);
    boolean existsByUserAndItemsProductAndOrderStatus(User user, Product product, OrderStatus orderStatus);
}
