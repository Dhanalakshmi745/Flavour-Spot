package com.flavorspot.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TableRequest {

    @NotBlank(message = "Table number is required")
    @Size(max = 20, message = "Table number cannot exceed 20 characters")
    private String tableNumber;

    @NotNull(message = "Seating capacity is required")
    @Min(value = 1, message = "Seating capacity must be at least 1")
    private Integer seatingCapacity;

    @NotBlank(message = "Location area is required")
    private String locationArea = "MAIN_DINING";

    private Boolean isActive = true;

    public TableRequest() {}

    public TableRequest(String tableNumber, Integer seatingCapacity, String locationArea, Boolean isActive) {
        this.tableNumber = tableNumber;
        this.seatingCapacity = seatingCapacity;
        this.locationArea = locationArea != null ? locationArea : "MAIN_DINING";
        this.isActive = isActive != null ? isActive : true;
    }

    public String getTableNumber() { return tableNumber; }
    public void setTableNumber(String tableNumber) { this.tableNumber = tableNumber; }

    public Integer getSeatingCapacity() { return seatingCapacity; }
    public void setSeatingCapacity(Integer seatingCapacity) { this.seatingCapacity = seatingCapacity; }

    public String getLocationArea() { return locationArea; }
    public void setLocationArea(String locationArea) { this.locationArea = locationArea; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
