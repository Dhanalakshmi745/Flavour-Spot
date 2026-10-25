package com.flavorspot.dto.response;

public class CategoryResponse {

    private Long id;
    private String name;
    private String description;
    private Integer displayOrder;
    private Integer itemCount;

    public CategoryResponse() {}

    public CategoryResponse(Long id, String name, String description, Integer displayOrder, Integer itemCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.displayOrder = displayOrder;
        this.itemCount = itemCount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public Integer getItemCount() { return itemCount; }
    public void setItemCount(Integer itemCount) { this.itemCount = itemCount; }
}
