package com.test.ecomm.modules.cart.repository;

import com.test.ecomm.modules.cart.entity.Cart;
import com.test.ecomm.modules.cart.entity.CartItem;
import com.test.ecomm.modules.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}
