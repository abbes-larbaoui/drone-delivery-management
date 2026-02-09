# Drone Delivery Management System

A Java Spring Boot application that manages a drone-based delivery workflow.
The system allows users to create delivery orders, drones to execute deliveries, and admins to supervise drones and orders.

This project is designed as an assessment-style backend with:
- JWT authentication
- Role-based authorization
- In-memory repositories
- Clear state transitions for orders and drones

---

## Business Overview

The system manages three main actors:

- **USER**: Creates and manages delivery orders
- **DRONE**: Executes delivery jobs
- **ADMIN**: Monitors and manages drones and orders

Each order follows a strict lifecycle, and drones operate based on their current state.

---

## Architecture

- **Spring Boot 3**
- **Java 17**
- **Spring Security (JWT-based)**
- **In-memory repositories**
- **RESTful API**
- **Layered architecture**
    - Controller
    - Service
    - Domain
    - Repository
    - Configuration

---

## Authentication & Authorization

Authentication is handled via **JWT**.

### Roles
- `USER`
- `DRONE`
- `ADMIN`

Each request (except authentication) must include:

Authorization: Bearer <JWT_TOKEN>


JWT contains:
- `sub` → user/drone name
- `role` → USER / DRONE / ADMIN

---

## Authentication API

### Generate Token

POST /api/v1/auth/token


**Request**
```json
{
  "name": "john",
  "type": "USER"
}
```

Response

```
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

## Order Lifecycle

Order statuses:

    CREATED

    RESERVED

    PICKED_UP

    DELIVERED

    FAILED

    CANCELLED

    HAND_OFF

Rules are strictly enforced in the service layer.
## Drone States

Drone states:

    AVAILABLE

    BUSY

    BROKEN

## Main Features
User

    Create order

    View own order

    Withdraw order

Drone

    Reserve order

    Grab order

    Deliver or fail order

    Send heartbeat (location update)

    Mark itself as broken

Admin

    View all drones

    View all orders

    Mark drone broken

    Fix drone

    Update order origin/destination

    Add new drone (not requested in the assessment)

## Running the Application

mvn clean install
mvn spring-boot:run

Application runs on:

http://localhost:8080

## Notes

    Data is stored in memory (no database)

    Restarting the app resets all data

    Designed for clarity and correctness over persistence


---

## System Endpoints

### Authentication API

| Method | Endpoint | Description | Role | Request Body | Response |
|--------|----------|-------------|------|--------------|----------|
| POST | /api/v1/auth/token | Generate JWT token | Public | `{ "name": "string", "type": "USER \| DRONE \| ADMIN" }` | `{ "token": "string" }` |

---

### Orders API

| Method | Endpoint | Description | Role | Request Body | Response |
|--------|----------|-------------|------|--------------|----------|
| POST | /api/v1/orders | Create a new order | USER | `{ "description": "string", "origin": { "latitude": 10, "longitude": 20 }, "destination": { "latitude": 30, "longitude": 40 } }` | 201 CREATED |
| GET | /api/v1/orders/{order-id} | Get own order details | USER | — | 200 OK |
| DELETE | /api/v1/orders/{order-id} | Withdraw an order (only if CREATED or RESERVED) | USER | — | 200 OK |
| GET | /api/v1/orders/admin | Get all orders | ADMIN | — | 200 OK |
| PUT | /api/v1/orders/admin/{order-id} | Update order origin and destination | ADMIN | `{ "origin": { "latitude": 10, "longitude": 20 }, "destination": { "latitude": 30, "longitude": 40 } }` | 200 OK |

---

### Drone API

| Method | Endpoint | Description                | Role | Request Body                          | Notes |
|--------|----------|----------------------------|------|---------------------------------------|-------|
| PUT    | /api/v1/drones/jobs/{order-id}/reserve | Reserve an available order | DRONE | —                                     | — |
| PUT    | /api/v1/drones/jobs/{order-id}/grab | Pick up a reserved order   | DRONE | —                                     | — |
| PUT    | /api/v1/drones/jobs/{order-id}/deliver | Deliver an order           | DRONE | —                                     | — |
| PUT    | /api/v1/drones/jobs/{order-id}/fail | Fail an order              | DRONE | —                                     | — |
| PUT    | /api/v1/drones/heartbeat | Send drone location update | DRONE | `{ "latitude": 12, "longitude": 34 }` | — |
| GET    | /api/v1/drones/order | Get current assigned order | DRONE | —                                     | — |
| PUT    | /api/v1/drones/broken | Mark drone as broken       | DRONE | —                                     | If carrying an order, a handoff order is created automatically |
| GET    | /api/v1/drones/admin | Get all drones             | ADMIN | —                                     | — |
| PUT    | /api/v1/drones/{drone-name}/admin/broken | Force drone to BROKEN state | ADMIN | —                                     | — |
| PUT    | /api/v1/drones/{drone-name}/admin/fix | Fix a broken drone         | ADMIN | —                                     | — |
| POST   | /api/v1/drones/admin | Add new drone              | ADMIN | `{ "droneName": "drone-1" }`          | — |

