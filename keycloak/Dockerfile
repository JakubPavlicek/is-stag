# Start from the official Keycloak image
FROM quay.io/keycloak/keycloak:26.3.2

# Copy provider JAR into the providers directory in the image
COPY ./providers/is-stag-authenticator/target/is-stag-authenticator.jar /opt/keycloak/providers/

# Copy theme files into the themes directory in the image
COPY ./theme/is-stag-theme /opt/keycloak/themes/is-stag-theme