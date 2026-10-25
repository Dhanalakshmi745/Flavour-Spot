package com.flavorspot.service.impl;

import com.flavorspot.dto.request.OrderItemRequest;
import com.flavorspot.dto.request.OrderRequest;
import com.flavorspot.dto.response.OrderItemResponse;
import com.flavorspot.dto.response.OrderResponse;
import com.flavorspot.entity.*;
import com.flavorspot.exception.BadRequestException;
import com.flavorspot.exception.ResourceNotFoundException;
import com.flavorspot.repository.MenuItemRepository;
import com.flavorspot.repository.OrderRepository;
import com.flavorspot.repository.ReservationRepository;
import com.flavorspot.repository.UserRepository;
import com.flavorspot.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final MenuItemRepository menuItemRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            UserRepository userRepository,
                            ReservationRepository reservationRepository,
                            MenuItemRepository menuItemRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
        this.menuItemRepository = menuItemRepository;
    }

    @Override
    @Transactional
    public OrderResponse placeOrder(OrderRequest request) {
        // 1. Validate Customer
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        // 2. Strict Business Rule: Enforce that customer has an active CONFIRMED reservation
        Reservation reservation = reservationRepository.findConfirmedReservationForUser(
                request.getReservationId(), request.getUserId())
                .orElseThrow(() -> new BadRequestException(
                        "Dine-in food orders require an active CONFIRMED table reservation. " +
                        "Reservation #" + request.getReservationId() + " is either not confirmed or does not belong to your account."));

        // 3. Initialize Order Entity
        Order order = new Order(
                user,
                reservation,
                BigDecimal.ZERO,
                OrderStatus.PLACED,
                request.getSpecialInstructions()
        );

        // 4. Server-Side Price Calculation & Snapshot Capture
        BigDecimal calculatedGrandTotal = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemReq.getMenuItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("MenuItem", "id", itemReq.getMenuItemId()));

            if (!Boolean.TRUE.equals(menuItem.getIsAvailable())) {
                throw new BadRequestException("Item '" + menuItem.getName() + "' is currently out of stock.");
            }

            BigDecimal unitPrice = menuItem.getPrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            calculatedGrandTotal = calculatedGrandTotal.add(subtotal);

            // Create OrderItem capturing the price at this exact moment
            OrderItem orderItem = new OrderItem(order, menuItem, itemReq.getQuantity(), unitPrice);
            order.addOrderItem(orderItem);
        }

        order.setTotalAmount(calculatedGrandTotal);

        // 5. Persist Order and its Cascade Items
        Order savedOrder = orderRepository.save(order);
        return mapToOrderResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCustomer(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }

        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        return mapToOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrdersForAdmin() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatusOrderByCreatedAtAsc(status).stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        order.setStatus(newStatus);
        Order updated = orderRepository.save(order);
        return mapToOrderResponse(updated);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (!order.getUser().getId().equals(userId)) {
            throw new BadRequestException("You are not authorized to cancel this order.");
        }

        // Business rule: Once kitchen starts cooking, order cannot be cancelled
        if (order.getStatus() != OrderStatus.PLACED) {
            throw new BadRequestException("Order #" + orderId + " cannot be cancelled because the kitchen has already started preparing it (Status: " + order.getStatus() + ").");
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order updated = orderRepository.save(order);
        return mapToOrderResponse(updated);
    }

    // -------------------------------------------------------------
    // Helper Mappers
    // -------------------------------------------------------------

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(this::mapToOrderItemResponse)
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getUser().getFullName(),
                order.getUser().getEmail(),
                order.getReservation().getId(),
                order.getReservation().getTable().getTableNumber(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getSpecialInstructions(),
                itemResponses,
                order.getCreatedAt()
        );
    }

    private OrderItemResponse mapToOrderItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getMenuItem().getId(),
                item.getMenuItem().getName(),
                item.getMenuItem().getCategory().getName(),
                item.getMenuItem().getImageUrl(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubtotal()
        );
    }
}
