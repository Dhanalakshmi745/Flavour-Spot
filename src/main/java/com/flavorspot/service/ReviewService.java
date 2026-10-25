package com.flavorspot.service;

import com.flavorspot.dto.request.ReviewRequest;
import com.flavorspot.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewService {

    ReviewResponse submitReview(ReviewRequest request);
    List<ReviewResponse> getApprovedTestimonials();
    List<ReviewResponse> getReviewsByCustomer(Long userId);
    List<ReviewResponse> getAllReviewsForAdmin();
    ReviewResponse moderateReview(Long id, Boolean approved);
    void deleteReview(Long id);
}
