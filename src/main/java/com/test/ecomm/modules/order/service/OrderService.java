package com.test.ecomm.modules.order.service;

import com.test.ecomm.common.dto.ApiResponse;
import com.test.ecomm.common.dto.PageResponse;
import com.test.ecomm.modules.order.dto.OrderFilterRequest;
import com.test.ecomm.modules.order.dto.OrderRequest;
import com.test.ecomm.modules.order.dto.OrderResponse;
import com.test.ecomm.modules.order.entity.OrderStatus;

public interface OrderService {
    ApiResponse<OrderResponse> placeOrder(OrderRequest orderRequest);
    ApiResponse<PageResponse<OrderResponse>> getMyOrders(OrderFilterRequest request);
    ApiResponse<OrderResponse> getOrderById(Long orderId);
    ApiResponse<OrderResponse> cancelOrder(Long orderId);
    ApiResponse<PageResponse<OrderResponse>> getAllOrders(OrderFilterRequest request);
    ApiResponse<OrderResponse> updateOrderStatus(Long orderId, OrderStatus status);
}
