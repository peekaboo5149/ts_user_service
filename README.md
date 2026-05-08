# TS User Service

A Spring Boot-based user and profile management service built with an event-driven architecture.

---

## Overview

The **TS User Service** manages user credentials and profiles, persists data in MongoDB, and emits domain events to Kafka for downstream consumers. It serves as the source of truth for user-related information within the blogger platform.

Key capabilities:

* User credential management (create, read, update, delete)
* User profile management with pagination support
* Password validation
* Event emission for user lifecycle changes (`USER_CREATED`, `USER_UPDATED`, `USER_DELETED`)
* Pluggable messaging consumer framework with retry, DLT, and idempotency support

---

## Tech Stack

| Layer                | Technology                                      |
| -------------------- | ----------------------------------------------- |
| Language             | Java 25                                         |
| Framework            | Spring Boot 4.0.6                               |
| Database             | MongoDB 8 (Spring Data MongoDB)                 |
| Messaging            | Apache Kafka 4.2.0                              |
| Observability        | Micrometer, Prometheus, Zipkin (Brave)          |
| Build Tool           | Gradle                                          |
| Container            | Docker (multi-stage build, Eclipse Temurin JRE) |

---

## API Overview

### User Credentials

| Method | Endpoint                                            | Description                  |
| ------ | --------------------------------------------------- | ---------------------------- |
| POST   | `/api/v1/user-credentials/create`                   | Create a new user            |
| GET    | `/api/v1/user-credentials?type={ID\|EMAIL\|USERNAME}&value={value}` | Get user by identifier       |
| POST   | `/api/v1/user-credentials/validate-password`        | Validate user password       |
| PATCH  | `/api/v1/user-credentials/{id}`                     | Update user credentials      |
| DELETE | `/api/v1/user-credentials/{id}`                     | Soft delete a user           |

### User Profiles

| Method | Endpoint                                | Description                        |
| ------ | --------------------------------------- | ---------------------------------- |
| GET    | `/api/v1/user-profiles/{id}`            | Get profile by user ID             |
| PATCH  | `/api/v1/user-profiles/{id}`            | Update user profile                |
| GET    | `/api/v1/user-profiles`                 | Get all profiles (paginated)       |
| GET    | `/api/v1/user-profiles/non-admin`       | Get non-admin profiles (paginated) |

### Actuator Endpoints

| Endpoint                   | Port  | Description            |
| -------------------------- | ----- | ---------------------- |
| `/actuator/health`         | 10103 | Health probes          |
| `/actuator/prometheus`     | 10103 | Prometheus metrics     |
| `/actuator/info`           | 10103 | Application info       |
| `/actuator/db`             | 10103 | Database health        |
| `/actuator/metrics`        | 10103 | JVM and custom metrics |

---

## Environment Variables

| Variable                                      | Description                                   | Default               |
| --------------------------------------------- | --------------------------------------------- | --------------------- |
| `TS_USER_SERVICE_MONGODB_URI`                 | MongoDB connection URI                        | —                     |
| `TS_USER_SERVICE_KAFKA_BOOTSTRAP_SERVERS`     | Kafka bootstrap servers                       | —                     |
| `TS_USER_SERVICE_USER_EVENT_TOPIC_NAME`       | Kafka topic for publishing user events        | —                     |

---

## Running Locally

### Prerequisites

* Java 25
* Docker and Docker Compose
* Gradle (wrapper included)

### 1. Start Infrastructure with Docker Compose

The project includes a `docker-compose.yaml` that starts MongoDB and Kafka:

```bash
docker compose up -d
```

This starts:

* MongoDB on `localhost:27017`
* Kafka on `localhost:9092`

Verify services are healthy:

```bash
docker compose ps
```

To stop the infrastructure:

```bash
docker compose down
```

To stop and remove volumes:

```bash
docker compose down -v
```

### 2. Run the Application with Gradle

Set the required environment variables and start the application with the `local` profile:

```bash
export TS_USER_SERVICE_MONGODB_URI="mongodb://user:pass@localhost:27017/user_db?authSource=admin"
export TS_USER_SERVICE_KAFKA_BOOTSTRAP_SERVERS="localhost:9092"
export TS_USER_SERVICE_USER_EVENT_TOPIC_NAME="user-event-topic"

./gradlew bootRun --args='--spring.profiles.active=local'
```

Or run the JAR directly after building:

```bash
./gradlew bootJar -x test
java -jar build/libs/*.jar --spring.profiles.active=local
```

The application will be available at:

* API: `http://localhost:8080`
* Actuator: `http://localhost:10103`

---

## Docker

### Build the Image

```bash
docker build -t ts-user-service:latest .
```

### Run the Container

```bash
docker run -d \
  --name ts-user-service \
  -p 8080:8080 \
  -p 10103:10103 \
  -e TS_USER_SERVICE_MONGODB_URI="mongodb://user:pass@host.docker.internal:27017/user_db?authSource=admin" \
  -e TS_USER_SERVICE_KAFKA_BOOTSTRAP_SERVERS="host.docker.internal:9092" \
  -e TS_USER_SERVICE_USER_EVENT_TOPIC_NAME="user-event-topic" \
  ts-user-service:latest
```

### View Logs

```bash
docker logs -f ts-user-service
```

### Stop and Remove

```bash
docker stop ts-user-service && docker rm ts-user-service
```

---

## Testing with Postman

A Postman collection is included at `ts-user-service-postman-collection.json`. Import it into Postman and create an environment with:

| Variable | Value                   |
| -------- | ----------------------- |
| `baseUrl`  | `http://localhost:8080` |

The collection is fully automated:

* The **Create User** request generates random credentials and stores the returned `id`, `username`, `email`, and `password` in environment variables.
* All downstream requests (Get, Validate Password, Update, Delete, Profile) automatically reuse those variables.
* No manual copy-pasting is required.

### Collection Structure

* **User Credentials** — Create, Get by ID/Email/Username, Validate Password, Update, Delete
* **User Profiles** — Get Profile, Update Profile
* **Batch / Paginated Operations** — Get All Profiles, Get Non-Admin Profiles (with pagination and sorting)

---

## Architecture Notes

* **Event-driven** — Domain events are published to Kafka after successful MongoDB transaction commit.
* **Transactional outbox pattern** — Events are published via Spring's `TransactionalEventListener` to ensure atomicity.
* **Observability** — Metrics are exposed via Micrometer/Prometheus; distributed tracing is supported via Brave/Zipkin.
* **Security** — Non-root Docker user; container-aware JVM tuning; actuator health probes for orchestrators.

---

## Project Structure

```
.
├── src/main/java/org/bloggers/ts_users/
│   ├── annotations/          # Custom annotations (e.g., HideResponse)
│   ├── config/               # Application configuration
│   ├── controllers/          # REST API controllers
│   ├── dto/                  # Request/response DTOs and events
│   ├── entities/             # MongoDB document entities
│   ├── exceptions/           # Global exception handling
│   ├── factories/            # Strategy factories
│   ├── listener/             # Event publishers and message listeners
│   ├── repositories/         # Spring Data MongoDB repositories
│   ├── service/              # Business logic services
│   ├── strategy/             # Identifier resolution strategies
│   ├── utils/                # Utility classes
│   └── validation/           # Custom validators
├── src/main/resources/
│   ├── application.yaml       # Base configuration
│   ├── application-local.yaml # Local development profile
│   └── logback.xml            # Logging configuration
├── Dockerfile                 # Multi-stage Docker build
├── docker-compose.yaml        # Local infrastructure (MongoDB + Kafka)
└── ts-user-service-postman-collection.json
```

---

## License

This project is licensed under the terms of the [LICENSE](LICENSE) file.
