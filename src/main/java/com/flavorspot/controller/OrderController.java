package com.flavorspot.controller;

import com.flavorspot.dto.request.OrderRequest;
import com.flavorspot.dto.response.ApiResponse;
import com.flavorspot.dto.response.OrderResponse;
import com.flavorspot.entity.OrderStatus;
import com.flavorspot.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // -------------------------------------------------------------
    // Customer Endpoints
    // -------------------------------------------------------------

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse order = orderService.placeOrder(request);
        return new ResponseEntity<>(
                ApiResponse.success("Order placed successfully! The kitchen has received your ticket.", order),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders(@RequestParam Long userId) {
        List<OrderResponse> orders = orderService.getOrdersByCustomer(userId);
        return ResponseEntity.ok(
                ApiResponse.success("Customer order history retrieved", orders)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Long id) {
        OrderResponse order = orderService.getOrderById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Order details retrieved", order)
        );
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable Long id,
            @RequestParam Long userId) {
        OrderResponse cancelled = orderService.cancelOrder(id, userId);
        return ResponseEntity.ok(
                ApiResponse.success("Order #" + id + " has been cancelled.", cancelled)
        );
    }

    // -------------------------------------------------------------
    // Kitchen & Admin Operations
    // -------------------------------------------------------------

    @GetMapping("/admin/all")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrdersForAdmin() {
        List<OrderResponse> orders = orderService.getAllOrdersForAdmin();
        return ResponseEntity.ok(
                ApiResponse.success("All restaurant orders retrieved", orders)
        );
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByStatus(
            @RequestParam OrderStatus status) {
        List<OrderResponse> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(
                ApiResponse.success("Orders filtered by status: " + status, orders)
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        OrderResponse updated = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(
                ApiResponse.success("Order #" + id + " status updated to " + status, updated)
        );
    }
}
