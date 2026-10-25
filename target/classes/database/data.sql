-- =====================================================================
-- Flavor Spot - Realistic Seed Data
-- Passwords:
-- Admin:    admin@flavorspot.com    / admin123
-- Customer: john.doe@example.com    / customer123
-- (Hashed with BCrypt: strength 10)
-- =====================================================================

USE flavorspot_db;

-- 1. Insert Users (Admin & Customer)
-- Password for 'admin123': $2a$10$GRLdNNYKEIVxUlYnvgrGW.iF7Gv74pZ.fQOhy5aT9tWj9tVreA5zC
-- Password for 'customer123': $2a$10$xK7J48e89fK1uC0sY1KkI.v5E9kP4qKz4gR2b9sN1d0uU2Z8yq123
INSERT INTO users (id, full_name, email, password, phone_number, role) VALUES
(1, 'Restaurant Admin', 'admin@flavorspot.com', '$2a$10$GRLdNNYKEIVxUlYnvgrGW.iF7Gv74pZ.fQOhy5aT9tWj9tVreA5zC', '+1 555-0199', 'ROLE_ADMIN'),
(2, 'John Doe', 'john.doe@example.com', '$2a$10$GRLdNNYKEIVxUlYnvgrGW.iF7Gv74pZ.fQOhy5aT9tWj9tVreA5zC', '+1 555-0144', 'ROLE_CUSTOMER')
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);

-- 2. Insert Restaurant Tables
INSERT INTO restaurant_tables (id, table_number, seating_capacity, location_area, is_active) VALUES
(1, 'T-01', 2, 'WINDOW_SIDE', TRUE),
(2, 'T-02', 4, 'MAIN_DINING', TRUE),
(3, 'T-03', 4, 'MAIN_DINING', TRUE),
(4, 'T-04', 6, 'PATIO_GARDEN', TRUE),
(5, 'T-05', 8, 'VIP_LOUNGE', TRUE)
ON DUPLICATE KEY UPDATE seating_capacity=VALUES(seating_capacity);

-- 3. Insert Menu Categories
INSERT INTO menu_categories (id, name, description, display_order) VALUES
(1, 'Starters & Appetizers', 'Crispy bites and flavorful small plates to begin your journey', 1),
(2, 'Artisan Mains', 'Chef-crafted signature main entrees prepared with organic ingredients', 2),
(3, 'Wood-Fired Pizzas', 'Hand-stretched sourdough pizzas baked in high heat stone ovens', 3),
(4, 'Desserts & Sweets', 'Decadent sweet endings handcrafted daily by our pastry chef', 4),
(5, 'Artisan Beverages', 'Handcrafted cocktails, mocktails, and fresh-squeezed blends', 5)
ON DUPLICATE KEY UPDATE description=VALUES(description);

-- 4. Insert Menu Items
INSERT INTO menu_items (id, category_id, name, description, price, image_url, is_available) VALUES
(1, 1, 'Truffle Parmesan Bruschetta', 'Toasted artisan sourdough with balsamic glaze, heirloom tomatoes, and aged parmesan', 8.50, 'https://images.unsplash.com/photo-1572695157366-5e585ab2b69f?auto=format&fit=crop&w=600&q=80', TRUE),
(2, 1, 'Crispy Calamari Fritti', 'Tender squid rings served with zesty house marinara and roasted garlic lemon aioli', 11.00, 'https://images.unsplash.com/photo-1604909052743-94e838986d24?auto=format&fit=crop&w=600&q=80', TRUE),
(3, 2, 'Pan-Seared Atlantic Salmon', 'Wild-caught salmon with creamy saffron risotto and grilled asparagus spears', 24.50, 'https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?auto=format&fit=crop&w=600&q=80', TRUE),
(4, 2, 'Grilled Prime Ribeye (10oz)', 'Grass-fed beef steak with rosemary butter, truffle fries, and red wine reduction', 29.00, 'https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&w=600&q=80', TRUE),
(5, 3, 'Classic Margherita Pizza', 'San Marzano tomato base, fresh buffalo mozzarella, aromatic basil, and olive oil', 14.00, 'https://images.unsplash.com/photo-1604382354936-07c5d9983bd3?auto=format&fit=crop&w=600&q=80', TRUE),
(6, 3, 'Spicy Diavola Pepperoni', 'Smoked mozzarella, artisan pepperoni, hot calabrian chili oil, and fresh oregano', 16.50, 'https://images.unsplash.com/photo-1628840042765-356cda07504e?auto=format&fit=crop&w=600&q=80', TRUE),
(7, 4, 'Classic Italian Tiramisu', 'Espresso-soaked savoiardi ladyfingers layered with rich mascarpone cocoa cream', 7.50, 'https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?auto=format&fit=crop&w=600&q=80', TRUE),
(8, 4, 'Warm Molten Lava Cake', 'Belgian dark chocolate cake with a gooey flowing center and Madagascar vanilla gelato', 8.50, 'https://images.unsplash.com/photo-1606313564200-e75d5e30476c?auto=format&fit=crop&w=600&q=80', TRUE),
(9, 5, 'Berry Sparkler Mocktail', 'Muddled seasonal berries, mint, lime juice, and sparkling mineral water', 5.50, 'https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?auto=format&fit=crop&w=600&q=80', TRUE)
ON DUPLICATE KEY UPDATE price=VALUES(price);

-- 5. Insert Sample Confirmed Reservation (Eligible for ordering)
INSERT INTO reservations (id, user_id, table_id, reservation_date, reservation_time, guest_count, special_requests, status) VALUES
(1, 2, 2, CURDATE(), '19:30:00', 3, 'Anniversary dinner by the corner table', 'CONFIRMED')
ON DUPLICATE KEY UPDATE status=VALUES(status);

-- 6. Insert Reviews & Testimonials
INSERT INTO reviews (id, user_id, rating, comment, is_approved) VALUES
(1, 2, 5, 'The Atlantic Salmon and Wood-Fired Pizza were sublime! The ambiance and table reservation experience was seamless.', TRUE),
(2, 2, 4, 'Warm Molten Lava Cake is to die for. Friendly staff and prompt service.', TRUE)
ON DUPLICATE KEY UPDATE rating=VALUES(rating);
