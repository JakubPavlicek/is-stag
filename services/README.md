# Backend Microservices

[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=JakubPavlicek_is-stag&metric=coverage)](https://sonarcloud.io/summary/overall?id=JakubPavlicek_is-stag)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=JakubPavlicek_is-stag&metric=alert_status)](https://sonarcloud.io/summary/overall?id=JakubPavlicek_is-stag)
[![Services CI](https://github.com/JakubPavlicek/is-stag/actions/workflows/services-ci.yaml/badge.svg)](https://github.com/JakubPavlicek/is-stag/actions/workflows/services-ci.yaml)
[![JavaDoc](https://github.com/JakubPavlicek/is-stag/actions/workflows/publish-javadoc.yaml/badge.svg)](https://github.com/JakubPavlicek/is-stag/actions/workflows/publish-javadoc.yaml)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.6-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-25-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/)

This directory contains the Java backend for the IS/STAG cloud reference architecture. The backend is implemented as a
Spring Boot multi-module project with a public API gateway, domain services, REST APIs, gRPC communication, Oracle
Database access, Redis integration, and OpenTelemetry instrumentation.

## Services

| Service                                      | HTTP port | gRPC port | Description                                                                                                        |
|----------------------------------------------|-----------|-----------|--------------------------------------------------------------------------------------------------------------------|
| [`api-gateway`](./api-gateway)               | `8100`    | none      | Public entry point for REST traffic, OAuth2 resource server, routing, retries, circuit breakers, and rate limiting |
| [`codelist-service`](./codelist-service)     | `8010`    | `9010`    | Shared dictionaries, domains, countries, address data, and education codelists                                     |
| [`student-service`](./student-service)       | `8020`    | `9020`    | Student data and student-facing REST endpoints                                                                     |
| [`study-plan-service`](./study-plan-service) | `8030`    | `9030`    | Study plans, study programs, field-of-study data, and internal gRPC endpoints                                      |
| [`user-service`](./user-service)             | `8040`    | `9040`    | Persons, user profile data, and identity-related domain data                                                       |

## Technology Stack

| Area                    | Technologies                                                         |
|-------------------------|----------------------------------------------------------------------|
| Language                | Java 25 with preview features enabled                                |
| Framework               | Spring Boot 4.0.6, Spring Cloud 2025.1.1                             |
| APIs                    | REST, OpenAPI, Swagger UI                                            |
| Internal communication  | gRPC with protobuf contracts from `../proto`                         |
| Database                | Oracle Database through Spring Data JPA and Hibernate                |
| Cache and rate limiting | Redis                                                                |
| Security                | Spring Security, OAuth2 Resource Server, Keycloak JWT validation     |
| Resilience              | Resilience4j circuit breakers, retries, time limiters, and bulkheads |
| Observability           | OpenTelemetry Java agent, Spring Boot Actuator                       |
| Containers              | Cloud Native Buildpacks through the Spring Boot Maven plugin         |
| Tests                   | JUnit 6, Spring Boot tests, Mockito, Testcontainers                  |
| Build                   | Maven 3.9.11 or newer                                                |

## Module Structure

```text
services/
├── pom.xml               # Backend Maven aggregator and shared dependency management
├── api-gateway/          # Spring Cloud Gateway application
│   ├── pom.xml
│   └── src/
├── codelist-service/     # Shared dictionaries and address/codelist domains
│   ├── pom.xml
│   └── src/
├── student-service/      # Student domain service
│   ├── pom.xml
│   └── src/
├── study-plan-service/   # Study plan and study program domain service
│   ├── pom.xml
│   └── src/
└── user-service/         # Person and user profile domain service
    ├── pom.xml
    └── src/
```

## Prerequisites

| Tool                   | Purpose                                                     |
|------------------------|-------------------------------------------------------------|
| Java 25                | Compile and run services                                    |
| Maven 3.9.11 or newer  | Build backend modules, or use `../mvnw` from this directory |
| Docker                 | Testcontainers and local Compose dependencies               |
| Oracle Database access | Runtime datasource for domain services                      |
| Redis                  | Cache and API gateway rate limiting                         |
| Keycloak               | OAuth2 JWT issuer for the API gateway                       |

Use the root `docker-compose.yaml` when you want the standard local runtime dependencies and all backend containers to run
together.

## Developing Services

Run commands from the repository root with `./mvnw -f services/pom.xml` or from this directory with `../mvnw`.

Build all backend modules without tests:

```shell
./mvnw -f services/pom.xml package -DskipTests
```

Run all backend tests:

```shell
./mvnw -f services/pom.xml test
```

Run tests for one service:

```shell
./mvnw -f services/pom.xml -pl student-service -am test
```

Run one service from source:

```shell
./mvnw -f services/pom.xml -pl student-service -am spring-boot:run
```

Use `SPRING_PROFILES_ACTIVE=dev` for local development. The Compose containers already set this profile. When running a
service directly from the host, override container-only hostnames where needed. For example, use `localhost` for Redis or
for gRPC peers that are exposed by Docker Compose.

Example for running `student-service` locally while the rest of the stack runs in Docker Compose:

```shell
docker compose up -d redis-cache user-service study-plan-service
docker compose stop student-service

SPRING_PROFILES_ACTIVE=dev \
SPRING_DATA_REDIS_HOST=localhost \
STUDY_PLAN_SERVICE_GRPC_ADDRESS=localhost:9030 \
USER_SERVICE_GRPC_ADDRESS=localhost:9040 \
./mvnw -f services/pom.xml -pl student-service -am spring-boot:run
```

The service still needs the datasource variables from the root `.env` or equivalent shell environment variables.

## Container Images

Services use Cloud Native Buildpacks through the Spring Boot Maven plugin. No service-specific Dockerfiles are required.

Build a local image for one service:

```shell
./mvnw -f services/pom.xml -pl student-service -am spring-boot:build-image -DskipTests
```

Build an image with the same name used by Docker Compose:

```shell
./mvnw -f services/pom.xml -pl student-service -am spring-boot:build-image \
  -DskipTests \
  -Dspring-boot.build-image.imageName=ghcr.io/jakubpavlicek/student-service:0.0.1
```

Then restart the service container:

```shell
docker compose up -d student-service
```

## API Documentation

The API gateway aggregates Swagger UI for the public REST APIs.

| Runtime        | URL                                       |
|----------------|-------------------------------------------|
| Docker Compose | http://localhost:8100/api/swagger-ui.html |
| Kubernetes     | https://is-stag.cz/api/swagger-ui.html    |

OpenAPI source files are stored in each public REST service under `src/main/resources/static/openapi.yaml`. The React
client generates TypeScript schemas from these files with `npm run gen:api` in `../client`.

## gRPC Contracts

Shared protobuf definitions live in `../proto`. Service builds generate Java gRPC code through the protobuf Maven plugin.

When changing a protobuf contract:

1. Update the relevant `.proto` file in `../proto`.
2. Update the producer and consumer service code in this directory.
3. Run affected service tests with `./mvnw -f services/pom.xml -pl <service-name> -am test`.
4. Rebuild affected service images if Docker Compose or Kubernetes should use the change.

## Testing Notes

Unit and integration tests are run with Maven. Integration tests use Testcontainers, so Docker must be running before
executing tests.

Common commands:

```shell
./mvnw -f services/pom.xml test
./mvnw -f services/pom.xml -pl student-service -am test
./mvnw -f services/pom.xml verify
```

JaCoCo coverage reports are generated during the Maven test lifecycle and published by CI.
