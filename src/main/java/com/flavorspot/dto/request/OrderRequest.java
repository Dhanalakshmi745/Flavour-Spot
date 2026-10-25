package com.flavorspot.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class OrderRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Reservation ID is required. Dine-in orders require a confirmed table booking.")
    private Long reservationId;

    @NotEmpty(message = "Order must contain at least one food item")
    @Valid
    private List<OrderItemRequest> items;

    @Size(max = 255, message = "Special instructions cannot exceed 255 characters")
    private String specialInstructions;

    public OrderRequest() {}

    public OrderRequest(Long userId, Long reservationId, List<OrderItemRequest> items, String specialInstructions) {
        this.userId = userId;
        this.reservationId = reservationId;
        this.items = items;
        this.specialInstructions = specialInstructions;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }

    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }
}
