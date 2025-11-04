# uber-backend-system

A scalable backend simulation of Uber that supports ride booking, driver discovery, and trip tracking — starting as a monolith in Week 0 and evolving to microservices.

## Getting Started (Week 0)

- Start PostgreSQL via Docker Compose:
  ```bash
  docker compose up -d
  ```
- Run the Spring Boot app (requires Java 17+ and Maven):
  ```bash
  mvn spring-boot:run
  ```
- App should start on `http://localhost:8080` and connect to PostgreSQL.

## Project Structure

```
uber-backend-system/
├─ src/main/java/com/uber/backend/UberBackendApplication.java
├─ src/main/resources/application.yml
├─ pom.xml
├─ docker-compose.yml
├─ README.md
├─ CONTRIBUTING.md
└─ .github/
   ├─ ISSUE_TEMPLATE.md
   └─ PULL_REQUEST_TEMPLATE.md
```

## Branching Strategy

- `main`: production-ready
- `dev`: integration branch
- `week-00-setup`: initial Week 0 setup
- Feature branches: `feature/<topic>` or `week-00-task-<name>`

## Prerequisites

- Java 17+
- Maven 3.9+
- Docker Desktop

## Common Commands

- Build: `mvn clean package`
- Run: `mvn spring-boot:run`
- Start DB: `docker compose up -d`
- Stop DB: `docker compose down`

## Next Weeks

- Week 1: Ride Booking Module (controllers, models, repositories, Flyway)

## Documentation

- Week 2: Domain Modeling, ERD, and Kafka Event Flow
  - See docs/WEEK-2-Domain-Modeling-and-Event-Flow.md
