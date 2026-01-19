import Keycloak from 'keycloak-js'

/**
 * Keycloak instance configuration.
 * - Initializes the Keycloak client with environment variables.
 */
export const keycloak = new Keycloak({
  url: import.meta.env.VITE_KEYCLOAK_URL || 'http://localhost:8180/auth',
  realm: import.meta.env.VITE_KEYCLOAK_REALM || 'is-stag',
  clientId: import.meta.env.VITE_KEYCLOAK_CLIENT_ID || 'React-client',
})
