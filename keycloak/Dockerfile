FROM maven:3.9.14-eclipse-temurin-21-alpine AS builder
WORKDIR /usr/src/app

# Copy the custom provider POM file and download dependencies
COPY providers/is-stag-authenticator/pom.xml ./
RUN mvn --no-transfer-progress dependency:go-offline

# Copy the provider code and build the custom provider JAR
COPY providers/is-stag-authenticator/src ./src/
RUN mvn --no-transfer-progress package -DskipTests

FROM quay.io/keycloak/keycloak:26.5.7

# Build argument for realm file (defaults to production realm)
ARG REALM_FILE=is-stag-realm-export-prod.json

# Copy the custom provider JAR, theme, and realm configuration for auto-import on startup
COPY --from=builder /usr/src/app/target/*.jar /opt/keycloak/providers/
COPY theme/is-stag-theme /opt/keycloak/themes/is-stag-theme/
COPY ${REALM_FILE} /opt/keycloak/data/import/is-stag-realm.json

RUN /opt/keycloak/bin/kc.sh build