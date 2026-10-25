package com.flavorspot.controller;

import com.flavorspot.dto.request.ReviewRequest;
import com.flavorspot.dto.response.ApiResponse;
import com.flavorspot.dto.response.ReviewResponse;
import com.flavorspot.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // -------------------------------------------------------------
    // Public & Customer Endpoints
    // -------------------------------------------------------------

    @GetMapping("/testimonials")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getApprovedTestimonials() {
        List<ReviewResponse> testimonials = reviewService.getApprovedTestimonials();
        return ResponseEntity.ok(
                ApiResponse.success("Approved customer testimonials retrieved", testimonials)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> submitReview(@Valid @RequestBody ReviewRequest request) {
        ReviewResponse review = reviewService.submitReview(request);
        return new ResponseEntity<>(
                ApiResponse.success("Thank you for your review! It will appear on our homepage after brief moderation.", review),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getMyReviews(@RequestParam Long userId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByCustomer(userId);
        return ResponseEntity.ok(
                ApiResponse.success("Customer reviews retrieved", reviews)
        );
    }

    // -------------------------------------------------------------
    // Admin Moderation Endpoints
    // -------------------------------------------------------------

    @GetMapping("/admin/all")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getAllReviewsForAdmin() {
        List<ReviewResponse> allReviews = reviewService.getAllReviewsForAdmin();
        return ResponseEntity.ok(
                ApiResponse.success("All reviews retrieved for admin moderation", allReviews)
        );
    }

    @PatchMapping("/{id}/moderate")
    public ResponseEntity<ApiResponse<ReviewResponse>> moderateReview(
            @PathVariable Long id,
            @RequestParam Boolean approved) {
        ReviewResponse updated = reviewService.moderateReview(id, approved);
        String action = Boolean.TRUE.equals(approved) ? "approved" : "hidden/unapproved";
        return ResponseEntity.ok(
                ApiResponse.success("Review #" + id + " has been " + action, updated)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok(
                ApiResponse.success("Review #" + id + " has been deleted.", null)
        );
    }
}
