package com.flavorspot.service;

import com.flavorspot.dto.request.CategoryRequest;
import com.flavorspot.dto.request.MenuItemRequest;
import com.flavorspot.dto.response.CategoryResponse;
import com.flavorspot.dto.response.MenuItemResponse;

import java.util.List;

public interface MenuService {

    // Category operations
    List<CategoryResponse> getAllCategories();
    CategoryResponse createCategory(CategoryRequest request);

    // Customer Menu operations
    List<MenuItemResponse> getAvailableMenuItems();
    List<MenuItemResponse> getMenuItemsByCategory(Long categoryId);
    List<MenuItemResponse> searchMenuItems(String keyword);
    MenuItemResponse getMenuItemById(Long id);

    // Admin Menu operations
    List<MenuItemResponse> getAllMenuItemsForAdmin();
    MenuItemResponse createMenuItem(MenuItemRequest request);
    MenuItemResponse updateMenuItem(Long id, MenuItemRequest request);
    MenuItemResponse toggleAvailability(Long id, Boolean available);
    void deleteMenuItem(Long id);
}
