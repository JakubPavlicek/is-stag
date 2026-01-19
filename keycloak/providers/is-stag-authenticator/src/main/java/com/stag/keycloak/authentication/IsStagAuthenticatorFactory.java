package com.stag.keycloak.authentication;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.Collections;
import java.util.List;

/**
 * Factory class for creating {@link IsStagAuthenticator} instances.
 * <p>
 * This factory registers the IS/STAG Authenticator in Keycloak, defining its ID,
 * display name, help text, and configuration capabilities.
 * It provides a singleton instance of the authenticator.
 * </p>
 *
 * @author Jakub Pavlíček
 * @version 1.0.0
 */
public class IsStagAuthenticatorFactory implements AuthenticatorFactory {

    public static final String PROVIDER_ID = "is-stag-authenticator";
    private static final IsStagAuthenticator SINGLETON = new IsStagAuthenticator();

    /**
     * returns the unique identifier for this provider.
     *
     * @return The provider ID ("is-stag-authenticator").
     */
    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    /**
     * Returns the display name of the authenticator in the Keycloak admin console.
     *
     * @return The display name ("IS/STAG Authenticator").
     */
    @Override
    public String getDisplayType() {
        return "IS/STAG Authenticator";
    }

    /**
     * Returns the category of reference for this authenticator.
     *
     * @return {@code null} as no specific category is defined.
     */
    @Override
    public String getReferenceCategory() {
        return null;
    }

    /**
     * Returns the help text displayed in the Keycloak admin console.
     *
     * @return The help text string.
     */
    @Override
    public String getHelpText() {
        return "Authenticates users using the IS/STAG system.";
    }

    /**
     * Creates or returns an {@link Authenticator} instance.
     * <p>
     * Returns the singleton instance of {@link IsStagAuthenticator}.
     * </p>
     *
     * @param session The {@link KeycloakSession} (not used as singleton is returned).
     * @return The singleton {@link IsStagAuthenticator} instance.
     */
    @Override
    public Authenticator create(KeycloakSession session) {
        return SINGLETON;
    }

    /**
     * Indicates if the authenticator is configurable.
     *
     * @return {@code false} as this authenticator does not support configuration.
     */
    @Override
    public boolean isConfigurable() {
        return false;
    }

    /**
     * Returns the requirement choices for this authenticator in the authentication flow.
     *
     * @return An array of {@link AuthenticationExecutionModel.Requirement} choices.
     */
    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    /**
     * Indicates if the user is allowed to set up this authenticator.
     *
     * @return {@code false} as user setup is not allowed.
     */
    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    /**
     * Returns the configuration properties for this authenticator.
     *
     * @return An empty list since the authenticator is not configurable.
     */
    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return Collections.emptyList();
    }

    /**
     * Initializes the factory with the given configuration scope.
     * <p>
     * No operation is performed during initialization.
     * </p>
     *
     * @param config The configuration scope.
     */
    @Override
    public void init(Config.Scope config) {
        // NO-OP
    }

    /**
     * Performs post-initialization tasks.
     * <p>
     * No operation is performed during post-initialization.
     * </p>
     *
     * @param factory The {@link KeycloakSessionFactory}.
     */
    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // NO-OP
    }

    /**
     * Closes the factory and releases resources.
     * <p>
     * No operation is performed during close.
     * </p>
     */
    @Override
    public void close() {
        // NO-OP
    }

}
