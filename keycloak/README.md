# Identity & Access Management (Keycloak)

[![Keycloak CI](https://github.com/JakubPavlicek/is-stag/actions/workflows/keycloak-ci.yaml/badge.svg)](https://github.com/JakubPavlicek/is-stag/actions/workflows/keycloak-ci.yaml)
[![Keycloak](https://img.shields.io/badge/Keycloak-26.5.1-blue?logo=keycloak&logoColor=white)](https://www.keycloak.org/)
[![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white)](https://www.docker.com/)

This module provides the Identity and Access Management (IAM) layer for the IS/STAG platform, built on top of **Keycloak**.
It manages user authentication, authorization (OIDC/OAuth2), and federation.

It includes a custom build of Keycloak with specific providers and themes tailored for the university portal.

---

## 🔐 Features

* **Custom Realm Configuration:** Pre-configured `is-stag` realm with roles, clients, and scopes.
* **Custom Theme:** A branded login theme (`is-stag-theme`) with IS/STAG login button.
* **Custom Authenticator:** A specialized Java provider (`is-stag-authenticator`) extending Keycloak's core authentication flows to enable IS/STAG login.
* **Auto-Import:** Automatically initializes the realm state on startup.

---

## 📂 Structure

```text
keycloak/
├── Dockerfile                      # Multi-stage build (Maven builder + Keycloak runtime)
├── is-stag-realm-export.json       # Production realm configuration export
├── is-stag-realm-export-dev.json   # Development realm configuration export
├── providers/                      # Custom Java Service Provider Interfaces (SPIs)
│   └── is-stag-authenticator/      # Custom authentication logic source code
└── theme/                          # Custom UI themes
    └── is-stag-theme/              # HTML/FTL templates and CSS for login pages
```

---

## 🚀 Getting Started

### Prerequisites

* **Java 21**
* **Maven**
* **Docker**

### Running Locally

The easiest way to run Keycloak is via the project's root `docker-compose.yaml`, but you can also run it standalone:

```bash
docker build -t is-stag-keycloak .
docker run -p 8180:8080 -e KC_BOOTSTRAP_ADMIN_USERNAME=admin -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin is-stag-keycloak start-dev --import-realm
```

Access the IS/STAG Console: [http://localhost:8180/is-stag](http://localhost:8180/admin)  
(Credentials: `stagadmin`/`stagadmin`)

Access the Admin Console: [http://localhost:8180/admin](http://localhost:8180/admin)  
(Credentials: `admin`/`admin`)

---

## 🛠 Development

### Custom Authenticator

The `providers/is-stag-authenticator` directory contains a Maven project for a custom authentication execution.

To build it manually:

```bash
cd providers/is-stag-authenticator
mvn clean package
```

The resulting JAR is automatically copied into the `providers/` directory of the Keycloak container during the Docker
build.

### Custom Theme

The theme is located in `theme/is-stag-theme`.
Any changes to the Freemarker templates (`.ftl`) or CSS require a container rebuild or volume mount to take effect.
