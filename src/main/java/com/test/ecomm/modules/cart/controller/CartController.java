package com.test.ecomm.modules.cart.controller;

import com.test.ecomm.common.dto.ApiResponse;
import com.test.ecomm.modules.cart.dto.CartItemRequest;
import com.test.ecomm.modules.cart.dto.CartResponse;
import com.test.ecomm.modules.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<CartResponse>> getMyCart() {
        return ResponseEntity.ok(cartService.getMyCart());
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(
            @Valid @RequestBody CartItemRequest cartItemRequest) {
        return ResponseEntity.ok(cartService.addItem(cartItemRequest));
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateItem(
            @PathVariable Long cartItemId,
            @Valid @RequestBody CartItemRequest cartItemRequest) {
        return ResponseEntity.ok(cartService.updateItem(cartItemId, cartItemRequest));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
            @PathVariable Long cartItemId) {
        return ResponseEntity.ok(cartService.removeItem(cartItemId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart() {
        return ResponseEntity.ok(cartService.clearCart());
    }
}
