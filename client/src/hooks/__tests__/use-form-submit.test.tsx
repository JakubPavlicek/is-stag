import { useQueryClient } from '@tanstack/react-query'
import { useRouter } from '@tanstack/react-router'
import { renderHook } from '@testing-library/react'
import { toast } from 'sonner'
import { beforeEach, describe, expect, it, vi } from 'vitest'

import { useFormSubmit } from '../use-form-submit'

vi.mock('react-i18next', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
}))

vi.mock('@tanstack/react-query', () => ({
  useQueryClient: vi.fn(),
}))

vi.mock('@tanstack/react-router', () => ({
  useRouter: vi.fn(),
}))

vi.mock('sonner', () => ({
  toast: {
    promise: vi.fn((promise) => promise),
  },
}))

describe('useFormSubmit', () => {
  const mockInvalidateQueries = vi.fn()
  const mockInvalidateRouter = vi.fn()

  beforeEach(() => {
    vi.clearAllMocks()
    ;(useQueryClient as any).mockReturnValue({
      invalidateQueries: mockInvalidateQueries,
    })
    ;(useRouter as any).mockReturnValue({
      invalidate: mockInvalidateRouter,
    })
  })

  it('handles successful submission', async () => {
    const { result } = renderHook(() => useFormSubmit())
    const mutationFn = vi.fn().mockResolvedValue({ data: 'success' })
    const onSuccess = vi.fn()

    await result.current.handleSubmit('vars', {
      mutationFn,
      onSuccess,
      invalidateKeys: [['key1']],
    })

    expect(mutationFn).toHaveBeenCalledWith('vars')
    expect(mockInvalidateQueries).toHaveBeenCalledWith({
      queryKey: ['key1'],
      refetchType: 'inactive',
    })
    expect(mockInvalidateRouter).toHaveBeenCalled()
    expect(onSuccess).toHaveBeenCalledWith('success')
    expect(toast.promise).toHaveBeenCalled()
  })

  it('handles error submission', async () => {
    const { result } = renderHook(() => useFormSubmit())
    const error = new Error('fail')
    const mutationFn = vi.fn().mockResolvedValue({ error })
    const onSuccess = vi.fn()

    await expect(
      result.current.handleSubmit('vars', {
        mutationFn,
        onSuccess,
      }),
    ).rejects.toThrow('fail')

    expect(mutationFn).toHaveBeenCalled()
    expect(mockInvalidateQueries).not.toHaveBeenCalled()
    expect(mockInvalidateRouter).not.toHaveBeenCalled()
    expect(onSuccess).not.toHaveBeenCalled()
  })
})
