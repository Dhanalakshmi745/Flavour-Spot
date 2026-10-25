package com.flavorspot.service;

import com.flavorspot.dto.request.OrderRequest;
import com.flavorspot.dto.response.OrderResponse;
import com.flavorspot.entity.OrderStatus;

import java.util.List;

public interface OrderService {

    OrderResponse placeOrder(OrderRequest request);
    List<OrderResponse> getOrdersByCustomer(Long userId);
    OrderResponse getOrderById(Long id);
    List<OrderResponse> getAllOrdersForAdmin();
    List<OrderResponse> getOrdersByStatus(OrderStatus status);
    OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus);
    OrderResponse cancelOrder(Long orderId, Long userId);
}
