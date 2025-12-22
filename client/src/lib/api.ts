import createFetchClient, { type Middleware } from 'openapi-fetch'
import createReactQueryClient from 'openapi-react-query'

import type { paths as CodelistPaths } from '@/api/codelist/schema'
import type { paths as StudentPaths } from '@/api/student/schema'
import type { paths as UserPaths } from '@/api/user/schema'

import i18n from '../i18n'
import { keycloak } from './auth'

const authMiddleware: Middleware = {
  async onRequest({ request }) {
    // 1. Authorization
    if (keycloak.token) {
      if (keycloak.isTokenExpired(5)) {
        try {
          await keycloak.updateToken(5)
        } catch (error) {
          console.error('Failed to refresh token', error)
        }
      }
      request.headers.set('Authorization', `Bearer ${keycloak.token}`)
    }

    // 2. Localization
    // Only send the primary language code (e.g., "cs" or "en"), ignoring regions
    const currentLanguage = i18n.language?.split('-')[0] || 'cs'
    request.headers.set('Accept-Language', currentLanguage)

    return request
  },
}

const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8100/api/v1'

export const studentFetch = createFetchClient<StudentPaths>({ baseUrl: `${BASE_URL}` })
studentFetch.use(authMiddleware)
export const $student = createReactQueryClient(studentFetch)

export const userFetch = createFetchClient<UserPaths>({ baseUrl: `${BASE_URL}` })
userFetch.use(authMiddleware)
export const $user = createReactQueryClient(userFetch)

export const codelistFetch = createFetchClient<CodelistPaths>({ baseUrl: `${BASE_URL}` })
codelistFetch.use(authMiddleware)
export const $codelist = createReactQueryClient(codelistFetch)
