package com.flavorspot.service.impl;

import com.flavorspot.dto.request.ReviewRequest;
import com.flavorspot.dto.response.ReviewResponse;
import com.flavorspot.entity.Review;
import com.flavorspot.entity.User;
import com.flavorspot.exception.ResourceNotFoundException;
import com.flavorspot.repository.ReviewRepository;
import com.flavorspot.repository.UserRepository;
import com.flavorspot.service.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ReviewResponse submitReview(ReviewRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        // Reviews require admin approval before appearing on the public landing page
        Review review = new Review(
                user,
                request.getRating(),
                request.getComment().trim(),
                false // Pending moderation
        );

        Review saved = reviewRepository.save(review);
        return mapToReviewResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getApprovedTestimonials() {
        return reviewRepository.findByIsApprovedTrueOrderByCreatedAtDesc().stream()
                .map(this::mapToReviewResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByCustomer(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }

        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToReviewResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getAllReviewsForAdmin() {
        return reviewRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToReviewResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReviewResponse moderateReview(Long id, Boolean approved) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));

        review.setIsApproved(approved);
        Review updated = reviewRepository.save(review);
        return mapToReviewResponse(updated);
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));
        reviewRepository.delete(review);
    }

    private ReviewResponse mapToReviewResponse(Review r) {
        return new ReviewResponse(
                r.getId(),
                r.getUser().getId(),
                r.getUser().getFullName(),
                r.getRating(),
                r.getComment(),
                r.getIsApproved(),
                r.getCreatedAt()
        );
    }
}
