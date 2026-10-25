package com.flavorspot.config;

import com.flavorspot.entity.*;
import com.flavorspot.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Automatically seeds the database on application startup if empty.
 * Works seamlessly with both MySQL and H2 in-memory databases.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RestaurantTableRepository tableRepository;
    private final MenuCategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;
    private final ReservationRepository reservationRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           RestaurantTableRepository tableRepository,
                           MenuCategoryRepository categoryRepository,
                           MenuItemRepository menuItemRepository,
                           ReservationRepository reservationRepository,
                           ReviewRepository reviewRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tableRepository = tableRepository;
        this.categoryRepository = categoryRepository;
        this.menuItemRepository = menuItemRepository;
        this.reservationRepository = reservationRepository;
        this.reviewRepository = reviewRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return; // Already seeded
        }

        System.out.println(">>> Initializing Flavor Spot Seed Data in Database...");

        // 1. Users (Admin and Customer)
        String hashedPass = passwordEncoder.encode("admin123");
        User admin = new User("Restaurant Admin", "admin@flavorspot.com", hashedPass, "+1 555-0199", Role.ROLE_ADMIN);
        User customer = new User("John Doe", "john.doe@example.com", hashedPass, "+1 555-0144", Role.ROLE_CUSTOMER);
        userRepository.saveAll(List.of(admin, customer));

        // 2. Restaurant Tables
        RestaurantTable t1 = new RestaurantTable("T-01", 2, "WINDOW_SIDE", true);
        RestaurantTable t2 = new RestaurantTable("T-02", 4, "MAIN_DINING", true);
        RestaurantTable t3 = new RestaurantTable("T-03", 4, "MAIN_DINING", true);
        RestaurantTable t4 = new RestaurantTable("T-04", 6, "PATIO_GARDEN", true);
        RestaurantTable t5 = new RestaurantTable("T-05", 8, "VIP_LOUNGE", true);
        tableRepository.saveAll(List.of(t1, t2, t3, t4, t5));

        // 3. Menu Categories
        MenuCategory cat1 = new MenuCategory("Starters & Appetizers", "Crispy bites and flavorful small plates", 1);
        MenuCategory cat2 = new MenuCategory("Artisan Mains", "Chef-crafted signature main entrees", 2);
        MenuCategory cat3 = new MenuCategory("Wood-Fired Pizzas", "Hand-stretched sourdough pizzas baked in high heat stone ovens", 3);
        MenuCategory cat4 = new MenuCategory("Desserts & Sweets", "Decadent sweet endings handcrafted daily", 4);
        MenuCategory cat5 = new MenuCategory("Artisan Beverages", "Handcrafted cocktails and mocktails", 5);
        categoryRepository.saveAll(List.of(cat1, cat2, cat3, cat4, cat5));

        // 4. Menu Items
        MenuItem m1 = new MenuItem(cat1, "Truffle Parmesan Bruschetta", "Toasted artisan sourdough with balsamic glaze and heirloom tomatoes", new BigDecimal("8.50"), "https://images.unsplash.com/photo-1572695157366-5e585ab2b69f?auto=format&fit=crop&w=600&q=80", true);
        MenuItem m2 = new MenuItem(cat1, "Crispy Calamari Fritti", "Tender squid rings served with zesty house marinara and aioli", new BigDecimal("11.00"), "https://images.unsplash.com/photo-1604909052743-94e838986d24?auto=format&fit=crop&w=600&q=80", true);
        MenuItem m3 = new MenuItem(cat2, "Pan-Seared Atlantic Salmon", "Wild-caught salmon with creamy saffron risotto and asparagus", new BigDecimal("24.50"), "https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?auto=format&fit=crop&w=600&q=80", true);
        MenuItem m4 = new MenuItem(cat2, "Grilled Prime Ribeye (10oz)", "Grass-fed beef steak with rosemary butter and truffle fries", new BigDecimal("29.00"), "https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&w=600&q=80", true);
        MenuItem m5 = new MenuItem(cat3, "Classic Margherita Pizza", "San Marzano tomatoes, fresh buffalo mozzarella, and aromatic basil", new BigDecimal("14.00"), "https://images.unsplash.com/photo-1604382354936-07c5d9983bd3?auto=format&fit=crop&w=600&q=80", true);
        MenuItem m6 = new MenuItem(cat3, "Spicy Diavola Pepperoni", "Smoked mozzarella, artisan pepperoni, and calabrian chili oil", new BigDecimal("16.50"), "https://images.unsplash.com/photo-1628840042765-356cda07504e?auto=format&fit=crop&w=600&q=80", true);
        MenuItem m7 = new MenuItem(cat4, "Classic Italian Tiramisu", "Espresso-soaked ladyfingers with rich mascarpone cocoa cream", new BigDecimal("7.50"), "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?auto=format&fit=crop&w=600&q=80", true);
        MenuItem m8 = new MenuItem(cat4, "Warm Molten Lava Cake", "Belgian dark chocolate cake with vanilla bean gelato", new BigDecimal("8.50"), "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?auto=format&fit=crop&w=600&q=80", true);
        MenuItem m9 = new MenuItem(cat5, "Berry Sparkler Mocktail", "Muddled seasonal berries, mint, lime juice, and sparkling water", new BigDecimal("5.50"), "https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?auto=format&fit=crop&w=600&q=80", true);
        menuItemRepository.saveAll(List.of(m1, m2, m3, m4, m5, m6, m7, m8, m9));

        // 5. Sample Confirmed Reservation (Eligible for ordering right away)
        Reservation sampleRes = new Reservation(customer, t2, LocalDate.now().plusDays(1), LocalTime.of(19, 0), 2, "Anniversary dinner", ReservationStatus.CONFIRMED);
        reservationRepository.save(sampleRes);

        // 6. Sample Reviews
        Review r1 = new Review(customer, 5, "The Atlantic Salmon and Wood-Fired Pizza were sublime! Exceptional ambiance.", true);
        Review r2 = new Review(customer, 4, "Warm Molten Lava Cake is to die for. Friendly staff and prompt service.", true);
        reviewRepository.saveAll(List.of(r1, r2));

        System.out.println(">>> Flavor Spot Seed Data Successfully Loaded!");
    }
}
