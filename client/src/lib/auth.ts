import Keycloak from 'keycloak-js'
import type { Middleware } from 'openapi-fetch'

import i18n from '../i18n'

/**
 * Keycloak instance configuration.
 * - Initializes the Keycloak client with environment variables.
 */
export const keycloak = new Keycloak({
  url: import.meta.env.VITE_KEYCLOAK_URL || 'http://localhost:8180/auth',
  realm: import.meta.env.VITE_KEYCLOAK_REALM || 'is-stag',
  clientId: import.meta.env.VITE_KEYCLOAK_CLIENT_ID || 'React-client',
})

/**
 * Middleware to handle authentication and localization for all API requests.
 * - Automatically attaches the Keycloak Bearer token.
 * - Refreshes the token if it's about to expire (within 5 seconds).
 * - Sets the Accept-Language header based on the current i18n language.
 */
export const authMiddleware: Middleware = {
  async onRequest({ request }) {
    // Authorization
    if (keycloak.token) {
      // Check if the token is valid for at least 5 more seconds.
      // If not, try to update it to ensure the request doesn't fail due to an expired token.
      if (keycloak.isTokenExpired(5)) {
        try {
          await keycloak.updateToken(5)
        } catch (error) {
          console.error('Failed to refresh token', error)
        }
      }
      request.headers.set('Authorization', `Bearer ${keycloak.token}`)
    }

    // Localization (only sends the primary language code (e.g., "cs" or "en"))
    const primaryLanguage = i18n.language.split('-')[0]
    request.headers.set('Accept-Language', primaryLanguage)

    return request
  },
}
