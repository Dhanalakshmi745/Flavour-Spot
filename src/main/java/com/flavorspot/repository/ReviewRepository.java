package com.flavorspot.repository;

import com.flavorspot.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByIsApprovedTrueOrderByCreatedAtDesc();
    List<Review> findAllByOrderByCreatedAtDesc();
    List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);
    Long countByIsApprovedFalse();
}
