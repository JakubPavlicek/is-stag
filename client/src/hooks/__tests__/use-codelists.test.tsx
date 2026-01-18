import { renderHook } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'

import { $codelist } from '@/lib/api'

import { useCodelist, useCountries } from '../use-codelists'

vi.mock('react-i18next', () => ({
  useTranslation: () => ({
    i18n: { language: 'cs' },
  }),
}))

vi.mock('@/lib/api', () => ({
  $codelist: {
    useQuery: vi.fn(),
  },
}))

describe('useCodelist', () => {
  it('calls useQuery with correct params', () => {
    renderHook(() => useCodelist('TEST_DOMAIN'))
    expect($codelist.useQuery).toHaveBeenCalledWith('get', '/domains/{domain}', {
      params: { path: { domain: 'TEST_DOMAIN' }, header: { 'Accept-Language': 'cs' } },
    })
  })
})

describe('useCountries', () => {
  it('calls useQuery with correct params', () => {
    renderHook(() => useCountries())
    expect($codelist.useQuery).toHaveBeenCalledWith('get', '/countries', {
      params: { header: { 'Accept-Language': 'cs' } },
    })
  })
})
