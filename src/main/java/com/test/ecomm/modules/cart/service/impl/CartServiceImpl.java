package com.test.ecomm.modules.cart.service.impl;

import com.test.ecomm.common.dto.ApiResponse;
import com.test.ecomm.common.exception.BadRequestException;
import com.test.ecomm.common.exception.ResourceNotFoundException;
import com.test.ecomm.common.util.SecurityUtils;
import com.test.ecomm.modules.cart.dto.CartItemRequest;
import com.test.ecomm.modules.cart.dto.CartItemResponse;
import com.test.ecomm.modules.cart.dto.CartResponse;
import com.test.ecomm.modules.cart.entity.Cart;
import com.test.ecomm.modules.cart.entity.CartItem;
import com.test.ecomm.modules.cart.repository.CartItemRepository;
import com.test.ecomm.modules.cart.repository.CartRepository;
import com.test.ecomm.modules.cart.service.CartService;
import com.test.ecomm.modules.product.entity.Product;
import com.test.ecomm.modules.product.repository.ProductRepository;
import com.test.ecomm.modules.user.entity.User;
import com.test.ecomm.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;


    @Override
    public ApiResponse<CartResponse> getMyCart() {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);
        return ApiResponse.success(mapToResponse(cart), "Səbət məlumatları");
    }

    @Override
    @Transactional
    public ApiResponse<CartResponse> addItem(CartItemRequest cartItemRequest) {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);
        Product product = findProduct(cartItemRequest.getProductId());
        validateStock(product, cartItemRequest.getQuantity());

        cartItemRepository.findByCartAndProduct(cart, product)
                .ifPresentOrElse(existing ->{
                    existing.setQuantity(existing.getQuantity() + cartItemRequest.getQuantity());
                    existing.calculateSubTotal();},
                        () -> cart.addItem(CartItem.builder()
                                .cart(cart)
                                .product(product)
                                .quantity(cartItemRequest.getQuantity())
                                .price(product.getPrice())
                                .build())
                );
        cart.updateTotalAmount();
        return ApiResponse.success(mapToResponse(cartRepository.save(cart)), "Məhsul səbətə əlavə edildi");
    }

    @Override
    @Transactional
    public ApiResponse<CartResponse> updateItem(Long cartItemId, CartItemRequest cartItemRequest) {
        CartItem item = findCartItem(cartItemId);
        validateStock(item.getProduct(), cartItemRequest.getQuantity());
        item.setQuantity(cartItemRequest.getQuantity());
        item.calculateSubTotal();
        item.getCart().updateTotalAmount();
        cartRepository.save(item.getCart());
        return ApiResponse.success(mapToResponse(item.getCart()), "Səbət yeniləndi");
    }

    @Override
    @Transactional
    public ApiResponse<CartResponse> removeItem(Long cartItemId) {
        CartItem item = findCartItem(cartItemId);
        Cart cart = item.getCart();
        cart.removeItem(item);
        return ApiResponse.success(mapToResponse(cartRepository.save(cart)), "Məhsul səbətdən çıxarıldı");
    }

    @Override
    @Transactional
    public ApiResponse<Void> clearCart() {
        Cart cart = getOrCreateCart(getCurrentUser());
        cart.getItems().clear();
        cart.updateTotalAmount();
        cartRepository.save(cart);
        return ApiResponse.success(null, "Səbət təmizləndi");
    }

    //    ----Köməkçi metodlar -----------

    private Cart getOrCreateCart(User user) {
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(
                        Cart.builder().user(user).build()
                ));
        for (CartItem item : cart.getItems()) {
            if(!item.getPrice().equals(item.getProduct().getPrice())) {
                item.setPrice(item.getProduct().getPrice());
            }
        }
        cart.updateTotalAmount();
        return cartRepository.save(cart);
    }

    private CartItem findCartItem(Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "cartItemId", cartItemId));
        User currentUser = getCurrentUser();
        if (!item.getCart().getUser().getUserId().equals(currentUser.getUserId())) {
            throw new BadRequestException("Bu əməliyyat üçün icazəniz yoxdur");
        }
        return item;
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
    }

    private void validateStock(Product product, int quantity) {
        if (product.getQuantity() < quantity) {
            throw new BadRequestException("Stokda kifayət qədər məhsul yoxdur");
        }
    }

    private User getCurrentUser() {
        return userRepository.findByEmail(SecurityUtils.getCurrentUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", SecurityUtils.getCurrentUserEmail()));
    }

    private CartResponse mapToResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(item -> CartItemResponse.builder()
                        .cartItemId(item.getCartItemId())
                        .productId(item.getProduct().getProductId())
                        .productName(item.getProduct().getProductName())
                        .imageUrl(item.getProduct().getImage())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .subTotal(item.getSubTotal())
                        .build())
                .toList();
        return CartResponse.builder()
                .cartId(cart.getCartId())
                .items(items)
                .itemCount(items.size())
                .totalAmount(cart.getTotalAmount())
                .build();
    }
}
