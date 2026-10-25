# Flavor Spot — Restaurant Reservation and Dining Management System

> **A full-stack enterprise dining management application built with Spring Boot, Spring Data JPA, MySQL, and a modern responsive frontend.**

---

## 1. Project Overview & Business Problem

In high-end dining, operations fail when tables are double-booked or kitchens prepare food for guests who haven't arrived. **Flavor Spot** addresses these challenges by combining:
1. **Double-Booking & Conflict Prevention Engine**: Prevents overlapping bookings for the same table across dining slots in $O(\log N)$ time using composite database indexes.
2. **Reservation-Synchronized Food Ordering**: Enforces a strict business invariant where customers can only order dine-in food if they possess a verified, **`CONFIRMED`** table reservation.
3. **Server-Side Price Authority**: Recomputes item prices and order totals on the backend, capturing historical price snapshots in `order_items` so future menu adjustments never corrupt audit trails.
4. **End-to-End Operational Lifecycle**: Real-time ticket management from kitchen to table (`PLACED` $\rightarrow$ `PREPARING` $\rightarrow$ `READY` $\rightarrow$ `SERVED`), coupled with customer review moderation.

---

## 2. Technology Stack

| Layer | Technologies Used |
| :--- | :--- |
| **Backend** | Java 17, Spring Boot 3.2.5 (Spring MVC, Spring Data JPA, Spring Security) |
| **ORM & Database** | Hibernate 6, MySQL 8.0, HikariCP Connection Pool |
| **Security & Auth** | BCrypt Adaptive Password Hashing (10 rounds), Role-Based Access Control (`ROLE_CUSTOMER`, `ROLE_ADMIN`) |
| **Validation** | Jakarta Bean Validation API (`@Valid`, `@NotBlank`, `@FutureOrPresent`, `@Min`, `@Max`) |
| **Frontend** | HTML5, CSS3 (Modern Glassmorphism & Micro-animations), Bootstrap 5.3, JavaScript (Fetch API & LocalStorage) |
| **Build & Testing** | Maven 3, JUnit 5, Mockito |
| **Dev Environment** | Visual Studio Code, Postman, MySQL Workbench |

---

## 3. System Architecture

Flavor Spot follows a strict **Separation of Concerns (SoC)** layered architecture:

```text
    ┌────────────────────────────────────────────────────────┐
    │     Responsive Client (HTML5 / Bootstrap 5 / JS)       │
    └───────────────────────────┬────────────────────────────┘
                                │ HTTP REST (JSON)
                                ▼
    ┌────────────────────────────────────────────────────────┐
    │            Controller Layer (@RestController)          │
    │  - Endpoints: /api/auth, /api/menu, /api/reservations  │
    │  - Request DTOs with Jakarta Validation (@Valid)       │
    │  - Centralized GlobalExceptionHandler Interceptor      │
    └───────────────────────────┬────────────────────────────┘
                                │ Calls Service Interfaces
                                ▼
    ┌────────────────────────────────────────────────────────┐
    │              Service Layer (@Service)                  │
    │  - Business Invariants & Conflict Prevention Algorithm │
    │  - Server-Side Price Calculation & Entity Mappings     │
    │  - Transaction Management Boundaries (@Transactional)  │
    └───────────────────────────┬────────────────────────────┘
                                │ Uses Repositories
                                ▼
    ┌────────────────────────────────────────────────────────┐
    │            Repository Layer (Spring Data JPA)          │
    │  - Derived Queries & Custom JPQL (@Query)              │
    └───────────────────────────┬────────────────────────────┘
                                │ Hibernate ORM / JDBC
                                ▼
    ┌────────────────────────────────────────────────────────┐
    │                  MySQL 8.0 Relational DB               │
    └────────────────────────────────────────────────────────┘
```

---

## 4. Database Schema (Entity Relationship Diagram)

```
  ┌────────────────┐ 1        N ┌────────────────┐
  │     users      ├───────────►│  reservations  │
  └───────┬────────┘            └───────┬────────┘
          │ 1                           │ 1
          │                             │ (Mandatory confirmed reservation)
          │ N                           │ 1
  ┌───────▼────────┐            ┌───────▼────────┐
  │    reviews     │            │     orders     │
  └────────────────┘            └───────┬────────┘
                                        │ 1
  ┌──────────────────┐ 1      N         │
  │ restaurant_tables├─────────┐        │ N
  └──────────────────┘         │ ┌──────▼────────┐
                               └─► order_items   │
  ┌─────────────────┐ 1      N   └──────▲────────┘
  │ menu_categories ├─────────┐         │ N
  └─────────────────┘         │ ┌───────┴────────┐
                              └─►│  menu_items   │
                                └────────────────┘
```

### Table Definitions & Design Decisions:
- **`users`**: Stores hashed passwords (`BCrypt`), unique email constraints, and role enum (`ROLE_CUSTOMER`, `ROLE_ADMIN`).
- **`restaurant_tables`**: Physical table inventory with seating capacities and locations (*WINDOW_SIDE*, *MAIN_DINING*, *PATIO_GARDEN*, *VIP_LOUNGE*).
- **`reservations`**: Date, time, guest count, and status (`PENDING`, `CONFIRMED`, `CANCELLED`, `COMPLETED`). Fast composite B-Tree index on `(reservation_date, reservation_time, table_id)`.
- **`orders`**: Directly foreign-keyed to `reservations(id)` with `ON DELETE RESTRICT` and total amount recalculated on the server.
- **`order_items`**: Captures a snapshot `unit_price` at the exact millisecond of purchase, protecting financial history.
- **`reviews`**: Customer ratings (1–5) and comments with an `is_approved` moderation flag.

---

## 5. REST API Documentation

### Authentication (`/api/auth`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Registers customer; hashes password with BCrypt |
| `POST` | `/api/auth/login` | Validates credentials; returns profile and role |
| `GET` | `/api/auth/profile/{id}`| Retrieves user profile |
| `POST` | `/api/auth/logout` | Clears authentication session |

### Menu Management (`/api/menu`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/menu/categories` | Retrieves all categories in display order |
| `GET` | `/api/menu/items` | Retrieves available food items (optional `?categoryId=`) |
| `GET` | `/api/menu/items/search`| Full-text keyword search (`?query=salmon`) |
| `POST` | `/api/menu/items` | Admin adds new food item |
| `PUT` | `/api/menu/items/{id}` | Admin updates food item details and price |
| `PATCH`| `/api/menu/items/{id}/availability` | Real-time stock toggle (`?available=true/false`) |
| `DELETE`| `/api/menu/items/{id}` | Admin removes menu item |

### Table Reservations (`/api/reservations`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/reservations/available-tables` | Returns unbooked tables for date, time, and guest count |
| `POST` | `/api/reservations` | Books reservation; validates conflict & operating hours |
| `GET` | `/api/reservations/my?userId=` | Customer reservation history |
| `PATCH`| `/api/reservations/{id}/cancel` | Customer cancels reservation |
| `GET` | `/api/reservations/admin/all` | Admin views all reservations |
| `PATCH`| `/api/reservations/{id}/status` | Admin updates status (`CONFIRMED`, `COMPLETED`, etc.) |

### Food Orders (`/api/orders`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/orders` | Places order; validates confirmed reservation; recomputes totals |
| `GET` | `/api/orders/my?userId=` | Customer order history with live status |
| `PATCH`| `/api/orders/{id}/cancel` | Customer cancels order if still `PLACED` |
| `GET` | `/api/orders/admin/all` | Admin/Kitchen views all tickets |
| `PATCH`| `/api/orders/{id}/status` | Kitchen advances status (`PREPARING`, `READY`, `SERVED`) |

### Customer Reviews & Moderation (`/api/reviews`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/reviews/testimonials` | Public feed of approved testimonials |
| `POST` | `/api/reviews` | Customer submits rating [1–5] and feedback (pending moderation) |
| `GET` | `/api/reviews/admin/all` | Admin review moderation queue |
| `PATCH`| `/api/reviews/{id}/moderate`| Admin toggles approval (`?approved=true/false`) |
| `DELETE`| `/api/reviews/{id}` | Admin deletes review |

### Admin Operations & KPIs (`/api/admin`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/admin/stats` | Aggregates diners, active bookings, gross revenue ($), and queue counts |

---

## 6. How to Set Up and Run Locally

### Prerequisites
- Java Development Kit (JDK 17 or higher)
- MySQL Server (v8.0+)
- Visual Studio Code (with "Extension Pack for Java")

### Step 1: Database Setup
1. Open MySQL Workbench or MySQL CLI:
   ```sql
   SOURCE src/main/resources/database/schema.sql;
   SOURCE src/main/resources/database/data.sql;
   ```
2. Verify your MySQL password in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   ```

### Step 2: Run the Application in VS Code
- Open `src/main/java/com/flavorspot/FlavorSpotApplication.java`.
- Click the **`Run`** link above `public static void main(String[] args)` (or press **F5**).

### Step 3: Open in Browser
Visit `http://localhost:8080/`

**Demo Credentials**:
- **Customer**: `john.doe@example.com` / `admin123`
- **Admin**: `admin@flavorspot.com` / `admin123`

---

## 7. Running Automated Tests

To execute unit and integration test suites:
- In VS Code, navigate to the **Testing** tab on the left sidebar and click **Run All Tests**.
- Or run tests directly on `ReservationServiceTest.java` and `OrderServiceTest.java`.

---
n workflows.
