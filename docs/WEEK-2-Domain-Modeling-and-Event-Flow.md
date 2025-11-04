# Week 2 — Domain Modeling, Microservices Architecture & Database Design

## Weekly Goal
By the end of Week 2, you’ll be able to:
- Design the Ride Booking core service (the heart of Uber).
- Model entities and relationships in a microservices-based architecture.
- Understand how services talk to each other via REST + Kafka.
- Implement database schemas, data flow, and service boundaries.
- Think like a backend system designer, not just a coder.

---

## Architecture Diagram (Ride Booking Module)
```mermaid
flowchart LR
    subgraph Client["🧍 Rider Mobile App"]
        A1["Request Ride API Call"]
    end

    subgraph Gateway["🌐 API Gateway"]
        A2["/ride/request"]
    end

    subgraph BookingService["🚕 Ride Booking Service"]
        A3["Booking Controller"]
        A4["Booking Service Layer"]
        A5["Trip Repository (ORM)"]
    end

    subgraph DB["🗄️ Booking DB"]
        A6["Trips Table"]
        A7["Riders Table"]
        A8["Drivers Table"]
    end

    subgraph Kafka["🔁 Kafka Event Bus"]
        A9["Topic: trip_created"]
        A10["Topic: driver_assigned"]
    end

    subgraph FutureServices["🔧 Other Microservices"]
        A11["Driver Service"]
        A12["Payment Service"]
        A13["Notification Service"]
    end

    %% Flows
    A1 --> A2 --> A3 --> A4 --> A5 --> A6
    A4 --> A9
    A9 --> A11
    A9 --> A12
    A9 --> A13
    A11 --> A10 --> A4
    A4 --> A5 --> A6
```

---

## Database ER Diagram — Ride Booking Core
```mermaid
erDiagram
    RIDER {
        int rider_id PK
        string name
        string phone_number
        float rating
    }

    DRIVER {
        int driver_id PK
        string name
        string vehicle_no
        string status  // AVAILABLE, BUSY, OFFLINE
        float rating
        float current_lat
        float current_long
    }

    TRIP {
        int trip_id PK
        int rider_id FK
        int driver_id FK
        string status  // REQUESTED, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED
        string pickup_location
        string dropoff_location
        float fare_estimate
        timestamp created_at
        timestamp updated_at
    }

    PAYMENT {
        int payment_id PK
        int trip_id FK
        float amount
        string status  // INITIATED, SUCCESS, FAILED
        string method  // CARD, UPI, WALLET
        timestamp created_at
    }

    RIDER ||--o{ TRIP : "books"
    DRIVER ||--o{ TRIP : "assigned_to"
    TRIP ||--o{ PAYMENT : "generates"
```

### Why this ER works
- Bounded contexts map to microservices: Booking (TRIP), Driver (DRIVER), Payment (PAYMENT).
- Scales independently; can shard by `trip_id`.
- Trip lifecycle is event-driven (status changes from events).

### Suggested DDL (monolith-phase evolution)
```sql
ALTER TABLE riders
  ADD COLUMN IF NOT EXISTS rating REAL DEFAULT 5.0,
  ADD COLUMN IF NOT EXISTS phone_number VARCHAR(30);

ALTER TABLE drivers
  ADD COLUMN IF NOT EXISTS vehicle_no VARCHAR(20),
  ADD COLUMN IF NOT EXISTS rating REAL DEFAULT 5.0,
  ADD COLUMN IF NOT EXISTS current_lat REAL,
  ADD COLUMN IF NOT EXISTS current_long REAL,
  ALTER COLUMN status SET DEFAULT 'AVAILABLE';

CREATE TABLE IF NOT EXISTS trips (
  trip_id BIGSERIAL PRIMARY KEY,
  rider_id BIGINT NOT NULL REFERENCES riders(id),
  driver_id BIGINT REFERENCES drivers(id),
  status VARCHAR(20) NOT NULL,
  pickup_location VARCHAR(255) NOT NULL,
  dropoff_location VARCHAR(255) NOT NULL,
  fare_estimate REAL,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS payments (
  payment_id BIGSERIAL PRIMARY KEY,
  trip_id BIGINT NOT NULL REFERENCES trips(trip_id),
  amount REAL NOT NULL,
  status VARCHAR(20) NOT NULL,
  method VARCHAR(20) NOT NULL,
  created_at TIMESTAMP NOT NULL
);
```

---

## Kafka Event Flow — Event-Driven Ride Lifecycle
```mermaid
flowchart TD
    subgraph RiderApp["📱 Rider App"]
        A1["Request Ride"]
    end

    subgraph BookingService["🚕 Booking Service"]
        A2["Produce trip_created"]
        A3["Consume driver_assigned"]
        A3b["Produce trip_completed"]
    end

    subgraph DriverService["🚗 Driver Service"]
        A4["Consume trip_created"]
        A5["Produce driver_assigned"]
    end

    subgraph PaymentService["💳 Payment Service"]
        A6["Consume trip_completed"]
        A7["Produce payment_completed"]
    end

    subgraph NotificationService["🔔 Notification Service"]
        A8["Consume trip_created, driver_assigned, trip_completed, payment_completed"]
    end

    subgraph Kafka["🔁 Kafka Broker"]
        T1["topic: trip_created"]
        T2["topic: driver_assigned"]
        T3["topic: trip_completed"]
        T4["topic: payment_completed"]
    end

    RiderApp -->|HTTP POST /requestRide| BookingService
    BookingService -->|publish| T1
    T1 -->|consume| DriverService
    DriverService -->|publish| T2
    T2 -->|consume| BookingService
    BookingService -->|publish| T3
    T3 -->|consume| PaymentService
    PaymentService -->|publish| T4
    T1 -->|notify events| NotificationService
    T2 -->|notify events| NotificationService
    T3 -->|notify events| NotificationService
    T4 -->|notify events| NotificationService
```

### Event Catalog
- `trip_created`: Producer=Booking; Consumers=Driver, Notification. Starts driver assignment.
- `driver_assigned`: Producer=Driver; Consumers=Booking, Notification. Booking updates trip.
- `trip_completed`: Producer=Booking; Consumers=Payment, Notification. Kicks off billing.
- `payment_completed`: Producer=Payment; Consumers=Notification. Receipt + finalization.

### Example Event Payloads
```json
{ "tripId": 12345, "riderId": 1, "pickupLocation": "Airport", "dropoffLocation": "Downtown", "requestedAt": "2025-11-04T09:30:00Z" }
```
```json
{ "tripId": 12345, "driverId": 888, "vehicleNo": "MH01AB1234", "assignedAt": "2025-11-04T09:31:10Z" }
```
```json
{ "tripId": 12345, "distanceKm": 14.5, "durationMin": 35, "completedAt": "2025-11-04T10:05:12Z" }
```
```json
{ "tripId": 12345, "amount": 325.50, "method": "UPI", "status": "SUCCESS", "paidAt": "2025-11-04T10:06:00Z" }
```

---

## Deep Dive: What/Why/How
- **Event-Driven Design**: decouples services; resilient and scalable. Kafka persists events until consumed.
- **Service Boundaries**: Booking manages trips; Driver manages availability; Payment manages billing.
- **Data Consistency**: eventual consistency across services via events.
- **Transaction Flow**: Booking → Kafka → Driver → Kafka → Booking → Kafka → Payment.

## Expected Outcome
- You can explain the architecture, ER design, and event choreography.
- You can outline a Spring Boot Booking service that persists a trip and publishes `trip_created`.

## Next (Week 3 Preview)
- Add Kafka (Zookeeper + Broker) to Docker Compose.
- Booking service publishes `trip_created` on POST /api/trips.
- Simulated consumer for `driver_assigned`.
- Tests and Postman collection.
