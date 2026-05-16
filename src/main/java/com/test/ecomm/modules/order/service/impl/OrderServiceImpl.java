package com.test.ecomm.modules.order.service.impl;

import com.test.ecomm.common.constants.AppConstants;
import com.test.ecomm.common.dto.ApiResponse;
import com.test.ecomm.common.dto.PageResponse;
import com.test.ecomm.common.exception.BadRequestException;
import com.test.ecomm.common.exception.ResourceNotFoundException;
import com.test.ecomm.common.util.SecurityUtils;
import com.test.ecomm.modules.address.entity.Address;
import com.test.ecomm.modules.address.repository.AddressRepository;
import com.test.ecomm.modules.cart.entity.Cart;
import com.test.ecomm.modules.cart.entity.CartItem;
import com.test.ecomm.modules.cart.repository.CartRepository;
import com.test.ecomm.modules.order.dto.OrderFilterRequest;
import com.test.ecomm.modules.order.dto.OrderRequest;
import com.test.ecomm.modules.order.dto.OrderResponse;
import com.test.ecomm.modules.order.entity.Order;
import com.test.ecomm.modules.order.entity.OrderItem;
import com.test.ecomm.modules.order.entity.OrderStatus;
import com.test.ecomm.modules.order.mapper.OrderMapper;
import com.test.ecomm.modules.order.repository.OrderRepository;
import com.test.ecomm.modules.order.service.OrderService;
import com.test.ecomm.modules.product.entity.Product;
import com.test.ecomm.modules.product.repository.ProductRepository;
import com.test.ecomm.modules.user.entity.User;
import com.test.ecomm.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;


    @Override
    @Transactional
    public ApiResponse<OrderResponse> placeOrder(OrderRequest orderRequest) {
        User user = getCurrentUser();
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new BadRequestException("Səbətiniz boşdur"));
        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Səbətiniz boşdur");
        }

        Address address = addressRepository.findById(orderRequest.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", orderRequest.getAddressId()));
        if (!address.getUser().getUserId().equals(user.getUserId())) {
            throw new BadRequestException("Bu ünvan sizə məxsus deyil");
        }

        Order order = orderMapper.toEntity(orderRequest);
        order.setUser(user);
        order.setAddress(address);

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            validateStock(product, cartItem.getQuantity());

            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = orderMapper.cartItemToOrderItem(cartItem);
            orderItem.setOrder(order);
            orderItem.calculateSubTotal();
            order.getItems().add(orderItem);
        }

        order.calculateTotalAmount();
        Order savedOrder = orderRepository.save(order);

        cart.getItems().clear();
        cart.updateTotalAmount();
        cartRepository.save(cart);

        return ApiResponse.success(orderMapper.toResponse(savedOrder), "Sifariş uğurla yerləşdirildi");
    }

    @Override
    public ApiResponse<PageResponse<OrderResponse>> getMyOrders(OrderFilterRequest request) {
        User user = getCurrentUser();
        Pageable pageable = buildPageable(request);

        Page<Order> page;
        if (request.getStatus() != null) {
            page = orderRepository.findAll(
                    Specification.where(
                            (root, query, cb) -> cb.and(
                                    cb.equal(root.get("user"), user),
                                    cb.equal(root.get("orderStatus"), request.getStatus())
                            )
                    ), pageable);
        } else {
            page = orderRepository.findByUser(user, pageable);
        }

        return ApiResponse.success(buildPageResponse(page), "Sifarişləriniz");
    }

    @Override
    public ApiResponse<OrderResponse> getOrderById(Long orderId) {
        Order order = findOrderByIdAndUser(orderId);
        return ApiResponse.success(orderMapper.toResponse(order), "Sifariş məlumatları");
    }

    @Override
    @Transactional
    public ApiResponse<OrderResponse> cancelOrder(Long orderId) {
        Order order = findOrderByIdAndUser(orderId);

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Yalnız gözləmədə olan sifarişlər ləğv edilə bilər");
        }

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        return ApiResponse.success(orderMapper.toResponse(orderRepository.save(order)), "Sifariş ləğv edildi");
    }

    @Override
    public ApiResponse<PageResponse<OrderResponse>> getAllOrders(OrderFilterRequest request) {
        Pageable pageable = buildPageable(request);

        Specification<Order> spec = Specification.where(
                request.getStatus() != null
                        ? (root, query, cb) -> cb.equal(root.get("orderStatus"), request.getStatus())
                        : null
        );
        Page<Order> page = orderRepository.findAll(spec, pageable);
        return ApiResponse.success(buildPageResponse(page), "Bütün sifarişlər");
    }

    @Override
    @Transactional
    public ApiResponse<OrderResponse> updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderId", orderId));
        order.setOrderStatus(status);
        return ApiResponse.success(orderMapper.toResponse(orderRepository.save(order)), "Sifarişin statusu yeniləndi");
    }

//    Köməkçi Metodlar--------------------------

    private void validateStock(Product product, Integer quantity) {
        if (product.getQuantity() < quantity) {
            throw new BadRequestException("'" + product.getProductName() + "' məhsulu stokda kifayət qədər yoxdur");
        }
    }

    private Order findOrderByIdAndUser(Long orderId) {
        User user = getCurrentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderId", orderId));
        if (!order.getUser().getUserId().equals(user.getUserId())) {
            throw new BadRequestException("Bu sifarişə müdaxilə etmək icazəniz yoxdur");
        }
        return order;
    }

    private User getCurrentUser() {
        return userRepository.findByEmail(SecurityUtils.getCurrentUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", SecurityUtils.getCurrentUserEmail()));
    }

    private Pageable buildPageable(OrderFilterRequest request) {
        Sort sort = request.getSortOrder().equalsIgnoreCase(AppConstants.SORT_ASC)
                ? Sort.by(request.getSortBy()).ascending()
                : Sort.by(request.getSortBy()).descending();
        return PageRequest.of(request.getPageNumber(), request.getPageSize(), sort);
    }

    private PageResponse<OrderResponse> buildPageResponse(Page<Order> page) {
        List<OrderResponse> content = page.getContent().stream().map(orderMapper::toResponse).toList();
        return PageResponse.<OrderResponse>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .lastPage(page.isLast())
                .build();
    }
}
