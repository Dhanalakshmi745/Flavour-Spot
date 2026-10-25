package com.flavorspot.dto.response;

public class TableResponse {

    private Long id;
    private String tableNumber;
    private Integer seatingCapacity;
    private String locationArea;
    private Boolean isActive;

    public TableResponse() {}

    public TableResponse(Long id, String tableNumber, Integer seatingCapacity, String locationArea, Boolean isActive) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.seatingCapacity = seatingCapacity;
        this.locationArea = locationArea;
        this.isActive = isActive;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTableNumber() { return tableNumber; }
    public void setTableNumber(String tableNumber) { this.tableNumber = tableNumber; }

    public Integer getSeatingCapacity() { return seatingCapacity; }
    public void setSeatingCapacity(Integer seatingCapacity) { this.seatingCapacity = seatingCapacity; }

    public String getLocationArea() { return locationArea; }
    public void setLocationArea(String locationArea) { this.locationArea = locationArea; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
