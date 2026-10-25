package com.flavorspot.controller;

import com.flavorspot.dto.response.AdminStatsResponse;
import com.flavorspot.dto.response.ApiResponse;
import com.flavorspot.entity.OrderStatus;
import com.flavorspot.entity.ReservationStatus;
import com.flavorspot.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final ReviewRepository reviewRepository;

    public AdminController(UserRepository userRepository,
                           ReservationRepository reservationRepository,
                           OrderRepository orderRepository,
                           MenuItemRepository menuItemRepository,
                           ReviewRepository reviewRepository) {
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
        this.reviewRepository = reviewRepository;
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<AdminStatsResponse>> getAdminStats() {
        long totalCustomers = userRepository.count();
        long totalReservations = reservationRepository.count();
        long activeReservations = reservationRepository.findAll().stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED || r.getStatus() == ReservationStatus.PENDING)
                .count();

        long totalOrders = orderRepository.count();

        // Calculate total gross revenue from active/completed orders (excluding cancelled)
        BigDecimal totalRevenue = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
                .map(o -> o.getTotalAmount() != null ? o.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalMenuItems = menuItemRepository.count();
        long pendingReviews = reviewRepository.findAll().stream()
                .filter(r -> !Boolean.TRUE.equals(r.getIsApproved()))
                .count();

        AdminStatsResponse stats = new AdminStatsResponse(
                totalCustomers,
                totalReservations,
                activeReservations,
                totalOrders,
                totalRevenue,
                totalMenuItems,
                pendingReviews
        );

        return ResponseEntity.ok(
                ApiResponse.success("Admin KPIs retrieved successfully", stats)
        );
    }
}
