package com.test.ecomm.modules.review.service;

import com.test.ecomm.common.dto.ApiResponse;
import com.test.ecomm.common.dto.PageResponse;
import com.test.ecomm.modules.review.dto.ReviewRequest;
import com.test.ecomm.modules.review.dto.ReviewResponse;

public interface ReviewService {
    ApiResponse<ReviewResponse> createReview(ReviewRequest reviewRequest);
    ApiResponse<ReviewResponse> updateReview(Long reviewId, ReviewRequest reviewRequest);
    ApiResponse<Long> deleteReview(Long reviewId);
    ApiResponse<PageResponse<ReviewResponse>> getProductReviews(Long productId, int page, int size);
    ApiResponse<Double> getAverageRating(Long productId);
}
