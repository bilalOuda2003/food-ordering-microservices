# Food Ordering System – Microservices (Spring Boot + gRPC)

**Student:** Bilal Kamal Mohamed Ouda  
**ID:** 120212648  
**Course:** SDEV 4304 – Advanced Software Engineering

---

## System Overview

This project implements two collaborating microservices from the Food Ordering System using **Spring Boot** and **gRPC**:

| Service | Role | Port |
|---|---|---|
| `restaurant-service` | gRPC Server – checks meal availability | 9090 |
| `order-service` | REST API + gRPC Client – creates orders | 8080 |

### Communication Flow
```
Customer App
     │
     │  POST /api/v1/orders  (REST/JSON)
     ▼
Order Service (port 8080)
     │
     │  CheckAvailability (gRPC/Protobuf)
     ▼
Restaurant Service (port 9090)
     │
     └──► Returns availability + prices
```

---

## How to Run

### Prerequisites
- Java 17+
- Maven 3.8+

### Step 1 – Start Restaurant Service
```bash
cd restaurant-service
mvn spring-boot:run
```

### Step 2 – Start Order Service (new terminal)
```bash
cd order-service
mvn spring-boot:run
```

### Step 3 – Test the API
```bash
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "C-1001",
    "restaurantId": "R-202",
    "deliveryAddress": {
      "street": "Al-Nasser St",
      "city": "Gaza",
      "postalCode": "00970"
    },
    "items": [
      { "mealId": "M-501", "name": "Chicken Shawarma", "quantity": 2 },
      { "mealId": "M-303", "name": "Falafel Wrap", "quantity": 1 }
    ],
    "paymentMethod": "CREDIT_CARD"
  }'
```

---

## Project Structure
```
food-ordering/
├── restaurant-service/
│   ├── src/main/proto/restaurant.proto       ← gRPC schema
│   └── src/main/java/com/foodordering/restaurant/
│       ├── grpc/RestaurantGrpcService.java   ← gRPC Server impl
│       ├── model/Meal.java
│       └── service/MealService.java
└── order-service/
    ├── src/main/proto/restaurant.proto       ← same proto (shared)
    └── src/main/java/com/foodordering/order/
        ├── controller/OrderController.java   ← REST endpoint
        ├── client/RestaurantGrpcClient.java  ← gRPC Client
        ├── service/OrderService.java
        ├── dto/                              ← Request/Response DTOs
        └── model/Order.java
```
