package com.flavorspot.dto.response;

import com.flavorspot.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {

    private Long id;
    private Long userId;
    private String customerName;
    private String customerEmail;
    private Long reservationId;
    private String tableNumber;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private String specialInstructions;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;

    public OrderResponse() {}

    public OrderResponse(Long id, Long userId, String customerName, String customerEmail, 
                         Long reservationId, String tableNumber, BigDecimal totalAmount, 
                         OrderStatus status, String specialInstructions, 
                         List<OrderItemResponse> items, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.reservationId = reservationId;
        this.tableNumber = tableNumber;
        this.totalAmount = totalAmount;
        this.status = status;
        this.specialInstructions = specialInstructions;
        this.items = items;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }

    public String getTableNumber() { return tableNumber; }
    public void setTableNumber(String tableNumber) { this.tableNumber = tableNumber; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

    public List<OrderItemResponse> getItems() { return items; }
    public void setItems(List<OrderItemResponse> items) { this.items = items; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
