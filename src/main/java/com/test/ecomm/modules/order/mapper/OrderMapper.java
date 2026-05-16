package com.test.ecomm.modules.order.mapper;

import com.test.ecomm.modules.cart.entity.CartItem;
import com.test.ecomm.modules.order.dto.OrderItemResponse;
import com.test.ecomm.modules.order.dto.OrderRequest;
import com.test.ecomm.modules.order.dto.OrderResponse;
import com.test.ecomm.modules.order.entity.Order;
import com.test.ecomm.modules.order.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "address.title", target = "addressTitle")
    @Mapping(source = "address.city", target = "addressCity")
    @Mapping(source = "address.street", target = "addressStreet")
    OrderResponse toResponse(Order order);

    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "orderStatus", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "address", ignore = true)
    Order toEntity(OrderRequest request);

    @Mapping(source = "product.productId", target = "productId")
    OrderItemResponse toItemResponse(OrderItem orderItem);

    @Mapping(target = "orderItemId", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(source = "product.productName", target = "productName")
    @Mapping(source = "product.price", target = "price")
    @Mapping(target = "subTotal", ignore = true)
    OrderItem cartItemToOrderItem(CartItem cartItem);
}
