package com.flavorspot.dto.response;

import java.time.LocalDateTime;

public class ReviewResponse {

    private Long id;
    private Long userId;
    private String customerName;
    private Integer rating;
    private String comment;
    private Boolean isApproved;
    private LocalDateTime createdAt;

    public ReviewResponse() {}

    public ReviewResponse(Long id, Long userId, String customerName, Integer rating, 
                          String comment, Boolean isApproved, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.customerName = customerName;
        this.rating = rating;
        this.comment = comment;
        this.isApproved = isApproved;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Boolean getIsApproved() { return isApproved; }
    public void setIsApproved(Boolean isApproved) { this.isApproved = isApproved; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
