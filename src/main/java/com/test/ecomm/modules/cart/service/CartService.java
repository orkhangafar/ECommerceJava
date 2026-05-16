package com.test.ecomm.modules.cart.service;

import com.test.ecomm.common.dto.ApiResponse;
import com.test.ecomm.modules.cart.dto.CartItemRequest;
import com.test.ecomm.modules.cart.dto.CartResponse;

public interface CartService {
    ApiResponse<CartResponse> getMyCart();
    ApiResponse<CartResponse> addItem(CartItemRequest cartItemRequest);
    ApiResponse<CartResponse> updateItem(Long cartItemId, CartItemRequest cartItemRequest);
    ApiResponse<CartResponse> removeItem(Long cartItemId);
    ApiResponse<Void> clearCart();
}
