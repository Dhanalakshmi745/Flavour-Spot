package com.flavorspot.dto.response;

import com.flavorspot.entity.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ReservationResponse {

    private Long id;
    private Long userId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private Long tableId;
    private String tableNumber;
    private Integer tableCapacity;
    private String locationArea;
    private LocalDate reservationDate;
    private LocalTime reservationTime;
    private Integer guestCount;
    private String specialRequests;
    private ReservationStatus status;
    private LocalDateTime createdAt;

    public ReservationResponse() {}

    public ReservationResponse(Long id, Long userId, String customerName, String customerEmail, 
                               String customerPhone, Long tableId, String tableNumber, 
                               Integer tableCapacity, String locationArea, LocalDate reservationDate, 
                               LocalTime reservationTime, Integer guestCount, String specialRequests, 
                               ReservationStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.tableId = tableId;
        this.tableNumber = tableNumber;
        this.tableCapacity = tableCapacity;
        this.locationArea = locationArea;
        this.reservationDate = reservationDate;
        this.reservationTime = reservationTime;
        this.guestCount = guestCount;
        this.specialRequests = specialRequests;
        this.status = status;
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

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public Long getTableId() { return tableId; }
    public void setTableId(Long tableId) { this.tableId = tableId; }

    public String getTableNumber() { return tableNumber; }
    public void setTableNumber(String tableNumber) { this.tableNumber = tableNumber; }

    public Integer getTableCapacity() { return tableCapacity; }
    public void setTableCapacity(Integer tableCapacity) { this.tableCapacity = tableCapacity; }

    public String getLocationArea() { return locationArea; }
    public void setLocationArea(String locationArea) { this.locationArea = locationArea; }

    public LocalDate getReservationDate() { return reservationDate; }
    public void setReservationDate(LocalDate reservationDate) { this.reservationDate = reservationDate; }

    public LocalTime getReservationTime() { return reservationTime; }
    public void setReservationTime(LocalTime reservationTime) { this.reservationTime = reservationTime; }

    public Integer getGuestCount() { return guestCount; }
    public void setGuestCount(Integer guestCount) { this.guestCount = guestCount; }

    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }

    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
