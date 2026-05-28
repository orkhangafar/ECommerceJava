package com.test.ecomm.modules.review.service.impl;

import com.test.ecomm.common.dto.ApiResponse;
import com.test.ecomm.common.dto.PageResponse;
import com.test.ecomm.common.exception.BadRequestException;
import com.test.ecomm.common.exception.ResourceNotFoundException;
import com.test.ecomm.common.util.SecurityUtils;
import com.test.ecomm.modules.order.entity.OrderStatus;
import com.test.ecomm.modules.order.repository.OrderRepository;
import com.test.ecomm.modules.product.entity.Product;
import com.test.ecomm.modules.product.repository.ProductRepository;
import com.test.ecomm.modules.review.dto.ReviewRequest;
import com.test.ecomm.modules.review.dto.ReviewResponse;
import com.test.ecomm.modules.review.entity.Review;
import com.test.ecomm.modules.review.repository.ReviewRepository;
import com.test.ecomm.modules.review.service.ReviewService;

import com.test.ecomm.modules.user.entity.User;
import com.test.ecomm.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Override
    public ApiResponse<ReviewResponse> createReview(ReviewRequest reviewRequest) {
        User user =  getCurrentUser();
        Product product = findProduct(reviewRequest.getProductId());
        if (!orderRepository.existsByUserAndItemsProductAndOrderStatus(
                user, product, OrderStatus.DELIVERED)) {
            throw new BadRequestException("Yalnız aldığınız və çatdırılmış məhsula rəy yaza bilərsiniz");
        }
        if (reviewRepository.existsByUserAndProduct(user, product)) {
            throw new BadRequestException("Bu məhsula artıq rəy yazmısınız");
        }
        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(reviewRequest.getRating())
                .comment(reviewRequest.getComment())
                .build();

        return ApiResponse.success(mapToResponse(reviewRepository.save(review)), "Rəy əlavə edildi");
    }

    @Override
    @Transactional
    public ApiResponse<ReviewResponse> updateReview(Long reviewId, ReviewRequest reviewRequest) {
        Review review = findReviewByIdAndUser(reviewId);
        review.setRating(reviewRequest.getRating());
        review.setComment(reviewRequest.getComment());
        return ApiResponse.success(mapToResponse(reviewRepository.save(review)), "Rəy yeniləndi");
    }

    @Override
    @Transactional
    public ApiResponse<Long> deleteReview(Long reviewId) {
        User user = getCurrentUser();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "reviewId", reviewId));

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(r -> r.getRoleName().name().equals("ROLE_ADMIN"));

        if (!isAdmin && !review.getUser().getUserId().equals(user.getUserId())) {
            throw new BadRequestException("Bu rəyə müdaxilə etmək icazəniz yoxdur");
        }

        reviewRepository.delete(review);
        return ApiResponse.success(reviewId, "Rəy silindi");
    }

    @Override
    public ApiResponse<PageResponse<ReviewResponse>> getProductReviews(Long productId, int page, int size) {
        Product product = findProduct(productId);
        Page<Review> reviewPage = reviewRepository.findByProduct(product,
                PageRequest.of(page, size, Sort.by("createdAt").descending()));

        List<ReviewResponse> content = reviewPage.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        return ApiResponse.success(PageResponse.<ReviewResponse>builder()
                .content(content)
                .pageNumber(reviewPage.getNumber())
                .pageSize(reviewPage.getSize())
                .totalElements(reviewPage.getTotalElements())
                .totalPages(reviewPage.getTotalPages())
                .lastPage(reviewPage.isLast())
                .build(), "Məhsulun rəyləri");
    }

    @Override
    public ApiResponse<Double> getAverageRating(Long productId) {
        Product product = findProduct(productId);
        Double avg = reviewRepository.findAverageRatingByProduct(product);
        return ApiResponse.success(avg != null ? avg : 0.0, "Ortalama reytinq");
    }

//    Köməkçi metodlar --------------------

    private Review findReviewByIdAndUser(Long reviewId) {
        User user = getCurrentUser();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "reviewId", reviewId));
        if (!review.getUser().getUserId().equals(user.getUserId())) {
            throw new BadRequestException("Bu rəyə müdaxilə etmək icazəniz yoxdur");
        }
        return review;
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
    }

    private User getCurrentUser() {
        return userRepository.findByEmail(SecurityUtils.getCurrentUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", SecurityUtils.getCurrentUserEmail()));
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .reviewId(review.getReviewId())
                .productId(review.getProduct().getProductId())
                .productName(review.getProduct().getProductName())
                .userId(review.getUser().getUserId())
                .userFullName(review.getUser().getFirstName() + " " + review.getUser().getLastName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
