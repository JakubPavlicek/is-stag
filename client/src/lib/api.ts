import createFetchClient, { type Middleware } from 'openapi-fetch'
import createReactQueryClient from 'openapi-react-query'

import type { paths as CodelistPaths } from '@/api/codelist/schema'
import type { paths as StudentPaths } from '@/api/student/schema'
import type { paths as UserPaths } from '@/api/user/schema'

import i18n from '../i18n'
import { keycloak } from './auth'

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

const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8100/api/v1'

// --- Student Service ---
const studentFetch = createFetchClient<StudentPaths>({ baseUrl: `${BASE_URL}` })
studentFetch.use(authMiddleware)
export const $student = createReactQueryClient(studentFetch)

// --- User Service ---
const userFetch = createFetchClient<UserPaths>({ baseUrl: `${BASE_URL}` })
userFetch.use(authMiddleware)
export const $user = createReactQueryClient(userFetch)

// --- Codelist Service ---
const codelistFetch = createFetchClient<CodelistPaths>({ baseUrl: `${BASE_URL}` })
codelistFetch.use(authMiddleware)
export const $codelist = createReactQueryClient(codelistFetch)
