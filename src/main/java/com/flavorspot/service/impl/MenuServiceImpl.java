package com.flavorspot.service.impl;

import com.flavorspot.dto.request.CategoryRequest;
import com.flavorspot.dto.request.MenuItemRequest;
import com.flavorspot.dto.response.CategoryResponse;
import com.flavorspot.dto.response.MenuItemResponse;
import com.flavorspot.entity.MenuCategory;
import com.flavorspot.entity.MenuItem;
import com.flavorspot.exception.BadRequestException;
import com.flavorspot.exception.ResourceNotFoundException;
import com.flavorspot.repository.MenuCategoryRepository;
import com.flavorspot.repository.MenuItemRepository;
import com.flavorspot.service.MenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl implements MenuService {

    private final MenuCategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;

    public MenuServiceImpl(MenuCategoryRepository categoryRepository, MenuItemRepository menuItemRepository) {
        this.categoryRepository = categoryRepository;
        this.menuItemRepository = menuItemRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAllByOrderByDisplayOrderAsc()
                .stream()
                .map(this::mapToCategoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        categoryRepository.findByName(request.getName().trim()).ifPresent(c -> {
            throw new BadRequestException("Category with name '" + request.getName() + "' already exists.");
        });

        MenuCategory category = new MenuCategory(
                request.getName().trim(),
                request.getDescription(),
                request.getDisplayOrder()
        );

        MenuCategory saved = categoryRepository.save(category);
        return mapToCategoryResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getAvailableMenuItems() {
        return menuItemRepository.findByIsAvailableTrue()
                .stream()
                .map(this::mapToMenuItemResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getMenuItemsByCategory(Long categoryId) {
        // Validate category exists
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("MenuCategory", "id", categoryId);
        }

        return menuItemRepository.findByCategoryIdAndIsAvailableTrue(categoryId)
                .stream()
                .map(this::mapToMenuItemResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemResponse> searchMenuItems(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAvailableMenuItems();
        }

        return menuItemRepository.findByNameContainingIgnoreCase(keyword.trim())
                .stream()
                .filter(MenuItem::getIsAvailable)
                .map(this::mapToMenuItemResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MenuItemResponse getMenuItemById(Long id) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", "id", id));
        return mapToMenuItemResponse(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemResponse> getAllMenuItemsForAdmin() {
        return menuItemRepository.findAll()
                .stream()
                .map(this::mapToMenuItemResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MenuItemResponse createMenuItem(MenuItemRequest request) {
        MenuCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("MenuCategory", "id", request.getCategoryId()));

        MenuItem item = new MenuItem(
                category,
                request.getName().trim(),
                request.getDescription(),
                request.getPrice(),
                request.getImageUrl(),
                request.getIsAvailable() != null ? request.getIsAvailable() : true
        );

        MenuItem saved = menuItemRepository.save(item);
        return mapToMenuItemResponse(saved);
    }

    @Override
    @Transactional
    public MenuItemResponse updateMenuItem(Long id, MenuItemRequest request) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", "id", id));

        MenuCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("MenuCategory", "id", request.getCategoryId()));

        item.setCategory(category);
        item.setName(request.getName().trim());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setImageUrl(request.getImageUrl());
        if (request.getIsAvailable() != null) {
            item.setIsAvailable(request.getIsAvailable());
        }

        MenuItem updated = menuItemRepository.save(item);
        return mapToMenuItemResponse(updated);
    }

    @Override
    @Transactional
    public MenuItemResponse toggleAvailability(Long id, Boolean available) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", "id", id));

        item.setIsAvailable(available);
        MenuItem updated = menuItemRepository.save(item);
        return mapToMenuItemResponse(updated);
    }

    @Override
    @Transactional
    public void deleteMenuItem(Long id) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", "id", id));
        menuItemRepository.delete(item);
    }

    // Helper mappers
    private CategoryResponse mapToCategoryResponse(MenuCategory category) {
        int count = menuItemRepository.findByCategoryId(category.getId()).size();
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getDisplayOrder(),
                count
        );
    }

    private MenuItemResponse mapToMenuItemResponse(MenuItem item) {
        return new MenuItemResponse(
                item.getId(),
                item.getCategory().getId(),
                item.getCategory().getName(),
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getImageUrl(),
                item.getIsAvailable()
        );
    }
}
