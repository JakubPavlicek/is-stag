FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /usr/src/app

COPY providers/is-stag-authenticator/pom.xml ./
RUN mvn --no-transfer-progress dependency:go-offline

COPY providers/is-stag-authenticator/src ./src/

# Build the provider JAR
RUN mvn --no-transfer-progress package -DskipTests

FROM quay.io/keycloak/keycloak:26.5.5

# Build argument for realm file (defaults to prod)
ARG REALM_FILE=is-stag-realm-export-prod.json

# Copy the custom provider JAR from the builder stage
COPY --from=builder /usr/src/app/target/*.jar /opt/keycloak/providers/

# Copy the custom theme
COPY theme/is-stag-theme /opt/keycloak/themes/is-stag-theme/

# Copy the realm configuration for auto-import on startup.
COPY ${REALM_FILE} /opt/keycloak/data/import/is-stag-realm.json

RUN /opt/keycloak/bin/kc.sh build