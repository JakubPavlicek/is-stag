# IS/STAG Cloud Reference Architecture

[![Artifact Hub](https://img.shields.io/endpoint?url=https://artifacthub.io/badge/repository/is-stag)](https://artifacthub.io/packages/search?repo=is-stag)

A modern, cloud-native reference architecture for a University Information System (IS/STAG).
This project demonstrates a scalable microservices architecture using Spring Boot, React, and Kubernetes, complete with
full observability.

## Architecture

The application is split into a frontend, an API gateway, domain microservices, identity management, shared gRPC
contracts, and platform infrastructure.

![IS/STAG architecture](images/is-stag-architecture.png)

The Kubernetes deployment uses Helm charts and Helmfile to install infrastructure, observability, Keycloak, the frontend,
and the backend services.

![Kubernetes architecture](images/is-stag-kubernetes-architecture.png)

## Technology Stack

| Area          | Technologies                                                                           |
|---------------|----------------------------------------------------------------------------------------|
| Backend       | Java 25, Spring Boot 4.0.6, Spring Cloud 2025.1.1, Spring Data JPA, gRPC, Resilience4j |
| Frontend      | React 19, TypeScript, Vite, Tailwind CSS, shadcn/ui, TanStack Router, TanStack Query   |
| Identity      | Keycloak with a custom realm, theme, and authenticator provider                        |
| Data          | Oracle Database, Redis cache, Redis rate limiter                                       |
| Observability | OpenTelemetry Collector, Prometheus, Grafana, Loki, Tempo                              |
| Runtime       | Docker Compose for local startup, Kubernetes for cluster deployment                    |
| Deployment    | Helm, Helmfile, SOPS, Age, helm-secrets, GitHub Actions                                |

## Repository Structure

```text
.
├── docker-compose.yaml            # Local Docker Compose stack
├── pom.xml                        # Root Maven aggregator for services and Keycloak provider
├── charts/                        # Reusable Helm charts published for the project
│   ├── backend-service/            # Generic backend service chart
│   ├── cert-manager-config/        # Cert-manager issuer and certificate configuration
│   ├── client/                     # React client chart
│   ├── gateway-config/             # Gateway API routes and listeners
│   ├── metallb-config/             # MetalLB address pool configuration
│   ├── otel-config/                # OpenTelemetry collector and instrumentation chart
│   └── secrets/                    # Secret template chart
├── client/                        # React frontend application
├── images/                        # Architecture diagrams and UI screenshots
├── k6/                            # Load tests in k6
├── k8s/                           # Helmfile environments and values
│   ├── cert-manager/               # Cert-manager Helm values
│   ├── cert-manager-config/        # Cert-manager issuer and certificate configuration
│   ├── database/                   # Redis and Keycloak PostgreSQL values
│   ├── gateway-config/             # Gateway API routes
│   ├── is-stag/                    # Application values for services and client
│   ├── keycloak/                   # Keycloak Helm values
│   ├── metallb/                    # MetalLB Helm values
│   ├── metallb-config/             # MetalLB address pool configuration
│   ├── nginx-gateway-fabric/       # Nginx Gateway Fabric Helm values
│   ├── observability/              # Prometheus, Grafana, Loki, Tempo, OpenTelemetry values
│   ├── otel-config/                # OpenTelemetry collector and instrumentation configuration
│   ├── secrets/                    # SOPS-encrypted environment secrets
│   └── helmfile.yaml               # Main Kubernetes deployment entry point
├── keycloak/                      # Keycloak Docker build, realm exports, theme, and provider
├── observability/                 # Local Docker Compose observability configuration
├── otel/                          # Local OpenTelemetry Java agent mount point
├── proto/                         # Shared gRPC protobuf contracts
└── services/                      # Java Spring Boot backend microservices
    ├── api-gateway/               # Spring Cloud Gateway application
    ├── codelist-service/          # Shared dictionaries and address/codelist domains
    ├── student-service/           # Student domain service
    ├── study-plan-service/        # Study plan and study program domain service
    └── user-service/              # Person and user profile domain service
```

## Prerequisites

Install these tools before running the project locally:

| Tool                            | Purpose                                               |
|---------------------------------|-------------------------------------------------------|
| Docker Desktop or Docker Engine | Local container runtime and Docker Compose            |
| Java 25                         | Backend services                                      |
| Maven 3.9.11 or newer           | Backend and Keycloak provider builds, or use `./mvnw` |
| Node.js 25.8.2 or newer         | React client development                              |
| npm 11.12.1 or newer            | React client package manager                          |
| kubectl                         | Kubernetes access                                     |
| Helm                            | Kubernetes package manager                            |
| Helmfile                        | Kubernetes release orchestration                      |
| helm-secrets, SOPS, Age         | Decrypting Kubernetes secrets                         |

Docker Compose and the backend services also require access to an Oracle Database. The Oracle instance is not started by
`docker-compose.yaml`; provide a reachable JDBC URL through `.env`.

## Start With Docker Compose

Docker Compose is the fastest way to start the full local stack. It starts Keycloak, Redis, the React client, all backend
services, and the observability stack. Frontend and backend images are pulled from GitHub Container Registry by default.

1. Clone the repository:

```shell
git clone https://github.com/JakubPavlicek/is-stag.git
cd is-stag
```

2. Make sure the OpenTelemetry Java agent exists at `otel/opentelemetry-javaagent.jar`.

Download it from the OpenTelemetry Java instrumentation releases if the `.jar` file is missing:
https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases

3. Create `.env` in the repository root.

```text
SPRING_DATASOURCE_URL=<JDBC_CONNECTION_STRING>
SPRING_DATASOURCE_USERNAME=<DB_USERNAME>
SPRING_DATASOURCE_PASSWORD=<DB_PASSWORD>

DATASOURCE_PROXY_USERNAME=<DB_PROXY_USERNAME>
DATASOURCE_PROXY_PASSWORD=<DB_PROXY_PASSWORD>
DATASOURCE_TARGET_USERNAME=<DB_TARGET_USERNAME>

REDIS_PASSWORD=<REDIS_PASSWORD>
```

4. Start the stack:

```shell
docker compose up -d
```

5. Open the application:

| Service                      | URL                                              |
|------------------------------|--------------------------------------------------|
| React client                 | http://localhost:5173                            |
| API gateway                  | http://localhost:8100                            |
| Codelist service             | http://localhost:8010                            |
| Student service              | http://localhost:8020                            |
| Study plan service           | http://localhost:8030                            |
| User service                 | http://localhost:8040                            |
| Aggregated Swagger UI        | http://localhost:8100/api/swagger-ui.html        |
| Keycloak IS/STAG realm admin | http://localhost:8180/auth/admin/is-stag/console |
| Grafana                      | http://localhost:3000                            |
| Prometheus                   | http://localhost:9090                            |

Default Docker Compose credentials:

| Component              | Username               | Password    |
|------------------------|------------------------|-------------|
| Keycloak master realm  | `admin`                | `admin`     |
| Keycloak IS/STAG realm | `stagadmin`            | `stagadmin` |
| Grafana                | anonymous admin access | no password |

7. Stop the stack:

```shell
docker compose down
```

Use this only when you also want to remove local Compose volumes:

```shell
docker compose down -v
```

## Start On Kubernetes

Kubernetes deployment is managed from `k8s/helmfile.yaml`.

The Helmfile defines two environments:

| Environment | Namespace          | Notes                                                    |
|-------------|--------------------|----------------------------------------------------------|
| `prod`      | `is-stag-prod`     | Full application, including Keycloak and React client    |
| `perftest`  | `is-stag-perftest` | Performance-test setup with Keycloak and client disabled |

1. Connect `kubectl` to the target cluster.

For a local cluster, u can use Minikube:
```shell
minikube start
```

2. Install the Helm plugins and tools required for encrypted secrets:

```shell
helm plugin install https://github.com/jkroepke/helm-secrets
```

Install SOPS and Age using your system package manager if they are not already available.

3. Export the Age key path used by SOPS:

```shell
export SOPS_AGE_KEY_FILE=$HOME/.config/sops/age/keys.txt
```

4. Deploy the stack:

```shell
cd k8s
helmfile sync -e prod
```

For performance testing, use:

```shell
helmfile sync -e perftest
```

5. Check the rollout:

```shell
kubectl get pods -n is-stag-prod
kubectl get svc -n is-stag-prod
```

6. Configure local hostnames in `/etc/hosts`:

```text
<external-ip> is-stag.cz
<external-ip> grafana.is-stag.internal
<external-ip> prometheus.is-stag.internal
```

7. Open the application:

| Service                      | URL                                           |
|------------------------------|-----------------------------------------------|
| React client                 | https://is-stag.cz                            |
| API gateway                  | https://is-stag.cz/api                        |
| Aggregated Swagger UI        | https://is-stag.cz/api/swagger-ui.html        |
| Keycloak IS/STAG realm admin | https://is-stag.cz/auth/admin/is-stag/console |
| Grafana                      | https://grafana.is-stag.internal              |
| Prometheus                   | https://prometheus.is-stag.internal           |

Default credentials:

| Component              | Username    | Password    |
|------------------------|-------------|-------------|
| Keycloak master realm  | `admin`     | `admin`     |
| Keycloak IS/STAG realm | `stagadmin` | `stagadmin` |
| Grafana                | `admin`     | `admin`     |

## Project Context

This application is a Master's thesis project for the 2025/2026 academic year at the University of West Bohemia,
Faculty of Applied Sciences. Author: Jakub Pavlíček.
