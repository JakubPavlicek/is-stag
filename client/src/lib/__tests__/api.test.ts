import { describe, expect, it, vi } from 'vitest'

import i18n from '../../i18n'
import { authMiddleware, keycloak } from '../auth'

vi.mock('keycloak-js', () => {
  const KeycloakMock = vi.fn(function () {
    return {
      token: null,
      isTokenExpired: vi.fn(),
      updateToken: vi.fn(),
    }
  })
  return { default: KeycloakMock }
})

vi.mock('../../i18n', () => ({
  default: { language: 'en' },
}))

// helper to reduce boilerplate
const callMiddleware = (request: Request) =>
  authMiddleware.onRequest!({
    request,
    options: {} as any,
    schemaPath: '',
    params: { query: undefined, header: undefined, path: undefined, cookie: undefined },
    id: '',
  })

describe('authMiddleware', () => {
  it('adds Accept-Language header to every request', async () => {
    const request = new Request('https://api.com')
    await callMiddleware(request)
    expect(request.headers.get('Accept-Language')).toMatch(/en/)
  })

  it('does not add Authorization header when not authenticated', async () => {
    keycloak.token = null as any
    const request = new Request('https://api.com')
    await callMiddleware(request)
    expect(request.headers.get('Authorization')).toBeNull()
  })

  it('adds Authorization header when authenticated', async () => {
    keycloak.token = 'valid-token' as any
    vi.mocked(keycloak.isTokenExpired).mockReturnValue(false)

    const request = new Request('https://api.com')
    await callMiddleware(request)

    expect(request.headers.get('Authorization')).toBe('Bearer valid-token')
    expect(keycloak.updateToken).not.toHaveBeenCalled()
  })

  it('refreshes token if it is about to expire', async () => {
    keycloak.token = 'old-token' as any
    vi.mocked(keycloak.isTokenExpired).mockReturnValue(true)
    vi.mocked(keycloak.updateToken).mockImplementation(async () => {
      keycloak.token = 'new-token' as any
      return true
    })

    const request = new Request('https://api.com')
    await callMiddleware(request)

    expect(keycloak.updateToken).toHaveBeenCalledWith(5)
    expect(request.headers.get('Authorization')).toBe('Bearer new-token')
  })

  it('handles language changes correctly', async () => {
    ;(i18n as any).language = 'cs'
    const request = new Request('https://api.com')
    await callMiddleware(request)
    expect(request.headers.get('Accept-Language')).toBe('cs')
  })
})
