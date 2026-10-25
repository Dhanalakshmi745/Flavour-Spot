package com.flavorspot.controller;

import com.flavorspot.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller to test and verify database connectivity and Spring Data JPA repositories.
 */
@RestController
@RequestMapping("/api/db-status")
public class DatabaseStatusController {

    private final UserRepository userRepository;
    private final RestaurantTableRepository tableRepository;
    private final MenuCategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;
    private final ReservationRepository reservationRepository;
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;

    public DatabaseStatusController(UserRepository userRepository,
                                    RestaurantTableRepository tableRepository,
                                    MenuCategoryRepository categoryRepository,
                                    MenuItemRepository menuItemRepository,
                                    ReservationRepository reservationRepository,
                                    OrderRepository orderRepository,
                                    ReviewRepository reviewRepository) {
        this.userRepository = userRepository;
        this.tableRepository = tableRepository;
        this.categoryRepository = categoryRepository;
        this.menuItemRepository = menuItemRepository;
        this.reservationRepository = reservationRepository;
        this.orderRepository = orderRepository;
        this.reviewRepository = reviewRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDatabaseStatus() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("databaseConnected", true);
        stats.put("totalUsers", userRepository.count());
        stats.put("totalRestaurantTables", tableRepository.count());
        stats.put("totalMenuCategories", categoryRepository.count());
        stats.put("totalMenuItems", menuItemRepository.count());
        stats.put("totalReservations", reservationRepository.count());
        stats.put("totalOrders", orderRepository.count());
        stats.put("totalReviews", reviewRepository.count());
        stats.put("message", "Phase 4 Complete: Spring Data JPA Entities & Repositories successfully connected to MySQL!");
        return ResponseEntity.ok(stats);
    }
}
