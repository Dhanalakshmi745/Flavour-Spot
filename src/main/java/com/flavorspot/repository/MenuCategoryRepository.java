package com.flavorspot.repository;

import com.flavorspot.entity.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long> {
    List<MenuCategory> findAllByOrderByDisplayOrderAsc();
    Optional<MenuCategory> findByName(String name);
}
