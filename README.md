# Reservation Management System (ReservePoint)

## Deployment Information

- Deployed (Not currently active)
- **Website URL:** [ReservePoint](http://130.162.229.180/)

## Overview

- ReservePoint is a web-based Reservation Management System built using Spring Boot. 
- It provides a user-friendly interface for users to register, log in, make reservations, and manage their bookings.
- The system ensures secure authentication, real-time reservation tracking, and structured data storage with a MySQL database.

## Core Features

### User Authentication & Management

**User Registration:**
- Users can create an account with a unique username and email.
- Passwords are securely stored using BCrypt hashing.
- Email validation ensures only valid addresses are accepted.

**User Login:**
- Registered users can log in with their credentials.
- Secure session handling ensures users remain logged in.
- Redirects to the home page upon successful login.

**User Roles:**
- Admins have extra privileges (e.g., approving/canceling reservations).

### User Available Features

**Create a Reservation:**
- Users can select a service and submit reservation details.
- The system auto-fills the user's name, surname, and email based on login credentials.
- A unique reservation ID is generated for each booking.

**View Reservations:**
- Users can view a list of their reservations.
- The reservation list includes details such as date, time, and service type.
- The system prevents users from booking the same time slot twice.

### Admin Available Features

**View Reservations:**
- Admin can view a list of all the reservations.
- The reservation list includes details such as date, time, and service type.

**View Reservation Details:**
- When viewing reservations, Admin can click on button **View Details** and see all detailed information about specific reservation.
- While viewing them, Admin has options to:
	- Edit some information of reservation and change the state of reservation (PENDING, CONFIRMED, CANCELED)
	- Delete the reservation completely
	- Add CONFIRMED reservation into application's **calendar**
- 

**View calendar:**
- Admin can view calendar of the reservation system.
- In calendar are all the CONFIRMED reservations added manually by Admin.

## Technologies Used

| Technology                             | Purpose                                                  |
| -------------------------------------- | -------------------------------------------------------- |
| **Spring Boot**                        | Backend framework for handling requests & business logic |
| **Spring Security**                    | Manages authentication & user access                     |
| **Thymeleaf**                          | Server-side templating engine for rendering views        |
| **MySQL**                              | Relational database for storing users & reservations     |
| **Maven**                              | Dependency management & project configuration            |
| **HTML & CSS**                         | Frontend styling & UI design                             |
| **JavaScript (Optional Enhancements)** | Dynamic UI interactions, form validation                 |
| **BCrypt**                             | Secure password hashing                                  |

## Database Scheme

### 1. `customer_entity`

| Column Name  | Data Type      | Constraints                                    |
| ------------ | -------------- | ---------------------------------------------- |
| `id`         | `INT` (PK)     | Auto-increment, Primary Key                    |
| `address`    | `VARCHAR(255)` | Nullable                                       |
| `created_at` | `DATETIME(6)`  | Not null                                       |
| `phone`      | `VARCHAR(255)` | Not null                                       |
| `user_id`    | `INT` (FK)     | Not null, Unique, References `user_entity(id)` |

---

### 2. `reservation_entity`

| Column Name        | Data Type                                  | Constraints                                |
| ------------------ | ------------------------------------------ | ------------------------------------------ |
| `id`               | `BIGINT` (PK)                              | Auto-increment, Primary Key                |
| `created_at`       | `DATETIME(6)`                              | Not null                                   |
| `reservation_date` | `DATE`                                     | Not null                                   |
| `status`           | `ENUM('CANCELED', 'CONFIRMED', 'PENDING')` | Not null                                   |
| `customer_id`      | `INT` (FK)                                 | Not null, References `customer_entity(id)` |
| `service_id`       | `INT` (FK)                                 | Not null, References `service_entity(id)`  |
| `ordered_time`     | `DATETIME(6)`                              | Nullable                                   |
| `notes`            | `TEXT`                                     | Nullable                                   |

---

### 3. `service_entity`

| Column Name   | Data Type      | Constraints                 |
| ------------- | -------------- | --------------------------- |
| `id`          | `INT` (PK)     | Auto-increment, Primary Key |
| `description` | `VARCHAR(255)` | Not null                    |
| `name`        | `VARCHAR(255)` | Not null                    |
| `price`       | `DOUBLE`       | Not null                    |

---

### 4. `user_entity`

| Column Name  | Data Type               | Constraints                 |
| ------------ | ----------------------- | --------------------------- |
| `id`         | `INT` (PK)              | Auto-increment, Primary Key |
| `created_at` | `DATETIME(6)`           | Not null                    |
| `email`      | `VARCHAR(255)`          | Not null, Unique            |
| `name`       | `VARCHAR(255)`          | Not null                    |
| `password`   | `VARCHAR(255)`          | Not null                    |
| `role`       | `ENUM('ADMIN', 'USER')` | Not null                    |
| `surname`    | `VARCHAR(255)`          | Not null                    |
