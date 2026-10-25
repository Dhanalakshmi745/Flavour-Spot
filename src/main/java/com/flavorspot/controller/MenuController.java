package com.flavorspot.controller;

import com.flavorspot.dto.request.CategoryRequest;
import com.flavorspot.dto.request.MenuItemRequest;
import com.flavorspot.dto.response.ApiResponse;
import com.flavorspot.dto.response.CategoryResponse;
import com.flavorspot.dto.response.MenuItemResponse;
import com.flavorspot.service.MenuService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    // -------------------------------------------------------------
    // Category Endpoints
    // -------------------------------------------------------------

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories() {
        List<CategoryResponse> categories = menuService.getAllCategories();
        return ResponseEntity.ok(
                ApiResponse.success("Menu categories retrieved successfully", categories)
        );
    }

    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse category = menuService.createCategory(request);
        return new ResponseEntity<>(
                ApiResponse.success("Category created successfully", category),
                HttpStatus.CREATED
        );
    }

    // -------------------------------------------------------------
    // Menu Item Endpoints (Customer & Public Browsing)
    // -------------------------------------------------------------

    @GetMapping("/items")
    public ResponseEntity<ApiResponse<List<MenuItemResponse>>> getMenuItems(
            @RequestParam(required = false) Long categoryId) {
        List<MenuItemResponse> items = (categoryId != null)
                ? menuService.getMenuItemsByCategory(categoryId)
                : menuService.getAvailableMenuItems();

        return ResponseEntity.ok(
                ApiResponse.success("Menu items retrieved successfully", items)
        );
    }

    @GetMapping("/items/search")
    public ResponseEntity<ApiResponse<List<MenuItemResponse>>> searchMenuItems(
            @RequestParam(required = false) String query) {
        List<MenuItemResponse> items = menuService.searchMenuItems(query);
        return ResponseEntity.ok(
                ApiResponse.success("Search completed successfully", items)
        );
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<ApiResponse<MenuItemResponse>> getMenuItemById(@PathVariable Long id) {
        MenuItemResponse item = menuService.getMenuItemById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Menu item details retrieved", item)
        );
    }

    // -------------------------------------------------------------
    // Admin Menu Item Operations
    // -------------------------------------------------------------

    @GetMapping("/items/admin/all")
    public ResponseEntity<ApiResponse<List<MenuItemResponse>>> getAllItemsForAdmin() {
        List<MenuItemResponse> items = menuService.getAllMenuItemsForAdmin();
        return ResponseEntity.ok(
                ApiResponse.success("Admin menu items list retrieved", items)
        );
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<MenuItemResponse>> createMenuItem(@Valid @RequestBody MenuItemRequest request) {
        MenuItemResponse item = menuService.createMenuItem(request);
        return new ResponseEntity<>(
                ApiResponse.success("Menu item created successfully", item),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<ApiResponse<MenuItemResponse>> updateMenuItem(
            @PathVariable Long id,
            @Valid @RequestBody MenuItemRequest request) {
        MenuItemResponse item = menuService.updateMenuItem(id, request);
        return ResponseEntity.ok(
                ApiResponse.success("Menu item updated successfully", item)
        );
    }

    @PatchMapping("/items/{id}/availability")
    public ResponseEntity<ApiResponse<MenuItemResponse>> toggleAvailability(
            @PathVariable Long id,
            @RequestParam Boolean available) {
        MenuItemResponse item = menuService.toggleAvailability(id, available);
        String status = Boolean.TRUE.equals(available) ? "available" : "unavailable";
        return ResponseEntity.ok(
                ApiResponse.success("Menu item marked as " + status, item)
        );
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMenuItem(@PathVariable Long id) {
        menuService.deleteMenuItem(id);
        return ResponseEntity.ok(
                ApiResponse.success("Menu item deleted successfully", null)
        );
    }
}
