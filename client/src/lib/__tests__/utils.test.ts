import { describe, expect, it } from 'vitest'

import { cn, formatDate } from '../utils'

describe('utils', () => {
  describe('cn', () => {
    it('merges classes correctly', () => {
      expect(cn('c1', 'c2')).toBe('c1 c2')
      expect(cn('c1', { c2: true, c3: false })).toBe('c1 c2')
    })

    it('handles tailwind conflicts', () => {
      expect(cn('p-4', 'p-2')).toBe('p-2')
      expect(cn('text-red-500', 'text-blue-500')).toBe('text-blue-500')
    })
  })

  describe('formatDate', () => {
    it('formats date correctly for cs locale', () => {
      const date = new Date('2023-01-01')
      expect(formatDate(date, 'cs')).toBe('1. 1. 2023')
    })

    it('formats date correctly for en locale', () => {
      const date = new Date('2023-01-01')
      // US locale format is usually M/D/YYYY
      expect(formatDate(date, 'en')).toBe('1/1/2023')
    })
  })
})
