-- =====================================================================
-- Flavor Spot - Database Schema DDL
-- Restaurant Reservation and Dining Management System
-- Normalized to Third Normal Form (3NF)
-- =====================================================================

CREATE DATABASE IF NOT EXISTS flavorspot_db;
USE flavorspot_db;

-- 1. Users Table (Handles Customer and Admin credentials & roles)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    role VARCHAR(30) NOT NULL DEFAULT 'ROLE_CUSTOMER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_user_role CHECK (role IN ('ROLE_CUSTOMER', 'ROLE_ADMIN'))
) ENGINE=InnoDB;

-- 2. Restaurant Tables (Physical dining tables inside the restaurant)
CREATE TABLE IF NOT EXISTS restaurant_tables (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    table_number VARCHAR(20) NOT NULL UNIQUE,
    seating_capacity INT NOT NULL,
    location_area VARCHAR(50) NOT NULL DEFAULT 'MAIN_DINING', -- e.g., 'TERRACE', 'VIP', 'PATIO'
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_seating_capacity CHECK (seating_capacity > 0)
) ENGINE=InnoDB;

-- 3. Reservations Table (Customer bookings for specific tables and slots)
CREATE TABLE IF NOT EXISTS reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    table_id BIGINT NOT NULL,
    reservation_date DATE NOT NULL,
    reservation_time TIME NOT NULL,
    guest_count INT NOT NULL,
    special_requests VARCHAR(255),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_res_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_res_table FOREIGN KEY (table_id) REFERENCES restaurant_tables(id) ON DELETE RESTRICT,
    CONSTRAINT chk_res_status CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED')),
    CONSTRAINT chk_guest_count CHECK (guest_count > 0)
) ENGINE=InnoDB;

-- Index to fast-query slot availability and prevent overlapping reservations
CREATE INDEX idx_res_date_time_table ON reservations (reservation_date, reservation_time, table_id);

-- 4. Menu Categories (Starters, Main Course, Desserts, Beverages)
CREATE TABLE IF NOT EXISTS menu_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(80) NOT NULL UNIQUE,
    description VARCHAR(255),
    display_order INT NOT NULL DEFAULT 0
) ENGINE=InnoDB;

-- 5. Menu Items (Food products with prices and category linkage)
CREATE TABLE IF NOT EXISTS menu_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    name VARCHAR(120) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    image_url VARCHAR(255),
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_menu_category FOREIGN KEY (category_id) REFERENCES menu_categories(id) ON DELETE RESTRICT,
    CONSTRAINT chk_item_price CHECK (price >= 0.00)
) ENGINE=InnoDB;

-- 6. Orders Table (Enforces business rule: order is tied to a confirmed reservation)
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    reservation_id BIGINT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    status VARCHAR(30) NOT NULL DEFAULT 'PLACED',
    special_instructions VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_reservation FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE RESTRICT,
    CONSTRAINT chk_order_status CHECK (status IN ('PLACED', 'PREPARING', 'READY', 'SERVED', 'CANCELLED')),
    CONSTRAINT chk_total_amount CHECK (total_amount >= 0.00)
) ENGINE=InnoDB;

-- 7. Order Items (Line items capturing the unit price at time of order)
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    menu_item_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_oi_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_oi_menu_item FOREIGN KEY (menu_item_id) REFERENCES menu_items(id) ON DELETE RESTRICT,
    CONSTRAINT chk_oi_quantity CHECK (quantity > 0)
) ENGINE=InnoDB;

-- 8. Customer Reviews and Testimonials
CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    rating INT NOT NULL,
    comment TEXT NOT NULL,
    is_approved BOOLEAN NOT NULL DEFAULT FALSE, -- Admin moderates before showing on home page
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_review_rating CHECK (rating BETWEEN 1 AND 5)
) ENGINE=InnoDB;
