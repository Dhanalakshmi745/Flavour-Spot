package com.flavorspot.dto.response;

import java.math.BigDecimal;

public class AdminStatsResponse {

    private long totalCustomers;
    private long totalReservations;
    private long activeReservations;
    private long totalOrders;
    private BigDecimal totalRevenue;
    private long totalMenuItems;
    private long pendingReviews;

    public AdminStatsResponse() {}

    public AdminStatsResponse(long totalCustomers, long totalReservations, long activeReservations, 
                              long totalOrders, BigDecimal totalRevenue, long totalMenuItems, long pendingReviews) {
        this.totalCustomers = totalCustomers;
        this.totalReservations = totalReservations;
        this.activeReservations = activeReservations;
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
        this.totalMenuItems = totalMenuItems;
        this.pendingReviews = pendingReviews;
    }

    public long getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(long totalCustomers) { this.totalCustomers = totalCustomers; }

    public long getTotalReservations() { return totalReservations; }
    public void setTotalReservations(long totalReservations) { this.totalReservations = totalReservations; }

    public long getActiveReservations() { return activeReservations; }
    public void setActiveReservations(long activeReservations) { this.activeReservations = activeReservations; }

    public long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public long getTotalMenuItems() { return totalMenuItems; }
    public void setTotalMenuItems(long totalMenuItems) { this.totalMenuItems = totalMenuItems; }

    public long getPendingReviews() { return pendingReviews; }
    public void setPendingReviews(long pendingReviews) { this.pendingReviews = pendingReviews; }
}
