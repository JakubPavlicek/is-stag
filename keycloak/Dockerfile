FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /usr/src/app

COPY providers/is-stag-authenticator/pom.xml ./
RUN mvn dependency:go-offline

COPY providers/is-stag-authenticator/src ./src/

# Build the provider JAR
RUN mvn package

FROM quay.io/keycloak/keycloak:26.5.3

# Copy the custom provider JAR from the builder stage
COPY --from=builder /usr/src/app/target/is-stag-authenticator.jar /opt/keycloak/providers/

# Copy the custom theme
COPY theme/is-stag-theme /opt/keycloak/themes/is-stag-theme/

# Copy the realm configuration for auto-import on startup.
COPY is-stag-realm-export.json /opt/keycloak/data/import/is-stag-realm.json
