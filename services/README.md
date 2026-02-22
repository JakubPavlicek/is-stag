# Backend Microservices

[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=JakubPavlicek_is-stag&metric=coverage)](https://sonarcloud.io/summary/overall?id=JakubPavlicek_is-stag)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=JakubPavlicek_is-stag&metric=alert_status)](https://sonarcloud.io/summary/overall?id=JakubPavlicek_is-stag)
[![Backend CI](https://github.com/JakubPavlicek/is-stag/actions/workflows/backend-ci.yaml/badge.svg)](https://github.com/JakubPavlicek/is-stag/actions/workflows/backend-ci.yaml)
[![JavaDoc](https://github.com/JakubPavlicek/is-stag/actions/workflows/publish-javadoc.yaml/badge.svg)](https://github.com/JakubPavlicek/is-stag/actions/workflows/publish-javadoc.yaml)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.3-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-25-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/)

The backend of IS/STAG is built as a distributed microservices architecture. It leverages the power of Spring Boot 4 and
Java 25 to provide high performance, scalability, and modern language features (like virtual threads).

---

## 🧩 Services Overview

| Service                                        | Port   | Description                                                                                |
|:-----------------------------------------------|:-------|:-------------------------------------------------------------------------------------------|
| **[api-gateway](./api-gateway)**               | `8100` | Entry point for all external traffic. Handles routing, auth validation, and rate limiting. |
| **[codelist-service](./codelist-service)**     | `8010` | Manages static and semi-static dictionaries (Countries, Municipalities, etc.).             |
| **[user-service](./user-service)**             | `8020` | Identity and profile management for Persons.                                               |
| **[student-service](./student-service)**       | `8030` | Core domain service for student records and enrollments.                                   |
| **[study-plan-service](./study-plan-service)** | `8050` | Manages curriculums, subjects, and study prerequisites.                                    |

---

## 🛠 Technology Stack

* **Language:** Java 25 (Preview features enabled for Structured Concurrency)
* **Framework:** Spring Boot 4.0.3, Spring Cloud 2025.1.1
* **Communication:**
    * **External:** REST API (OpenAPI 3.0)
    * **Internal:** gRPC (High-performance inter-service communication)
* **Database:** Oracle Database 19c
* **ORM:** Spring Data JPA with Hibernate
* **Caching & Rate Limiting:** Redis
* **Security:** Spring Security with OAuth2 Resource Server (Keycloak)
* **gRPC Framework:** Spring Boot Starter for gRPC ([DanielLiu1123/grpc-starter](https://github.com/DanielLiu1123/grpc-starter))
* **API Documentation:** OpenAPI 3.0 & Swagger UI
* **Resilience:** Resilience4j (Circuit Breaker, Retry, Time Limiter, Bulkhead)
* **Observability:** OpenTelemetry (Tracing, Metrics, Logging)
* **Containerization:** Cloud Native Buildpacks (Paketobuildpacks) via Spring Boot Maven Plugin
* **Testing:** JUnit 5, Mockito, Testcontainers
* **Code Quality:** SonarCloud
* **Build Tool:** Maven 3.9.11+

---

## 🚀 Development Setup

### Prerequisites

* **Java 25**
* **Maven** (or use the provided `mvnw` wrapper)
* **Docker** (required for Testcontainers during tests)

### Build & Run

To build all services:

```shell
mvn clean package -DskipTests
```

To run a specific service (e.g., `student-service`):

```shell
cd student-service
mvn spring-boot:run
```

### Testing

We use **Testcontainers** for integration testing to ensure real-world reliability.

```shell
mvn test
```

---

## 📡 API Documentation

Each service exposes OpenAPI documentation. When running locally via Docker Compose or Kubernetes, the aggregated
documentation is available through the API Gateway.

- If running via Docker Compose: http://localhost:8100/api/swagger-ui/index.html
- If running via Kubernetes: https://is-stag.cz/api/swagger-ui/index.html

---

## 🐋 Docker & Containers

All services are containerized using **Cloud Native Buildpacks** (Paketobuildpacks) via the Maven plugin. This ensures secure,
efficient, and layered images without writing manual `Dockerfile`s.

```bash
# Build image for a specific module
mvn spring-boot:build-image -pl <module-name> -DskipTests
```
