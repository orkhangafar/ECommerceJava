package com.test.ecomm.modules.cart.repository;

import com.test.ecomm.modules.cart.entity.Cart;
import com.test.ecomm.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}

