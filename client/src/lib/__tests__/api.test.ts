import { describe, expect, it, vi } from 'vitest'

import i18n from '../../i18n'
import { authMiddleware } from '../api'
import { keycloak } from '../auth'

vi.mock('../auth', () => ({
  keycloak: {
    token: null,
    isTokenExpired: vi.fn(),
    updateToken: vi.fn(),
  },
}))

vi.mock('../i18n', () => ({
  default: { language: 'en' },
}))

describe('authMiddleware', () => {
  it('adds Accept-Language header to every request', async () => {
    const request = new Request('https://api.com')
    await authMiddleware.onRequest!({
      request,
      options: {} as any,
      schemaPath: '',
      params: {
        query: undefined,
        header: undefined,
        path: undefined,
        cookie: undefined,
      },
      id: '',
    })

    expect(request.headers.get('Accept-Language')).toMatch(/en/)
  })

  it('does not add Authorization header when not authenticated', async () => {
    ;(keycloak as any).token = null
    const request = new Request('https://api.com')
    await authMiddleware.onRequest!({
      request,
      options: {} as any,
      schemaPath: '',
      params: {
        query: undefined,
        header: undefined,
        path: undefined,
        cookie: undefined,
      },
      id: '',
    })

    expect(request.headers.get('Authorization')).toBeNull()
  })

  it('adds Authorization header when authenticated', async () => {
    ;(keycloak as any).token = 'valid-token'
    ;(keycloak.isTokenExpired as any).mockReturnValue(false)

    const request = new Request('https://api.com')
    await authMiddleware.onRequest!({
      request,
      options: {} as any,
      schemaPath: '',
      params: {
        query: undefined,
        header: undefined,
        path: undefined,
        cookie: undefined,
      },
      id: '',
    })

    expect(request.headers.get('Authorization')).toBe('Bearer valid-token')
    expect(keycloak.updateToken).not.toHaveBeenCalled()
  })

  it('refreshes token if it is about to expire', async () => {
    ;(keycloak as any).token = 'old-token'
    ;(keycloak.isTokenExpired as any).mockReturnValue(true)
    ;(keycloak.updateToken as any).mockImplementation(async () => {
      ;(keycloak as any).token = 'new-token'
    })

    const request = new Request('https://api.com')
    await authMiddleware.onRequest!({
      request,
      options: {} as any,
      schemaPath: '',
      params: {
        query: undefined,
        header: undefined,
        path: undefined,
        cookie: undefined,
      },
      id: '',
    })

    expect(keycloak.updateToken).toHaveBeenCalledWith(5)
    expect(request.headers.get('Authorization')).toBe('Bearer new-token')
  })

  it('handles language changes correctly', async () => {
    ;(i18n as any).language = 'cs'
    const request = new Request('https://api.com')
    await authMiddleware.onRequest!({
      request,
      options: {} as any,
      schemaPath: '',
      params: {
        query: undefined,
        header: undefined,
        path: undefined,
        cookie: undefined,
      },
      id: '',
    })

    expect(request.headers.get('Accept-Language')).toBe('cs')
  })
})
