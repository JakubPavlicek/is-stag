import Keycloak from 'keycloak-js'

/**
 * Keycloak instance configuration.
 * - Initializes the Keycloak client with environment variables.
 */
export const keycloak = new Keycloak({
  url: import.meta.env.VITE_KEYCLOAK_URL,
  realm: import.meta.env.VITE_KEYCLOAK_REALM,
  clientId: import.meta.env.VITE_KEYCLOAK_CLIENT_ID || 'React-client',
})
